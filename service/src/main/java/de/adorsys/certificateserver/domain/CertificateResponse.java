package de.adorsys.certificateserver.domain;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel(description = "Certificate Response", value = "CertificateResponse")
public class CertificateResponse {

  private String encodedCert;
  private String privateKey;
  private String keyId;
  private String algorithm;
}
