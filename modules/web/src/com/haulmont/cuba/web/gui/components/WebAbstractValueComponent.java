/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.web.gui.components;

import com.haulmont.bali.events.Subscription;
import com.haulmont.chile.core.model.utils.InstanceUtils;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.data.ConversionException;
import com.haulmont.cuba.gui.components.data.HasValueSource;
import com.haulmont.cuba.gui.components.data.ValueSource;
import com.haulmont.cuba.gui.components.data.meta.ValueBinding;
import com.haulmont.cuba.gui.components.data.value.ValueBinder;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.gui.sys.UiTestIds;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.WebConfig;
import com.haulmont.cuba.web.gui.ConversionErrorDisplayType;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public abstract class WebAbstractValueComponent<T extends com.vaadin.ui.Component & com.vaadin.data.HasValue<P>, P, V>
        extends WebAbstractComponent<T> implements HasValue<V>, HasValueSource<V> {

    protected V internalValue;
    protected ValueBinding<V> valueBinding;

    @Override
    public void setValueSource(ValueSource<V> valueSource) {
        if (this.valueBinding != null) {
            valueBinding.unbind();

            this.valueBinding = null;
        }

        if (valueSource != null) {
            ValueBinder binder = beanLocator.get(ValueBinder.NAME, ValueBinder.class);

            this.valueBinding = binder.bind(this, valueSource);

            valueBindingConnected(valueSource);

            this.valueBinding.activate();

            valueBindingActivated(valueSource);

            setUiTestId(valueSource);
        }
    }

    protected void setUiTestId(ValueSource<V> valueSource) {
        AppUI ui = AppUI.getCurrent();

        if (ui != null && ui.isTestMode()
                && getComponent().getCubaId() == null) {

            String testId = UiTestIds.getInferredTestId(valueSource);
            if (testId != null) {
                getComponent().setCubaId(testId);
            }
        }
    }

    protected void valueBindingConnected(ValueSource<V> valueSource) {
        // hook
    }

    protected void valueBindingActivated(@SuppressWarnings("unused") ValueSource<V> valueSource) {
        // hook
    }

    @Override
    public ValueSource<V> getValueSource() {
        return valueBinding != null ? valueBinding.getSource() : null;
    }

    @Override
    public V getValue() {
        return internalValue;
    }

    @Override
    public void setValue(V value) {
        setValueToPresentation(convertToPresentation(value));

        V oldValue = internalValue;
        this.internalValue = value;

        if (!fieldValueEquals(value, oldValue)) {
            ValueChangeEvent<V> event = new ValueChangeEvent<>(this, oldValue, value, false);
            publish(ValueChangeEvent.class, event);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscription addValueChangeListener(Consumer<ValueChangeEvent<V>> listener) {
        return getEventHub().subscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeValueChangeListener(Consumer<ValueChangeEvent<V>> listener) {
        unsubscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    protected void setValueToPresentation(P value) {
        if (hasValidationError()) {
            setValidationError(null);
        }

        component.setValue(value);
    }

    protected void componentValueChanged(P prevComponentValue, P newComponentValue, boolean isUserOriginated) {
        if (isUserOriginated) {
            V value;

            try {
                value = convertToModel(newComponentValue);
                P presentationValue = convertToPresentation(value);

                // always update presentation value after change
                // for instance: "1000" entered by user could be "1 000" in case of integer formatting
                setValueToPresentation(presentationValue);
            } catch (ConversionException ce) {
                LoggerFactory.getLogger(getClass()).trace("Unable to convert presentation value to model", ce);

                ConversionErrorDisplayType errorDisplayType = beanLocator.get(Configuration.class)
                        .getConfig(WebConfig.class)
                        .getConversionErrorDisplayType();

                if (errorDisplayType == ConversionErrorDisplayType.EXCEPTION) {
                    setValidationError(ce.getLocalizedMessage());
                } else {
                    Messages messages = beanLocator.get(Messages.NAME);
                    UiControllerUtils.getScreenContext(getFrame().getFrameOwner())
                            .getNotifications()
                            .create(Notifications.NotificationType.TRAY)
                            .withCaption(messages.getMainMessage("validationFail.caption"))
                            .withDescription(ce.getLocalizedMessage())
                            .show();
                }
                
                return;
            }

            V oldValue = internalValue;
            internalValue = value;

            if (!fieldValueEquals(value, oldValue)) {
                ValueChangeEvent<V> event = new ValueChangeEvent<>(this, oldValue, value, isUserOriginated);
                publish(ValueChangeEvent.class, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected V convertToModel(P componentRawValue) throws ConversionException {
        return (V) componentRawValue;
    }

    @SuppressWarnings("unchecked")
    protected P convertToPresentation(V modelValue) throws ConversionException {
        return (P) modelValue;
    }

    protected boolean fieldValueEquals(V value, V oldValue) {
        return InstanceUtils.propertyValueEquals(oldValue, value);
    }

    protected void commit() {
        if (valueBinding != null) {
            valueBinding.write();
        }
    }

    protected void discard() {
        if (valueBinding != null) {
            valueBinding.discard();
        }
    }

    protected boolean isBuffered() {
        return valueBinding != null
                && valueBinding.isBuffered();
    }

    protected void setBuffered(boolean buffered) {
        if (valueBinding != null) {
            valueBinding.setBuffered(buffered);
        }
    }

    protected boolean isModified() {
        return valueBinding != null
                && valueBinding.isModified();
    }
}