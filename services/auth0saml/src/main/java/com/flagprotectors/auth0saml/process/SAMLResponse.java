package com.flagprotectors.auth0saml.process;

public class SAMLResponse {
    private String SAMLResponse;
    private String RelayState;

    public String getSAMLResponse() {
        return SAMLResponse;
    }

    public void setSAMLResponse(String sAMLResponse) {
        SAMLResponse = sAMLResponse;
    }

    public String getRelayState() {
        return RelayState;
    }

    public void setRelayState(String relayState) {
        RelayState = relayState;
    }

}
