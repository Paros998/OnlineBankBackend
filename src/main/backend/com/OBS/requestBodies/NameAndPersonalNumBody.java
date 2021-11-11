package com.OBS.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NameAndPersonalNumBody {
    String fullName;
    String personalNumber;
}
