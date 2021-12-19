package com.OBS.lab;

public abstract class BankFactory {
    public static BankStuff getStuff(String type){
        if (type.equals("Loan")){
            return Loan.createLoan();
        }
//        if(type.equals("Transfer")){
//            return Transfer.createTransfer();
//        }
//
        return null;
    }
}
