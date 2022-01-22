package com.OBS.lab;


import com.OBS.searchers.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public abstract class ImplementedSpecification<T> implements Specification<T> {
    protected List<SearchCriteria> searchCriteriaList;

    protected List<SearchCriteria> getSearchCriteriaList(){
        return searchCriteriaList;
    }

    protected ImplementedSpecification() {
        this.searchCriteriaList = new ArrayList<>();
    }
    protected ImplementedSpecification(ArrayList<SearchCriteria> list) {
        this.searchCriteriaList = new ArrayList<>(list);
    }

    protected void onlyAdd(SearchCriteria criteria){
        this.searchCriteriaList.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaList)
           predicates.add(criteria.getOperation().getPredicate(root,criteria,builder));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
