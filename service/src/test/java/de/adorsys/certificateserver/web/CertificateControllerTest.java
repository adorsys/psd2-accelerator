package de.adorsys.certificateserver.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.certificateserver.domain.CertificateRequest;
import de.adorsys.certificateserver.domain.CertificateResponse;
import de.adorsys.certificateserver.service.CertificateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CertificateController.class)
public class CertificateControllerTest {

  private static final String KEY_ID = "154054446";
  private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----Stuff-----END CERTIFICATE-----";
  private static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----Stuff-----END RSA PRIVATE KEY-----";
  private static final String ALGORITHM = "SHA256WITHRSA";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CertificateService certificateService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void createCert() throws Exception {
    CertificateResponse certificateResponse = CertificateResponse.builder()
                                                .keyId(KEY_ID)
                                                .encodedCert(CERTIFICATE)
                                                .privateKey(PRIVATE_KEY)
                                                .algorithm(ALGORITHM)
                                                .build();

    given(certificateService.newCertificate(anyObject())).willReturn(certificateResponse);


    CertificateRequest certificateRequest = CertificateRequest.builder()
                                              .authorizationNumber("87B2AC")
                                              .organizationName("Fictional Corporation AG")
                                              .build();

    mockMvc.perform(
      post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(certificateRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.encodedCert").value(CERTIFICATE))
      .andExpect(jsonPath("$.privateKey").value(PRIVATE_KEY))
      .andExpect(jsonPath("$.keyId").value(KEY_ID))
      .andExpect(jsonPath("$.algorithm").value(ALGORITHM));
  }
}
