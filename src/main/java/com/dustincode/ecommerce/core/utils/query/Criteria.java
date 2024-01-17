package com.dustincode.ecommerce.core.utils.query;

/**
 * Implementation should usually contain fields of Filter instances.
 */
public interface Criteria {

    /**
     * @return a new criteria with copied filters
     */
    Criteria copy();
}
