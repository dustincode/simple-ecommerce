package com.dustincode.ecommerce.core.configs;

import io.swagger.v3.oas.models.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Objects;
import java.util.Optional;

@Component
public class SwaggerPreAuthorize implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);

        if (Objects.nonNull(preAuthorize)) {
            String description = String.format(
                    "%s**Security @PreAuthorize expression:** %s",
                    Optional.ofNullable(operation.getDescription()).map(d -> String.format("%s<br/><br/>", d)).orElse(StringUtils.EMPTY),
                    preAuthorize.value());
            operation.setDescription(description);
        }

        return operation;
    }
}
