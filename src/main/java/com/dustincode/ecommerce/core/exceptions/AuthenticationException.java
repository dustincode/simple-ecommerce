package com.dustincode.ecommerce.core.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class AuthenticationException extends AbstractThrowableProblem {

    public AuthenticationException(String message) {
        super(null, "Unauthorized", Status.UNAUTHORIZED, message);
    }
}
