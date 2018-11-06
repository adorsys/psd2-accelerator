package de.adorsys.psd2.sandbox.certificateserver.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.aspsp.xs2a.config.WebConfig;
import de.adorsys.psd2.sandbox.certificateserver.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.certificateserver.domain.CertificateResponse;
import de.adorsys.psd2.sandbox.certificateserver.domain.PspRole;
import de.adorsys.psd2.sandbox.certificateserver.service.CertificateService;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(secure = false)
public class CertificateControllerTest {

  private static final String KEY_ID = "154054446";

  // CHECKSTYLE:OFF
  private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----Stuff-----END CERTIFICATE-----";
  private static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----Stuff-----END RSA PRIVATE KEY-----";
  // CHECKSTYLE:ON
  private static final String ALGORITHM = "SHA256WITHRSA";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CertificateService certificateService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void createCert() throws Exception {
    CertificateResponse certificateResponse = CertificateResponse.builder()
        .encodedCert(CERTIFICATE)
        .privateKey(PRIVATE_KEY)
        .build();

    given(certificateService.newCertificate(anyObject())).willReturn(certificateResponse);

    CertificateRequest certificateRequest = CertificateRequest.builder()
        .authorizationNumber("87B2AC")
        .organizationName("Fictional Corporation AG")
        .roles(Collections.singletonList(PspRole.AISP))
        .build();

    mockMvc.perform(
        post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(certificateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.encodedCert").value(CERTIFICATE))
        .andExpect(jsonPath("$.privateKey").value(PRIVATE_KEY));
  }

  @Test
  public void createCertWithoutRoles() throws Exception {
    CertificateRequest certificateRequest = CertificateRequest.builder()
        .authorizationNumber("87B2AC")
        .organizationName("Fictional Corporation AG")
        .build();

    MockHttpServletResponse response = mockMvc.perform(
        post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(certificateRequest)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    assertThat(response.getContentAsString(), is(""));
  }

  @Test
  public void createCertWithoutAuthrizationNumber() throws Exception {
    CertificateRequest certificateRequest = CertificateRequest.builder()
        .organizationName("Fictional Corporation AG")
        .roles(Collections.singletonList(PspRole.AISP))
        .build();

    MockHttpServletResponse response = mockMvc.perform(
        post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(certificateRequest)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    assertThat(response.getContentAsString(), is(""));
  }

  @Test
  public void createCertWithoutOrganizationName() throws Exception {
    CertificateRequest certificateRequest = CertificateRequest.builder()
        .authorizationNumber("87B2AC")
        .roles(Collections.singletonList(PspRole.AISP))
        .build();

    MockHttpServletResponse response = mockMvc.perform(
        post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(certificateRequest)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    assertThat(response.getContentAsString(), is(""));
  }

  @SpringBootApplication
  static class WithoutXs2aApplication {
  }

}
