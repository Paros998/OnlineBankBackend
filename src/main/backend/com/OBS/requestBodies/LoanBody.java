package com.OBS.requestBodies;

import com.OBS.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LoanBody {
    LocalDate concludedDate;
    int initialRatesNumber;
    Float basicLoanAmount;
    Long clientId;

}
