package com.OBS.requestBodies;

import com.OBS.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientUserBody {
    Client client;
    UserCredentials userCredentials;
}
