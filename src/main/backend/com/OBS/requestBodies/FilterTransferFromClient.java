package com.OBS.requestBodies;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class FilterTransferFromClient {
    LocalDate dateFrom,dateTo;
    String transferType,transferCategory;
}
