/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import groovy.text.GStringTemplateEngine;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main class for validators.
 *
 * @param <T> value type
 */
public abstract class AbstractValidator<T> implements Consumer<T> {

    protected Messages messages;
    protected GStringTemplateEngine engine = new GStringTemplateEngine();

    protected String messagePack;
    protected String message;

    /**
     * @return custom error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets custom error message that will be used instead of default message. For error message it uses Groovy string
     * and it is possible to use values in message. Each validator has its own value keys for formatted output. See
     * JavaDocs for specific validator.
     *
     * @param message error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param errorMessage error message
     * @param values       values map
     * @return message with inserted values
     */
    protected String getTemplateErrorMessage(String errorMessage, Map<String, Object> values) {
        if (!Strings.isNullOrEmpty(errorMessage)) {
            StringWriter writer = new StringWriter();
            try {
                engine.createTemplate(errorMessage).make(values).writeTo(writer);
                return writer.toString();
            } catch (ClassNotFoundException | IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return errorMessage;
    }

    @Nullable
    protected String loadMessage() {
        if (!Strings.isNullOrEmpty(message)) {

            return !Strings.isNullOrEmpty(messagePack) ?
                    messages.getTools().loadString(messagePack, message) : message;
        }
        return null;
    }
}
