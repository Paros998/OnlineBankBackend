package com.OBS.entity;

import com.OBS.auth.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


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


    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
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

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
