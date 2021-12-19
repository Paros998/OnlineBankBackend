package com.OBS.lab;

import com.OBS.entity.Client;
import java.time.LocalDateTime;

public class Transfer {
    private Long transferId;
    private Float amount;
    private LocalDateTime transferDate;
    private String category;
    private String type;
    private String receiver_sender;
    private String title;
    private String toAccountNumber;

    private Client client;

    public static Transfer createTransfer() {
        return new Transfer();
    }

    public String toString() {
        Long clientId = client == null ? null : client.getClientId();

        return "Transfer: {" +
                "\ntransferId : " + transferId +
                "\namount :" + amount +
                "\ntransferDate :" + transferDate +
                "\ncategory :" + category +
                "\ntype :" + type +
                "\nreceiver_sender :" + receiver_sender +
                "\ntitle :" + title +
                "\ntoAccountNumber :" + toAccountNumber +
                "\nClient: " + clientId +
                "\n}";
    }

    public Transfer setTransferId(Long transferId) {
        this.transferId = transferId;
        return this;
    }

    public Transfer setAmount(Float amount) {
        this.amount = amount;
        return this;
    }

    public Transfer setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
        return this;
    }

    public Transfer setCategory(String category) {
        this.category = category;
        return this;
    }

    public Transfer setType(String type) {
        this.type = type;
        return this;
    }

    public Transfer setReceiver_sender(String receiver_sender) {
        this.receiver_sender = receiver_sender;
        return this;
    }

    public Transfer setTitle(String title) {
        this.title = title;
        return this;
    }

    public Transfer setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
        return this;
    }

    public Transfer setClient(Client client) {
        this.client = client;
        return this;
    }

    public void performTransfer(Client client, com.OBS.entity.Loan clientLoan){
        if(client == null) throw new IllegalStateException("Internal Server Error, Sender not available");
        if(client.getBalance() < clientLoan.getRateAmount()) {
            throw new IllegalStateException("There is insufficient account balance to perform this transaction");
        }

        System.out.println("Transfer performed successfully.");
    }
}
