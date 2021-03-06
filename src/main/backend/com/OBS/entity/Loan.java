package com.OBS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            updatable = false,
            nullable = false
    )
    private Long loanId;
    private Boolean isActive;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate concludedDate;
    private int initialRatesNumber;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate estimatedEndDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate nextRatePayDay;
    private int numOfRates;
    private Float yearlyInterestPercent;
    private Float rateAmount;
    private Float basicRateAmount;
    private Float basicLoanAmount;
    private Float commission;
    private Float interestAmount;
    private Float penaltyAmount;
    private Float totalPaidOff;
    private Float toRepaidOff;
    private int ratesLeftToPay;


    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "client_id")
    private Client client;

    @Transient
    public static final Float basicCommission = 0.1f;
    @Transient
    public static final Float yearlyRRSO = 0.175f;

    public Loan(LocalDate concludedDate,
                int initialRatesNumber,
                Float basicLoanAmount,
                Client client) {
        isActive = true;
        this.concludedDate = concludedDate;
        this.initialRatesNumber = initialRatesNumber;
        this.estimatedEndDate = concludedDate.plusMonths(initialRatesNumber);
        this.nextRatePayDay = concludedDate.plusMonths(1);
        this.numOfRates = initialRatesNumber;
        this.yearlyInterestPercent = yearlyRRSO * 100;
        this.basicLoanAmount = basicLoanAmount;
        this.commission = basicLoanAmount * basicCommission;
        this.penaltyAmount = 0f;
        this.totalPaidOff = 0f;
        this.ratesLeftToPay = initialRatesNumber;
        this.toRepaidOff = basicLoanAmount;

        if(initialRatesNumber > 12)
            for(int i = 0; i < initialRatesNumber / 12 ; i++){
                toRepaidOff += toRepaidOff * yearlyRRSO;
            }

        float monthlyPercent = yearlyRRSO / 12;

        for(int i = 0; i < initialRatesNumber % 12 ; i++){
            toRepaidOff += toRepaidOff * monthlyPercent;
        }

        toRepaidOff += commission;

        interestAmount = toRepaidOff - basicLoanAmount;

        this.rateAmount = toRepaidOff / initialRatesNumber;
        this.basicRateAmount = this.rateAmount;

        this.client = client;
    }

    public Loan() {

    }
}
