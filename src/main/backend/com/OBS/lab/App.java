package com.OBS.lab;

import java.time.LocalDate;
import java.util.Objects;
import com.OBS.entity.Client;
import com.OBS.enums.TransferCategory;
import com.OBS.enums.TransferType;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        Loan newLoan =((Loan) Objects.requireNonNull(BankFactory.getStuff("Loan")))
                .setBasicLoanAmount(6000.00f)
                .setInitialRatesNumber(15)
                .setConcludedDate(LocalDate.now())
                .setCommission(500f)
                .setYearlyInterestPercent(0.17f)
                .calculateLoan()
                .setActive();
        System.out.println(newLoan.toString());
        Transfer transfer = Transfer.createTransfer()
                .setTransferId(1L)
                .setAmount(15.25f)
                .setTransferDate(LocalDateTime.now())
                .setCategory("Inne")
                .setType(TransferType.OUTGOING.getTyp())
                .setReceiver_sender("Michał Tester")
                .setTitle("Kebab Alibaba")
                .setToAccountNumber("66 6666 6666 6666 6666 6666");
        System.out.println(transfer.toString());
    }

}
