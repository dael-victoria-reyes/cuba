/*
 * Copyright (c) 2011 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.cuba.web.toolkit.ui;

import com.haulmont.cuba.web.gui.components.WebDateField;
import com.vaadin.data.Property;
import com.vaadin.ui.Layout;

import java.util.Date;

/**
 * <p>$Id$</p>
 *
 * @author devyatkin
 */
public class DateFieldWrapper extends CustomField {

    private WebDateField dateField;

    public DateFieldWrapper(WebDateField dateField, Layout composition) {
        this.dateField = dateField;
        composition.setWidth("100%");
        setSizeUndefined();
        setCompositionRoot(composition);
    }

    public WebDateField getCubaField() {
        return dateField;
    }

    @Override
    public Object getValue() {
        if (getPropertyDataSource() != null)
            return getPropertyDataSource().getValue();
        return dateField.getValue();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
        if (getPropertyDataSource() != null)
            getPropertyDataSource().setValue(newValue);
        dateField.setValue(newValue);
    }

    public void focus() {
        dateField.getDateField().focus();
    }

    @Override
    public Class<?> getType() {
        return Date.class;
    }

    public void setReadOnly(boolean readOnly) {
        dateField.setEditable(!readOnly);
    }

    public boolean isReadOnly() {
        return !dateField.isEditable();
    }

    public boolean isRequired() {
        return dateField.isRequired();
    }

    public void setRequired(boolean required) {
        dateField.setRequired(required);
        super.setRequired(required);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        super.valueChange(event);
        // support dateField in editable table
        Property property = event.getProperty();
        if (property != null)
            dateField.setValue(property.getValue());
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        // support dateField in editable table
        if (newDataSource != null)
            dateField.setValue(newDataSource.getValue());
    }
}
