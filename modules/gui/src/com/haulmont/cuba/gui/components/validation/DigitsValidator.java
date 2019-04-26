/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.google.common.base.Strings;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberValidator;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static com.haulmont.cuba.gui.components.validation.ValidatorHelper.getNumberConstraint;

/**
 * Digits validator checks that value must be a number within accepted range.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value', '$integer' and '$fraction' keys for
 * formatted output.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *    &lt;bean id="cuba_DigitsValidator" class="com.haulmont.cuba.gui.components.validation.DigitsValidator" scope="prototype"/&gt;
 *    </pre>
 * Use {@link BeanLocator} when creating the validator programmatically.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer and String that represents BigDecimal value with current locale
 */
@Component(DigitsValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DigitsValidator<T> extends AbstractValidator<T> {

    public static final String NAME = "cuba_DigitsValidator";

    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    protected int integer;
    protected int fraction;

    /**
     * Constructor with default error message.
     *
     * @param integer  maximum number of integral digits
     * @param fraction maximum number of fractional digits
     */
    public DigitsValidator(int integer, int fraction) {
        this.integer = integer;
        this.fraction = fraction;
    }

    /**
     * Constructor with custom error message. This message can contain '$value', '$integer' and '$fraction' keys for
     * formatted output.
     * <p>
     * Example: "Value '$value' is out of bounds ($integer digits is expected in integer part and $fraction in
     * fractional part)".
     *
     * @param integer  maximum number of integral digits
     * @param fraction maximum number of fractional digits
     * @param message  error message
     */
    public DigitsValidator(int integer, int fraction, String message) {
        this.integer = integer;
        this.fraction = fraction;
        this.message = message;
    }

    /**
     * @param element     'digits' element
     * @param messagePack message pack
     */
    public DigitsValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = element.attributeValue("message");

        String integer = element.attributeValue("integer");
        if (Strings.isNullOrEmpty(integer)) {
            throw new IllegalArgumentException("Integer value is not defined");
        }
        this.integer = Integer.parseInt(integer);

        String fraction = element.attributeValue("fraction");
        if (Strings.isNullOrEmpty(fraction)) {
            throw new IllegalArgumentException("Fraction value is not defined");
        }
        this.fraction = Integer.parseInt(fraction);
    }

    @Inject
    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    /**
     * Sets maximum value inclusive.
     *
     * @param integer maximum number of integral digits
     */
    public void setIntger(int integer) {
        this.integer = integer;
    }

    /**
     * Sets maximum value inclusive.
     *
     * @param fraction maximum number of fractional digits
     */
    public void setFraction(int fraction) {
        this.fraction = fraction;
    }

    /**
     * @return maximum number of integral digits
     */
    public int getIntger() {
        return integer;
    }

    /**
     * @return maximum number of fractional digits
     */
    public int getFraction() {
        return fraction;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberValidator constraint = null;

        if (value instanceof Number) {
            constraint = getNumberConstraint((Number) value);
        } else if (value instanceof String) {
            try {
                Datatype datatype = Datatypes.getNN(BigDecimal.class);
                Locale locale = userSessionSource.getUserSession().getLocale();
                BigDecimal bigDecimal = (BigDecimal) datatype.parse((String) value, locale);
                if (bigDecimal == null) {
                    fireValidationException(value);
                }
                constraint = getNumberConstraint(bigDecimal);
            } catch (ParseException e) {
                throw new ValidationException(e.getLocalizedMessage());
            }
        }

        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("DigitsValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDigits(integer, fraction)) {
            fireValidationException(value);
        }
    }

    protected void fireValidationException(T value) {
        String message = loadMessage();
        if (message == null) {
            message = messages.getMainMessage("validation.constraints.digits");
        }

        String formatMessage = getTemplateErrorMessage(message,
                ParamsMap.of("value", value,
                             "integer", integer,
                             "fraction", fraction));

        throw new ValidationException(formatMessage);
    }
}
