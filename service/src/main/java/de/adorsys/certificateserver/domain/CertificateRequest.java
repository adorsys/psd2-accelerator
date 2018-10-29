package de.adorsys.certificateserver.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@ApiModel(description = "Certificate Data", value = "CertificateRequest")
public class CertificateRequest {

    @ApiModelProperty(required = true, example = "87B2AC", notes = "Available in the Public Register of the appropriate National Competent Authority; ")
    @NotNull
    private String authorizationNumber;

    @ApiModelProperty(required = true, example = "true", notes = "Account Information Service Provider")
    private boolean aISP;

    @ApiModelProperty(required = true, example = "true", notes = "Payment Initiation Service Provider")
    private boolean pISP;

    @ApiModelProperty(required = true, example = "true", notes = "Payment Instrument Issuer Service Provider")
    private boolean pIISP;

    @ApiModelProperty(required = true, example = "Fictional Corporation AG", notes = "Registered name of your corporation", position = 1)
    @NotNull
    private String organizationName;

    @ApiModelProperty(example = "Information Technology", notes = "", position = 2)
    private String organizationUnit;

    @ApiModelProperty(example = "public.corporation.de", notes = "Domain of your corporation", position = 2)
    private String domainComponent;

    @ApiModelProperty(example = "Nuremberg", notes = "Name of the city of your corporation headquarter", position = 2)
    private String localityName;

    @ApiModelProperty(example = "Bayern", notes = "Name of the state/province of your corporation headquarter", position = 2)
    private String stateOrProvinceName;

    @ApiModelProperty(example = "Germany", notes = "Name of the country your corporation is registered", position = 2)
    private String countryName;

    //TODO default value?
    //TODO min/max validation does not work
    @ApiModelProperty(example = "365", notes = "Number of days the certificate is valid", position = 2)
    @Size(min = 1, max = 365)
    private int validity;

}
