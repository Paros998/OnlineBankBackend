package com.OBS.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NamePersonalNum_BirthDateBody {
    String personalNumber_personName;
    LocalDate birthDate;
}
