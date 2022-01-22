package com.OBS.enums;

import com.OBS.auth.entity.AppUser;
import com.OBS.searchers.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;

public enum SearchOperation {

    GREATER_THAN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThan(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    LESS_THAN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThan(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    GREATER_THAN_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    LESS_THAN_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    NOT_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.notEqual(root.get(
                    criteria.getKey()), criteria.getValue()
            );
        }
    },

    EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.equal(root.get(
                    criteria.getKey()), criteria.getValue()
            );
        }
    },

    EQUAL_JOIN_USER {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            Join<T, AppUser> userJoin = root.join("user");
            return builder.equal(
                    userJoin.get(
                            criteria.getKey()
                    ),
                    criteria.getValue()
            );
        }
    },

    MATCH {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey()))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    MATCH_START {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey())),
                    "%" + criteria.getValue().toString().toLowerCase()
            );
        }
    },

    MATCH_END {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey())),
                    criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    IN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.in(
                    root.get(criteria.getKey())
            ).value(criteria.getValue());
        }
    },

    NOT_IN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.not(
                    root.get(criteria.getKey())
            ).in(criteria.getValue());
        }
    },

    EQUAL_NULL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.isNull(root.get(criteria.getKey()));
        }
    },

    NOT_EQUAL_NULL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.isNotNull(root.get(criteria.getKey()));
        }
    },

    GREATER_THAN_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    GREATER_THAN_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThan(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString()));
        }
    },

    LESS_THAN_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    LESS_THAN_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThan(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString()));
        }
    },

    EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.equal(root.get(
                    criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    NOT_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.notEqual(root.get(
                    criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    };

    abstract public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder);
}
