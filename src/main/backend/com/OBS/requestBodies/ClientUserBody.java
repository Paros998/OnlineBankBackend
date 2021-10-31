package com.OBS.requestBodies;

import com.OBS.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientUserBody {
    Client client;
    UserCredentials userCredentials;
}
