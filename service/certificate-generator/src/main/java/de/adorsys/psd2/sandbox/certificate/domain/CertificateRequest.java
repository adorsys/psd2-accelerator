package de.adorsys.psd2.sandbox.certificate.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Certificate Data", value = "CertificateRequest")
public class CertificateRequest {

  @ApiModelProperty(required = true, example = "87B2AC",
      notes = "Available in the Public Register of the appropriate National Competent Authority; ")
  @NotNull
  private String authorizationNumber;

  // TODO infer from enum?
  @ApiModelProperty(required = true, notes = "Roles of the PSP", position = 1)
  @Size(min = 1, max = 3)
  @NotNull
  @Builder.Default
  private List<PspRole> roles = new ArrayList<>();

  @ApiModelProperty(required = true, example = "Fictional Corporation AG",
      notes = "Registered name of your corporation", position = 1)
  @NotNull
  private String organizationName;

  @ApiModelProperty(example = "Information Technology", notes = "", position = 2)
  private String organizationUnit;

  @ApiModelProperty(example = "public.corporation.de",
      notes = "Domain of your corporation", position = 2)
  private String domainComponent;

  @ApiModelProperty(example = "Nuremberg",
      notes = "Name of the city of your corporation headquarter", position = 2)
  private String localityName;

  @ApiModelProperty(example = "Bayern",
      notes = "Name of the state/province of your corporation headquarter", position = 2)
  private String stateOrProvinceName;

  @ApiModelProperty(example = "Germany",
      notes = "Name of the country your corporation is registered", position = 2)
  private String countryName;

  @ApiModelProperty(example = "365",
      notes = "Number of days the certificate is valid", position = 2)
  @Min(1)
  @Max(365)
  @NotNull
  @Builder.Default
  private int validity = 365;

}
