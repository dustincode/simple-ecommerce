package com.dustincode.ecommerce.core.audit;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private static final String ANONYMOUS = "ANONYMOUS";

    private static final String SYSTEM = "SYSTEM";

    @Nonnull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM);
    }
}
