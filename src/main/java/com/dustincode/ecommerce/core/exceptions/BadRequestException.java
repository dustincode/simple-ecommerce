package com.dustincode.ecommerce.core.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadRequestException extends AbstractThrowableProblem {

    public BadRequestException(String message) {
        super(null, ExceptionTranslator.BAD_REQUEST_TITLE, Status.BAD_REQUEST, message);
    }
}
