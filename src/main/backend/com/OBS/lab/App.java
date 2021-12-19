package com.OBS.lab;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Loan newLoan = Loan.createLoan()
                .setBasicLoanAmount(6000.00f)
                .setInitialRatesNumber(15)
                .setConcludedDate(LocalDate.now())
                .setCommission(500f)
                .setYearlyInterestPercent(0.17f)
                .calculateLoan()
                .setActive();
        System.out.println(newLoan.toString());

    }
}
