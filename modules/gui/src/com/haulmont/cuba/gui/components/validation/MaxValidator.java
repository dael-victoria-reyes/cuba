/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberConstraint;

import static com.haulmont.cuba.gui.components.validation.ConstraintHelper.getNumberConstraint;

/**
 * Max validator checks that value must be less than or equal to the specified maximum.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' and '$max' keys for formatted output.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer
 */
public class MaxValidator<T extends Number> extends AbstractValidator<T> {

    protected long max;

    public MaxValidator(long max) {
        this.max = max;
        this.defaultMessage = messages.getMainMessage("validation.constraints.max");
    }

    /**
     * Constructor for custom error message. This message can contain '$value' and '$max' keys for formatted output.
     * <p>
     * Example: "Value '$value' should be less than or equal to '$max'".
     *
     * @param max     max value
     * @param message error message
     */
    public MaxValidator(long max, String message) {
        this.max = max;
        this.message = message;
    }

    /**
     * Sets max value.
     *
     * @param max max value
     * @return current instance
     */
    public MaxValidator<T> withMax(long max) {
        this.max = max;
        return this;
    }

    /**
     * @return max value
     */
    public long getMax() {
        return max;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("MaxValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isMax(max)) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "max", max)));
        }
    }
}
