package com.OBS.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long transferId;
    private Float amount;
    private LocalDate transferDate;
    private String category;
    private String type;
    private String receiver_sender;
    private String title;
    private String toAccountNumber;


    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    public Transfer(Float amount,
                    LocalDate transferDate,
                    String category,
                    String type,
                    String receiver_sender,
                    String title,
                    String toAccountNumber) {
        this.amount = amount;
        this.transferDate = transferDate;
        this.category = category;
        this.type = type;
        this.receiver_sender = receiver_sender;
        this.title = title;
        this.toAccountNumber = toAccountNumber;
    }

    public Transfer(){

    }

}
