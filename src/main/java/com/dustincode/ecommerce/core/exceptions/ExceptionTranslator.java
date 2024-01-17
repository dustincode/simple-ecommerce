package com.dustincode.ecommerce.core.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.Violation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

    public static final String BAD_REQUEST_TITLE = "Bad Request";
    public static final String BODY_MISSING_MSG = "Required request body is missing";
    public static final String INTERNAL_ERROR_MSG = "Oops!!! something went wrong please try again or check out our help area";

    @Override
    public ResponseEntity<Problem> process(
            ResponseEntity<Problem> entity,
            @Nonnull NativeWebRequest request
    ) {
        Problem problem = entity.getBody();

        if (Objects.nonNull(problem) && Status.INTERNAL_SERVER_ERROR == problem.getStatus()) {
            Problem newProblem = Problem
                    .builder()
                    .withStatus(problem.getStatus())
                    .withTitle(problem.getTitle())
                    .withDetail(INTERNAL_ERROR_MSG)
                    .build();
            return new ResponseEntity<>(newProblem, entity.getHeaders(), entity.getStatusCode());
        }

        return ProblemHandling.super.process(entity, request);
    }

    @Override
    public ResponseEntity<Problem> newConstraintViolationProblem(
            @Nonnull Throwable throwable,
            Collection<Violation> stream,
            @Nonnull NativeWebRequest request
    ) {
        List<Violation> violations = stream.stream()
                .sorted(Comparator.comparing(Violation::getField).thenComparing(Violation::getMessage))
                .toList();

        Problem problem = Problem
                .builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle(BAD_REQUEST_TITLE)
                .withDetail("Invalid request body")
                .with("violations", violations)
                .build();

        return this.create(throwable, problem, request);
    }

    @Override
    public ResponseEntity<Problem> handleMessageNotReadableException(
            HttpMessageNotReadableException exception,
            @Nonnull NativeWebRequest request
    ) {
        String detail = exception.getMessage().startsWith(BODY_MISSING_MSG) ? BODY_MISSING_MSG : exception.getMessage();
        Problem problem = Problem
                .builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle(BAD_REQUEST_TITLE)
                .withDetail(detail)
                .build();
        return new ResponseEntity<>(problem, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
