package com.OBS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime transferDate;
    private String category;
    private String type;
    private String receiver_sender;
    private String title;
    private String toAccountNumber;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "client_id")
    private Client client;

    public Transfer(Float amount,
                    LocalDateTime transferDate,
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
