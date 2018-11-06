package de.adorsys.psd2.sandbox.certificateserver.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.adorsys.psd2.sandbox.certificateserver.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.certificateserver.domain.CertificateResponse;
import de.adorsys.psd2.sandbox.certificateserver.domain.IssuerData;
import de.adorsys.psd2.sandbox.certificateserver.domain.PspRole;
import de.adorsys.psd2.sandbox.certificateserver.domain.SubjectData;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.junit.Test;

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
  public void exportPrivateKeyToStringResultsInSingleLinePrimaryKey() {
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

  @Test
  public void checkRolesWithinQcStatement() {
    CertificateService certificateService = new CertificateService();
    CertificateRequest certificateRequest = CertificateRequest.builder()
        .authorizationNumber("12345")
        .countryName("Germany")
        .roles(Collections.singletonList(PspRole.AISP))
        .organizationName("adorsys").build();

    SubjectData subjectData = certificateService.generateSubjectData(certificateRequest);
    IssuerData issuerData = certificateService.generateIssuerData();
    QCStatement qcStatement = certificateService.generateQcStatement(certificateRequest);

    X509Certificate certificate = CertificateService
        .generateCertificate(subjectData, issuerData, qcStatement);

    String formattedCertificate = null;

    try {
      formattedCertificate = CertificateService.format(certificate);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertNotNull(formattedCertificate);
    assertThat(formattedCertificate, containsString(CertificateService.NCA_SHORT_NAME));
    assertThat(formattedCertificate, containsString("PSP_AI"));
    assertThat(formattedCertificate, not(containsString("PSP_PI")));
    assertThat(formattedCertificate, not(containsString("PSP_IC")));
    assertThat(formattedCertificate, not(containsString("PSP_AS")));
  }
}
