package com.dustincode.ecommerce.core.utils.query.filter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Filter class for {@link BigDecimal} type attributes.
 *
 * @see RangeFilter
 */
public class BigDecimalFilter extends RangeFilter<BigDecimal> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for BigDecimalFilter.</p>
     */
    public BigDecimalFilter() {
    }

    /**
     * <p>Constructor for BigDecimalFilter.</p>
     *
     * @param filter a {@link BigDecimalFilter} object.
     */
    public BigDecimalFilter(final BigDecimalFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link BigDecimalFilter} object.
     */
    public BigDecimalFilter copy() {
        return new BigDecimalFilter(this);
    }

}
