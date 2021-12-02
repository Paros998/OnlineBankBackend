package com.OBS.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NameAndPersonalNumBody {
    String fullName;
    String personalNumber;
}
