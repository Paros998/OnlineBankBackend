package com.OBS.entity;

import com.OBS.auth.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long employeeId;

    private String email;
    private String fullName;
    private String personalNumber;
    private String identificationNumber;
    private LocalDate dateOfBirth;
    private String homeAddress;
    private String city;
    private String postalCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;


    public Employee(AppUser user,
                    String fullName,
                    String personalNumber,
                    String email,
                    String identificationNumber,
                    LocalDate dateOfBirth,
                    String homeAddress,
                    String city,
                    String postalCode) {
        this.user = user;
        this.fullName = fullName;
        this.personalNumber = personalNumber;
        this.email = email;
        this.identificationNumber = identificationNumber;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.city = city;
        this.postalCode = postalCode;
    }

    public Employee() {
    }
}
