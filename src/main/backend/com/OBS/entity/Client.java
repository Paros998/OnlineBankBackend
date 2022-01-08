package com.OBS.entity;

import com.OBS.auth.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long clientId;
    private String email;
    private String fullName;
    private String accountNumber;
    private Float balance;
    private String personalNumber;
    private String identificationNumber;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String homeAddress;
    private String city;
    private String postalCode;
    private String secHomeAddress;
    private String secCity;
    private String secPostalCode;
    private Integer numberOfCreditsCards;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOfCreation;


    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Client(String email,
                  String fullName,
                  String accountNumber,
                  Float balance,
                  String personalNumber,
                  String identificationNumber,
                  LocalDate dateOfBirth,
                  String homeAddress,
                  String city,
                  String postalCode,
                  Integer numberOfCreditsCards,
                  AppUser user) {
        this.email = email;
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.personalNumber = personalNumber;
        this.identificationNumber = identificationNumber;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.numberOfCreditsCards = numberOfCreditsCards;
        this.user = user;
        this.dateOfCreation = LocalDateTime.now();

    }
    public Client(String email,
                  String fullName,
                  String accountNumber,
                  Float balance,
                  String personalNumber,
                  String identificationNumber,
                  LocalDate dateOfBirth,
                  String homeAddress,
                  String city,
                  String postalCode,
                  String secHomeAddress,
                  String secCity,
                  String secPostalCode,
                  Integer numberOfCreditsCards,
                  AppUser user) {
        this.email = email;
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.personalNumber = personalNumber;
        this.identificationNumber = identificationNumber;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.secHomeAddress = secHomeAddress;
        this.secCity = secCity;
        this.secPostalCode = secPostalCode;
        this.numberOfCreditsCards = numberOfCreditsCards;
        this.user = user;
        this.dateOfCreation = LocalDateTime.now();
    }

    public Client() {

    }
}
