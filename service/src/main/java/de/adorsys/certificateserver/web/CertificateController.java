package de.adorsys.certificateserver.web;

import de.adorsys.certificateserver.domain.CertificateData;
import de.adorsys.certificateserver.domain.CertificateResponse;
import de.adorsys.certificateserver.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cert-generator")
@Api(value = "Certificate Controller")
public class CertificateController {

    @Autowired
    private CertificateService cerService;

    @ApiOperation(value = "Returns Base 64 encoded Certificate", response = CertificateResponse.class)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity add(@RequestBody CertificateData certData)  {

        return ResponseEntity.status(HttpStatus.CREATED).body(cerService.newCertificate(certData));
    }
}
