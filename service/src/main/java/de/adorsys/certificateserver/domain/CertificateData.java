package de.adorsys.certificateserver.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "Certificate Data", value = "CertificateData")
public class CertificateData {

    @ApiModelProperty(value = "domainComponent", required = true, example = "public.corporation.de")
    @NotNull
    private String domainComponent;

    @ApiModelProperty(value = "organizationName", required = true, example = "Fictional Corporation AG")
    @NotNull
    private String organizationName;

    @ApiModelProperty(value = "organizationUnit", required = true, example = "Information Technology")
    @NotNull
    private String organizationUnit;

    @ApiModelProperty(value = "localityName", required = true, example = "Nuremberg")
    @NotNull
    private String localityName;

    @ApiModelProperty(value = "stateOrProvinceName", required = true, example = "Bayern")
    @NotNull
    private String stateOrProvinceName;

    @ApiModelProperty(value = "countryName", required = true, example = "Germany")
    @NotNull
    private String countryName;

    // TODO Discussion: to be set from outside or hard-coded?
    @ApiModelProperty(value = "validity", required = true, example = "365")
    @NotNull
    private int validity;

    @ApiModelProperty(value = "pISP", required = true, example = "true")
    private boolean pISP;

    @ApiModelProperty(value = "aISP", required = true, example = "true")
    private boolean aISP;

    @ApiModelProperty(value = "pIISP", required = true, example = "true")
    private boolean pIISP;

    @ApiModelProperty(value = "aSPSP", required = true, example = "true")
    private boolean aSPSP;

    // TODO should be not part of this request (maybe within the header for authentication?)
    // not part of QCStatement, nor X.509 Certificate
    @ApiModelProperty(value = "authorizationNumber", required = true, example = "87B2AC")
    @NotNull
    private String authorizationNumber;

    // TODO remove from model class; set hard-coded
    /* The NCAName shall be plain text name in English provided by the NCA itself for purpose of identification in certificates.
     */
    @ApiModelProperty(value = "ncaName", required = true, example = "FictNCA")
    @NotNull
    private String ncaName;

    // TODO remove from model class; set hard-coded
    /* The NCAId shall contain information using the following structure in the presented order:
         2 character ISO 3166 countryName code representing the NCA countryName;
         hyphen-minus "-"; and
         2-8 character NCA identifier (A-Z uppercase only, no separator).
     * The NCAId shall be unique and provided by the NCA itself for purpose of identification in certificates.
     */
    @ApiModelProperty(value = "ncaId", required = true, example = "DE-FICTNCA")
    @NotNull
    private String ncaId;
}
