package com.OBS.searchers.specificators;


import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Specifications<T> implements Specification<T>, Cloneable {

    private final List<SearchCriteria> searchCriteriaList;

    public Specifications() {
        this.searchCriteriaList = new ArrayList<>();
    }

    public Specifications(ArrayList<SearchCriteria> list) {
        this.searchCriteriaList = new ArrayList<>(list);
    }

    public void onlyAdd(SearchCriteria searchCriteria) {
        this.searchCriteriaList.add(searchCriteria);
    }

    public Specifications<T> add(SearchCriteria searchCriteria) {
        searchCriteriaList.add(searchCriteria);
        return this;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaList) {
            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                predicates.add(
                        builder.greaterThan(
                                root.get(criteria.getKey()),
                                criteria.getValue().toString())
                );
            }else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                predicates.add(
                        builder.lessThan(
                                root.get(criteria.getKey()),
                                criteria.getValue().toString())
                );
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_DATE)) {
                predicates.add(
                        builder.greaterThan(
                                root.get(criteria.getKey()),
                                LocalDateTime.parse(criteria.getValue().toString()))
                );
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_DATE)) {
                predicates.add(
                        builder.lessThan(
                                root.get(criteria.getKey()),
                                LocalDateTime.parse(criteria.getValue().toString()))
                );
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                predicates.add(
                        builder.greaterThanOrEqualTo(
                                root.get(criteria.getKey()),
                                criteria.getValue().toString()
                        )
                );
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL_DATE)) {
                predicates.add(
                        builder.greaterThanOrEqualTo(
                                root.get(criteria.getKey()),
                                LocalDateTime.parse(criteria.getValue().toString())
                        )
                );
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                predicates.add(
                        builder.lessThanOrEqualTo(
                                root.get(criteria.getKey()),
                                criteria.getValue().toString()
                        ));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL_DATE)) {
                predicates.add(
                        builder.lessThanOrEqualTo(
                                root.get(criteria.getKey()),
                                LocalDateTime.parse(criteria.getValue().toString())
                        ));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(
                        builder.notEqual(root.get(
                                criteria.getKey()), criteria.getValue())
                );
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL_DATE)) {
                predicates.add(
                        builder.notEqual(root.get(
                                criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString()))
                );
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL_NULL)) {
                predicates.add(
                        builder.isNotNull(root.get(criteria.getKey()))
                );
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(
                        builder.equal(root.get(
                                criteria.getKey()), criteria.getValue())
                );
            }  else if (criteria.getOperation().equals(SearchOperation.EQUAL_DATE)) {
                predicates.add(
                        builder.equal(root.get(
                                criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString()))
                );
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL_NULL)) {
                predicates.add(
                        builder.isNull(root.get(criteria.getKey())));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                predicates.add(
                        builder.like(
                                builder.lower(root.get(criteria.getKey()))
                                , "%" + criteria.getValue().toString().toLowerCase() + "%")
                );
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                predicates.add(
                        builder.like(
                                builder.lower(root.get(criteria.getKey())),
                                criteria.getValue().toString().toLowerCase() + "%")
                );
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                predicates.add
                        (builder.like(
                                builder.lower(root.get(criteria.getKey())),
                                "%" + criteria.getValue().toString().toLowerCase())
                        );
            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                predicates.add(
                        builder.in(
                                root.get(criteria.getKey())
                        ).value(criteria.getValue())
                );
            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                predicates.add(
                        builder.not(
                                root.get(criteria.getKey())
                        ).in(criteria.getValue()));
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Specifications<T> clone() {
        return new Specifications<>(new ArrayList<>(searchCriteriaList));

    }
}
