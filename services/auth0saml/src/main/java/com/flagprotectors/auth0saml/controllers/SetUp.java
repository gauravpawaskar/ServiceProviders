package com.flagprotectors.auth0saml.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.json.JSONObject;

@RestController
public class SetUp {

    @PostMapping(value = "/setup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setUp(@RequestBody Map<String, Object> payload) {
        // System.out.println(payload.toString());
        System.setProperty("client_secret", payload.get("client_secret").toString());
        System.setProperty("client_id", payload.get("client_id").toString());
        JSONObject jsonbject = new JSONObject();
        jsonbject.put("status", "success");

        return ResponseEntity.status(HttpStatus.OK).body(jsonbject.toString());
    }
}
