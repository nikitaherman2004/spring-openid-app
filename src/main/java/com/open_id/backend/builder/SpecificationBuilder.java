package com.open_id.backend.builder;

import com.open_id.backend.entity.AppUser;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import static java.util.Objects.isNull;

public class SpecificationBuilder {

    private Specification<AppUser> socksSpecification;

    public SpecificationBuilder andEqual(Object value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<AppUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.equal(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public SpecificationBuilder andLessThen(Integer value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<AppUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.lessThan(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public SpecificationBuilder andGreaterThan(Integer value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<AppUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.greaterThan(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public SpecificationBuilder andEqual(Integer value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<AppUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.equal(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public <T extends Comparable<? super T>> SpecificationBuilder andBetween(T startValue, T endValue, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<AppUser> specification = (root, query, criteriaBuilder) ->
                isNull(startValue) || isNull(endValue)
                        ? null
                        : criteriaBuilder.between(root.get(fieldName), startValue, endValue);

        return createOrUpdateSpecification(specification);
    }

    private SpecificationBuilder createOrUpdateSpecification(Specification<AppUser> specification) {
        if (this.socksSpecification == null) {
            this.socksSpecification = Specification.where(specification);
        } else {
            this.socksSpecification = socksSpecification.and(specification);
        }

        return this;
    }

    public Specification<AppUser> build() {
        Specification<AppUser> temporarySpecification = this.socksSpecification;
        this.socksSpecification = null;

        return temporarySpecification;
    }
}

