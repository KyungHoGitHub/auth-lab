package com.example.authservice.auth;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthOauthResponse.class, name = "ID-PW"),
        @JsonSubTypes.Type(value = AuthIdPasswordResponse.class, name = "OAUTH")
})
public sealed interface AuthResponse permits AuthOauthResponse, AuthIdPasswordResponse {}
