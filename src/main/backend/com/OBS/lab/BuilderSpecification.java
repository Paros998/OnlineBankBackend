package com.OBS.lab;

import com.OBS.searchers.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

public class BuilderSpecification<T> extends ImplementedSpecification<T> implements Cloneable {
    public BuilderSpecification(){super();}
    protected BuilderSpecification(ArrayList<SearchCriteria> list) {
        super(list);
    }

    public BuilderSpecification<T> add(SearchCriteria searchCriteria){
        super.onlyAdd(searchCriteria);
        return this;
    }

    protected BuilderSpecification<T> add(List<SearchCriteria> searchCriteriaList){
        onlyAdd(searchCriteriaList);
        return this;
    }

    protected void onlyAdd(List<SearchCriteria> searchCriteriaList){
        this.searchCriteriaList.addAll(searchCriteriaList);
    }

    public BuilderSpecification<T> clone(boolean shallow) throws CloneNotSupportedException {
        if(shallow)
            return new BuilderSpecification<T>(new ArrayList<>(searchCriteriaList));
        return (BuilderSpecification<T>) super.clone();
    }
}
