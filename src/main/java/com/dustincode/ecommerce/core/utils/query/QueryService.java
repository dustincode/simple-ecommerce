package com.dustincode.ecommerce.core.utils.query;

import com.dustincode.ecommerce.core.utils.query.filter.Filter;
import com.dustincode.ecommerce.core.utils.query.filter.RangeFilter;
import com.dustincode.ecommerce.core.utils.query.filter.StringFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Base service for constructing and executing complex queries.
 *
 * @param <E> the type of the E which is queried.
 */
@Transactional(readOnly = true)
public abstract class QueryService<E> {

    /**
     * Helper function to return a specification for filtering on a single field, where equality, and null/non-null
     * conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @param <X>    The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X> Specification<E> buildSpecification(Filter<X> filter, SingularAttribute<? super E, X>
        field) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a single field, where equality, and null/non-null
     * conditions are supported.
     *
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaclassFunction the function, which navigates from the current E to a column, for which the filter applies.
     * @param <X>               The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X> Specification<E> buildSpecification(Filter<X> filter, Function<Root<E>, Expression<X>> metaclassFunction) {
        if (filter.getEquals() != null) {
            return equalsSpecification(metaclassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaclassFunction, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(metaclassFunction, filter.getNotIn());
        } else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(metaclassFunction, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(metaclassFunction, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a {@link String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected Specification<E> buildStringSpecification(StringFilter filter, SingularAttribute<? super E, String> field) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a {@link String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaClassFunction lambda, which based on a Root&lt;E&gt; returns Expression - basically picks a column
     * @return a Specification
     */
    protected Specification<E> buildSpecification(StringFilter filter, Function<Root<E>, Expression<String>> metaClassFunction) {
        if (filter.getEquals() != null) {
            return equalsSpecification(metaClassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaClassFunction, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(metaClassFunction, filter.getNotIn());
        } else if (filter.getContains() != null) {
            return likeUpperSpecification(metaClassFunction, filter.getContains());
        } else if (filter.getDoesNotContain() != null) {
            return doesNotContainSpecification(metaClassFunction, filter.getDoesNotContain());
        } else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(metaClassFunction, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(metaClassFunction, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a single {@link Comparable}, where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported.
     *
     * @param <X>    The type of the attribute which is filtered.
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected <X extends Comparable<? super X>> Specification<E> buildRangeSpecification(
            RangeFilter<X> filter,
            SingularAttribute<? super E, X> field
    ) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a single {@link Comparable}, where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported.
     *
     * @param <X>               The type of the attribute which is filtered.
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaClassFunction lambda, which based on a Root&lt;E&gt; returns Expression - basically picks a column
     * @return a Specification
     */
    protected <X extends Comparable<? super X>> Specification<E> buildSpecification(
            RangeFilter<X> filter,
            Function<Root<E>, Expression<X>> metaClassFunction
    ) {
        if (filter.getEquals() != null) {
            return equalsSpecification(metaClassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaClassFunction, filter.getIn());
        }

        Specification<E> result = Specification.where(null);
        if (filter.getSpecified() != null) {
            result = result.and(byFieldSpecified(metaClassFunction, filter.getSpecified()));
        }
        if (filter.getNotEquals() != null) {
            result = result.and(notEqualsSpecification(metaClassFunction, filter.getNotEquals()));
        }
        if (filter.getNotIn() != null) {
            result = result.and(valueNotIn(metaClassFunction, filter.getNotIn()));
        }
        if (filter.getGreaterThan() != null) {
            result = result.and(greaterThan(metaClassFunction, filter.getGreaterThan()));
        }
        if (filter.getGreaterThanOrEqual() != null) {
            result = result.and(greaterThanOrEqualTo(metaClassFunction, filter.getGreaterThanOrEqual()));
        }
        if (filter.getLessThan() != null) {
            result = result.and(lessThan(metaClassFunction, filter.getLessThan()));
        }
        if (filter.getLessThanOrEqual() != null) {
            result = result.and(lessThanOrEqualTo(metaClassFunction, filter.getLessThanOrEqual()));
        }
        return result;
    }

    /**
     * Helper function to return a specification for filtering on one-to-one or many-to-one reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByProjectId = buildReferringESpecification(criteria.getProjectId(),
     * Employee_.project, Project_.id);
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringESpecification(criteria.getProjectName(),
     * Employee_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if nullness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring E.
     * @param valueField the attribute of the static metamodel of the referred E, where the equality should be
     *                   checked.
     * @param <O>    The type of the referenced E.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <O, X> Specification<E> buildReferringESpecification(
            Filter<X> filter,
            SingularAttribute<? super E, O> reference,
            SingularAttribute<? super O, X> valueField
    ) {
        return buildSpecification(filter, root -> root.get(reference).get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-one or many-to-one reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByProjectId = buildReferringESpecification(criteria.getProjectId(),
     * Employee_.project, Project_.id);
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringESpecification(criteria.getProjectName(),
     * Employee_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if nullness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring E.
     * @param valueField the attribute of the static metamodel of the referred E, where the equality should be
     *                   checked.
     * @param <O>    The type of the referenced E.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <O, X> Specification<E> buildReferringEStringSpecification(
            StringFilter filter,
            SingularAttribute<? super E, O> reference,
            SingularAttribute<? super O, String> valueField
    ) {
        return buildSpecification(filter, root -> root.get(reference).get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringESpecification(criteria.getEmployeId(),
     * Project_.employees, Employee_.id);
     *   Specification&lt;Employee&gt; specByEmployeeName = buildReferringESpecification(criteria.getEmployeName(),
     * Project_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if emptiness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring E.
     * @param valueField the attribute of the static metamodel of the referred E, where the equality should be
     *                   checked.
     * @param <O>    The type of the referenced E.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <O, X> Specification<E> buildReferringESpecification(
            Filter<X> filter,
            SetAttribute<E, O> reference,
            SingularAttribute<O, X> valueField
    ) {
        return buildReferringESpecification(filter, root -> root.join(reference), E -> E.get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Usage:<pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringESpecification(
     *          criteria.getEmployeId(),
     *          root -&gt; root.get(Project_.company).join(Company_.employees),
     *          E -&gt; E.get(Employee_.id));
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringESpecification(
     *          criteria.getProjectName(),
     *          root -&gt; root.get(Project_.project)
     *          E -&gt; E.get(Project_.name));
     * </pre>
     *
     * @param filter           the filter object which contains a value, which needs to match or a flag if emptiness is
     *                         checked.
     * @param functionToE the function, which joins he current E to the E set, on which the filtering is applied.
     * @param EToColumn   the function, which of the static metamodel of the referred E, where the equality should be
     *                         checked.
     * @param <O>          The type of the referenced E.
     * @param <M>           The type of the E which is the last before the O in the chain.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <O, M, X> Specification<E> buildReferringESpecification(
            Filter<X> filter,
            Function<Root<E>, SetJoin<M, O>> functionToE,
            Function<SetJoin<M, O>, Expression<X>> EToColumn
    ) {
        if (filter.getEquals() != null) {
            return equalsSpecification(functionToE.andThen(EToColumn), filter.getEquals());
        } else if (filter.getSpecified() != null) {
            // Interestingly, 'functionToE' doesn't work, we need the longer lambda formula
            return byFieldSpecified(functionToE::apply, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringESpecification(criteria.getEmployeId(),
     * Project_.employees, Employee_.id);
     *   Specification&lt;Employee&gt; specByEmployeeName = buildReferringESpecification(criteria.getEmployeName(),
     * Project_.project, Project_.name);
     * </pre>
     *
     * @param <X>        The type of the attribute which is filtered.
     * @param filter     the filter object which contains a value, which needs to match or a flag if emptiness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring E.
     * @param valueField the attribute of the static metamodel of the referred E, where the equality should be
     *                   checked.
     * @param <O>    The type of the referenced E.
     * @return a Specification
     */
    protected <O, X extends Comparable<? super X>> Specification<E> buildReferringESpecification(
            final RangeFilter<X> filter,
            final SetAttribute<E, O> reference,
            final SingularAttribute<O, X> valueField
    ) {
        return buildReferringESpecification(filter, root -> root.join(reference), E -> E.get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported. Usage:
     * <pre><code>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringESpecification(
     *          criteria.getEmployeId(),
     *          root -&gt; root.get(Project_.company).join(Company_.employees),
     *          E -&gt; E.get(Employee_.id));
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringESpecification(
     *          criteria.getProjectName(),
     *          root -&gt; root.get(Project_.project)
     *          E -&gt; E.get(Project_.name));
     * </code>
     * </pre>
     *
     * @param <X>              The type of the attribute which is filtered.
     * @param filter           the filter object which contains a value, which needs to match or a flag if emptiness is
     *                         checked.
     * @param functionToE the function, which joins he current E to the E set, on which the filtering is applied.
     * @param EToColumn   the function, which of the static metamodel of the referred E, where the equality should be
     *                         checked.
     * @param <O>          The type of the referenced E.
     * @param <M>           The type of the E which is the last before the O in the chain.
     * @return a Specification
     */
    protected <O, M, X extends Comparable<? super X>> Specification<E> buildReferringESpecification(
            final RangeFilter<X> filter,
            Function<Root<E>, SetJoin<M, O>> functionToE,
            Function<SetJoin<M, O>, Expression<X>> EToColumn
    ) {

        Function<Root<E>, Expression<X>> fused = functionToE.andThen(EToColumn);
        if (filter.getEquals() != null) {
            return equalsSpecification(fused, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(fused, filter.getIn());
        }
        Specification<E> result = Specification.where(null);
        if (filter.getSpecified() != null) {
            // Interestingly, 'functionToE' doesn't work, we need the longer lambda formula
            result = result.and(byFieldSpecified(functionToE::apply, filter.getSpecified()));
        }
        if (filter.getNotEquals() != null) {
            result = result.and(notEqualsSpecification(fused, filter.getNotEquals()));
        }
        if (filter.getNotIn() != null) {
            result = result.and(valueNotIn(fused, filter.getNotIn()));
        }
        if (filter.getGreaterThan() != null) {
            result = result.and(greaterThan(fused, filter.getGreaterThan()));
        }
        if (filter.getGreaterThanOrEqual() != null) {
            result = result.and(greaterThanOrEqualTo(fused, filter.getGreaterThanOrEqual()));
        }
        if (filter.getLessThan() != null) {
            result = result.and(lessThan(fused, filter.getLessThan()));
        }
        if (filter.getLessThanOrEqual() != null) {
            result = result.and(lessThanOrEqualTo(fused, filter.getLessThanOrEqual()));
        }
        return result;
    }

    /**
     * Generic method, which based on a Root&lt;E&gt; returns an Expression which type is the same as the given 'value' type.
     *
     * @param metaClassFunction function which returns the column which is used for filtering.
     * @param value             the actual value to filter for.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification.
     */
    protected <X> Specification<E> equalsSpecification(
            Function<Root<E>, Expression<X>> metaClassFunction,
            final X value
    ) {
        return (root, query, builder) -> builder.equal(metaClassFunction.apply(root), value);
    }

    /**
     * Generic method, which based on a Root&lt;E&gt; returns an Expression which type is the same as the given 'value' type.
     *
     * @param metaclassFunction function which returns the column which is used for filtering.
     * @param value             the actual value to exclude for.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification.
     */
    protected <X> Specification<E> notEqualsSpecification(Function<Root<E>, Expression<X>> metaclassFunction, final X value) {
        return (root, query, builder) -> builder.not(builder.equal(metaclassFunction.apply(root), value));
    }

    /**
     * <p>likeUpperSpecification.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a {@link String} object.
     * @return a {@link Specification} object.
     */
    protected Specification<E> likeUpperSpecification(Function<Root<E>, Expression<String>> metaclassFunction,
                                                           final String value) {
        return (root, query, builder) -> builder.like(builder.upper(metaclassFunction.apply(root)), wrapLikeQuery(value));
    }

    /**
     * <p>doesNotContainSpecification.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a {@link String} object.
     * @return a {@link Specification} object.
     */
    protected Specification<E> doesNotContainSpecification(Function<Root<E>, Expression<String>> metaclassFunction,
                                                           final String value) {
        return (root, query, builder) -> builder.not(builder.like(builder.upper(metaclassFunction.apply(root)), wrapLikeQuery(value)));
    }

    /**
     * <p>byFieldSpecified.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param specified a boolean.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X> Specification<E> byFieldSpecified(Function<Root<E>, Expression<X>> metaclassFunction,
                                                         final boolean specified) {
        return specified ?
            (root, query, builder) -> builder.isNotNull(metaclassFunction.apply(root)) :
            (root, query, builder) -> builder.isNull(metaclassFunction.apply(root));
    }

    /**
     * <p>byFieldEmptiness.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param specified a boolean.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X> Specification<E> byFieldEmptiness(
            Function<Root<E>, Expression<Set<X>>> metaclassFunction,
            final boolean specified
    ) {
        return specified ?
            (root, query, builder) -> builder.isNotEmpty(metaclassFunction.apply(root)) :
            (root, query, builder) -> builder.isEmpty(metaclassFunction.apply(root));
    }

    /**
     * <p>valueIn.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param values a {@link Collection} object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X> Specification<E> valueIn(Function<Root<E>, Expression<X>> metaclassFunction,
                                                final Collection<X> values) {
        return (root, query, builder) -> {
            CriteriaBuilder.In<X> in = builder.in(metaclassFunction.apply(root));
            for (X value : values) {
                in = in.value(value);
            }
            return in;
        };
    }

    /**
     * <p>valueNotIn.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param values a {@link Collection} object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X> Specification<E> valueNotIn(Function<Root<E>, Expression<X>> metaclassFunction,
                                                   final Collection<X> values) {
        return (root, query, builder) -> {
            CriteriaBuilder.In<X> in = builder.in(metaclassFunction.apply(root));
            for (X value : values) {
                in = in.value(value);
            }
            return builder.not(in);
        };
    }

    /**
     * <p>greaterThanOrEqualTo.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<E> greaterThanOrEqualTo(Function<Root<E>, Expression<X>> metaclassFunction,
                                                                                           final X value) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(metaclassFunction.apply(root), value);
    }

    /**
     * <p>greaterThan.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<E> greaterThan(Function<Root<E>, Expression<X>> metaclassFunction,
                                                                                  final X value) {
        return (root, query, builder) -> builder.greaterThan(metaclassFunction.apply(root), value);
    }

    /**
     * <p>lessThanOrEqualTo.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<E> lessThanOrEqualTo(Function<Root<E>, Expression<X>> metaclassFunction,
                                                                                        final X value) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(metaclassFunction.apply(root), value);
    }

    /**
     * <p>lessThan.</p>
     *
     * @param metaclassFunction a {@link Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<E> lessThan(Function<Root<E>, Expression<X>> metaclassFunction,
                                                                               final X value) {
        return (root, query, builder) -> builder.lessThan(metaclassFunction.apply(root), value);
    }

    /**
     * <p>wrapLikeQuery.</p>
     *
     * @param txt a {@link String} object.
     * @return a {@link String} object.
     */
    protected String wrapLikeQuery(String txt) {
        return "%" + txt.toUpperCase() + '%';
    }

    /**
     * <p>distinct.</p>
     *
     * @param distinct a boolean.
     * @return a {@link Specification} object.
     */
    protected Specification<E> distinct(boolean distinct) {
        return (root, query, cb) -> {
            query.distinct(distinct);
            return null;
        };
    }

}
