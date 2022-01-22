package com.OBS;

import com.OBS.entity.Transfer;
import com.OBS.enums.SearchOperation;
import com.OBS.lab.BuilderSpecification;
import com.OBS.lab.ImplementedSpecification;
import com.OBS.searchers.SearchCriteria;

import java.time.LocalDateTime;

public class Tester {
    public static void main(String[] args) {
        Transfer transfer = new Transfer();
        transfer.setTransferDate(LocalDateTime.now());
        ImplementedSpecification<Transfer> specification;
        BuilderSpecification<Transfer> transferBuilderSpecification;


        SearchCriteria sc = new SearchCriteria(
                "transferDate",
                LocalDateTime.now().minusMonths(1),
                SearchOperation.GREATER_THAN);

        System.out.println(sc.getValue());
        System.out.println(sc.getValue().toString());
        System.out.println(sc.getValue().equals(sc.getValue().toString()));
    }
}
