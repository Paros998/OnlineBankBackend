package com.OBS.lab;

import com.OBS.entity.Client;

import java.time.LocalDate;

public class Loan implements BankStuff{
    private Boolean isActive;
    private LocalDate concludedDate;
    private int initialRatesNumber;
    private LocalDate estimatedEndDate;
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
    private Client client;

    public String toString() {

        Long clientId = client == null ? null : client.getClientId();

        return "Loan:{" +
                "\nisActive :" + isActive +
                "\nconcludedDate :" + concludedDate +
                "\ninitialRatesNumber :" + initialRatesNumber +
                "\nestimatedEndDate :" + estimatedEndDate +
                "\nnextRatePayDay :" + nextRatePayDay +
                "\nnumOfRates :" + numOfRates +
                "\nyearlyInterestPercent :" + yearlyInterestPercent +
                "\nrateAmount :" + rateAmount +
                "\nbasicRateAmount :" + basicRateAmount +
                "\nbasicLoanAmount :" + basicLoanAmount +
                "\ncommission :" + commission +
                "\ninterestAmount :" + interestAmount +
                "\npenaltyAmount :" + penaltyAmount +
                "\ntotalPaidOff :" + totalPaidOff +
                "\ntoRepaidOff :" + toRepaidOff +
                "\nratesLeftToPay :" + ratesLeftToPay +
                "\nClient: " + clientId +
                "\n}";
    }

    public Loan setBasicLoanAmount(Float basicLoanAmount) {
        this.basicLoanAmount = basicLoanAmount;
        return this;
    }

    public Loan setInitialRatesNumber(int initialRatesNumber) {
        this.initialRatesNumber = initialRatesNumber;
        this.numOfRates = initialRatesNumber;
        return this;
    }

    public Loan setConcludedDate(LocalDate concludedDate) {
        this.concludedDate = concludedDate;
        this.nextRatePayDay = concludedDate.plusMonths(1);
        if (this.initialRatesNumber > 0)
            this.estimatedEndDate = concludedDate.plusMonths(initialRatesNumber);
        return this;
    }

    public Loan setYearlyInterestPercent(Float yearlyInterestPercent) {
        this.yearlyInterestPercent = yearlyInterestPercent;
        return this;
    }

    public Loan calculateLoan(){
        if(yearlyInterestPercent != null && basicLoanAmount != null && initialRatesNumber > 0 && concludedDate != null){
            this.toRepaidOff = basicLoanAmount;
            this.ratesLeftToPay = initialRatesNumber;
            this.penaltyAmount = 0f;
            this.totalPaidOff = 0f;

            for(int i = 0; i < initialRatesNumber%12 ; i++){
                toRepaidOff += toRepaidOff * yearlyInterestPercent;
            }
            toRepaidOff += (initialRatesNumber - (12 * (initialRatesNumber%12))) * (yearlyInterestPercent / 12) * toRepaidOff;
            toRepaidOff += commission;

            interestAmount = toRepaidOff - basicLoanAmount;

            this.rateAmount = toRepaidOff / initialRatesNumber;
            this.basicRateAmount = this.rateAmount;
        }
        return this;
    }

    public Loan setCommission(Float commission){
        this.commission = commission;
        return this;
    }

    public Loan setClient(Client client){
        this.client = client;
        return this;
    }

    public Loan setActive(){
        this.isActive = true;
        return this;
    }
    public Loan setInActive(){
        this.isActive = false;
        return this;
    }

    public static Loan createLoan() {
        return new Loan();
    }

    @Override
    public String draw() {
        return toString();
    }
}
