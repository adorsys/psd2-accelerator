package de.adorsys.certificateserver.web;

import de.adorsys.certificateserver.domain.CertificateRequest;
import de.adorsys.certificateserver.domain.CertificateResponse;
import de.adorsys.certificateserver.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/cert-generator")
@Api(value = "Certificate Controller")
public class CertificateController {

    private CertificateService cerService;

    public CertificateController(CertificateService cerService) {
        this.cerService = cerService;
    }

    @ApiOperation(value = "Create a new base64 encoded X509 certificate for authentication at " +
            "the XS2A API with the corresponding private key and meta data", response = CertificateResponse.class)
    @PostMapping
    public ResponseEntity<CertificateResponse> createCert(@Valid @RequestBody CertificateRequest certData) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cerService.newCertificate(certData));
    }
}
