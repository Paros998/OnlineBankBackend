package com.OBS.requestBodies;

import com.OBS.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanBody {
    LocalDate concludedDate;
    int initialRatesNumber;
    Float basicLoanAmount;
    Long clientId;

}
