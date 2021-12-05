package com.OBS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime reTransferDate;
    private String category;
    private String receiver;
    private String accountNumber;
    private String title;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)

    @JoinColumn(name = "client_id")
    private Client client;

    public CyclicalTransfer(){}

}
