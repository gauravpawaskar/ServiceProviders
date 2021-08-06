package com.flagprotectors.auth0saml.process;

import org.springframework.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessLogin {

    @PostMapping(value = "//processlogin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity processLogin() {
        JSONObject jsonbject = new JSONObject();
        jsonbject.put("status", "success");

        return ResponseEntity.status(HttpStatus.OK).body(jsonbject.toString());
    }

}
