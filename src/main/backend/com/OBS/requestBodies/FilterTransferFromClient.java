package com.OBS.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FilterTransferFromClient {
    LocalDate dateFrom,dateTo;
    String transferType,transferCategory;
}
