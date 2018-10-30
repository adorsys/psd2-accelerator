package de.adorsys.certificateserver.service;

import de.adorsys.certificateserver.domain.CertificateRequest;
import de.adorsys.certificateserver.domain.CertificateResponse;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CertificateServiceTest {

    @Test
    public void newCertificateCreatesCertAndKey() {
        CertificateService certificateService = new CertificateService();
        CertificateRequest certificateRequest = CertificateRequest.builder()
                .authorizationNumber("12345")
                .countryName("Germany")
                .organizationName("adorsys").build();
        CertificateResponse certificateResponse = certificateService.newCertificate(certificateRequest);
        assertNotNull(certificateResponse.getPrivateKey());
        assertNotNull(certificateResponse.getEncodedCert());
    }

    @Test
    public void exportPrivateKeyToStringResultsInSingleLinePK() {
        PrivateKey key = CertificateService.getKeyFromClassPath("MyRootCA.key");
        String result = CertificateService.exportToString(key).trim();
        assertTrue(result.startsWith("-----BEGIN RSA PRIVATE KEY-----"));
        assertTrue(result.endsWith("-----END RSA PRIVATE KEY-----"));
    }

    @Test
    public void exportCertificateToStringResultsInSingleLineCertificate() {
        X509Certificate cert = CertificateService.getCertificateFromClassPath("MyRootCA.pem");
        String result = CertificateService.exportToString(cert).trim();
        System.out.println(result);
        assertTrue(result.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(result.endsWith("-----END CERTIFICATE-----"));
    }
}
