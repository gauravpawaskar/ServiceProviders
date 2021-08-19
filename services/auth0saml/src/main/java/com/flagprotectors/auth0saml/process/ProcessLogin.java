package com.flagprotectors.auth0saml.process;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@RestController
public class ProcessLogin {

    @PostMapping(value = "/processlogin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity processLogin(SAMLResponse samlResponseForm) {
        JSONObject jsonbject = new JSONObject();
        System.out.println(samlResponseForm.getSAMLResponse());

        String x509SigningCertificate = "MIIDETCCAfmgAwIBAgIJNgiyf2GQhYWdMA0GCSqGSIb3DQEBCwUAMCYxJDAiBgNVBAMTG2ZsYWdwcm90ZWN0b3JzLnVzLmF1dGgwLmNvbTAeFw0yMTA3MjAwMzMxNTFaFw0zNTAzMjkwMzMxNTFaMCYxJDAiBgNVBAMTG2ZsYWdwcm90ZWN0b3JzLnVzLmF1dGgwLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMLj3ZyYe+GFea6L16/I4bqRZgr8j75+cW5me6J8tQo5CxL05YvrOPTqQTMdzQXGv9iAVBfB19q2cb/2gvJis9K6p3R69zmI36xxM/SThjg08kh4WjHFqKrHCe+BXOsI0f33LLCMZHKVvg864irEQmi3/w8UAlyMfQc3i0T2Chrhne5Arg1nZ4vnOowtIsezEkHrCqA5IVGT0F8xqwRDjwE28wXWPDTrptL5NOV1bsfkpczVVyTbA2KopqdX4vhDePB5xOJJyNbM7ZCjLgVHpzsmsaP8hN0rNKP+mVw18+y4m9n6aSfd4D7xBMm7gwgbAC8JrXbi/8HtwoqTdfB0JPkCAwEAAaNCMEAwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUGS27zX4eab4oA7iluP+6GxRXaP4wDgYDVR0PAQH/BAQDAgKEMA0GCSqGSIb3DQEBCwUAA4IBAQBvXJgFWg53ElCa9R1fVF3R8CqtHcI4DLJhpcXxkzxdmS5mst1fAnw0lvQlL6HzS2dEE7//yoOVu/gi1lJVFE2/WpXkxPyH0t4I65OnZiUYpxJR+3U48L1hkBAYiG36rPOTCYv+yzxqfJAFEja1+x9GOC/Pm98JmPDkkPe2tDufVuYPzeR6w8q6C7I6/YUslonrcHeY7i7xt//4LM7Andg2orb56unPoI9DZB2w4keCDXIo5Q554Iebj4HfMSx/o8+7+/7SWIsr8W/4NOMPqcahaYERz04xiaEOP53AR+shGDya3CBeQNPdtJ44u3GUoZOnn49ndmAe9zJNpzi8k4EC";

        try {
            InitializationService.initialize();
        } catch (InitializationException e) {
            throw new IllegalStateException("Cannot initialize OpenSAML", e);
        }

        byte[] decodedBytesSAMLResponse = Base64.getDecoder().decode(samlResponseForm.getSAMLResponse());
        String decodedStringSAMLResponse = new String(decodedBytesSAMLResponse);

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setCoalescing(true);
            factory.setExpandEntityReferences(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            factory.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
            // factory.setIgnoringComments(true);
            System.out.println("Successful init");

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(decodedStringSAMLResponse)));

            System.out.println("Successful Parse");

            // Document doc = builder.parse(is);
            Response res = (Response) XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                    .getUnmarshaller(doc.getDocumentElement()).unmarshall(doc.getDocumentElement());
            // List<Assertion> validAssertions = new ArrayList<>();

            System.out.println("Successful Unmarshall");

            List<Assertion> foundAssertions = new ArrayList<>(res.getAssertions());

            SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();

            profileValidator.validate(foundAssertions.get(0).getSignature());
            String nameID = foundAssertions.get(0).getSubject().getNameID().getValue();
            System.out.println(nameID);
            jsonbject.put("nameid", nameID);
            // profileValidator.validate(foundAssertions.get(1).getSignature());
            X509Certificate signingCertificate = buildX509Certificate(x509SigningCertificate);

            BasicX509Credential sigCredential = new BasicX509Credential(signingCertificate);
            sigCredential.setEntityCertificate(signingCertificate);
            SignatureValidator.validate(foundAssertions.get(0).getSignature(), sigCredential);
        } catch (IllegalAccessError ex) {
            System.out.println(ex);
        } catch (SAXException e1) {
            System.out.println(e1);
        } catch (IOException e1) {
            System.out.println(e1);
        } catch (ParserConfigurationException e1) {
            System.out.println(e1);
        } catch (UnmarshallingException e1) {
            System.out.println(e1);
        } catch (org.opensaml.xmlsec.signature.support.SignatureException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        jsonbject.put("status", "success");

        // jsonbject.put("response", samlResponseForm.getSAMLResponse());

        return ResponseEntity.status(HttpStatus.OK).body(jsonbject.toString());
    }

    public X509Certificate buildX509Certificate(String certificateString) throws SignatureException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(certificateString))) {
                return (X509Certificate) certificateFactory.generateCertificate(bais);
            }
        } catch (IOException | CertificateException e) {
            throw new SignatureException("Failed to validate due to certificate error.", e);
        }
    }

}
