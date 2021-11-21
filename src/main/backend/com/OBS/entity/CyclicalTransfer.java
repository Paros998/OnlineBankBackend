package com.OBS.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "cyclical_transfers")
public class CyclicalTransfer {
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
    private LocalDate reTransferDate;
    private String category;
    private String receiver;
    private String accountNumber;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    public CyclicalTransfer(){}

}
