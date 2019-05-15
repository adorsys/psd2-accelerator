package de.adorsys.psd2.sandbox.xs2a.web.filter;

import de.adorsys.psd2.xs2a.service.validator.tpp.TppInfoHolder;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppRoleValidationService;
import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * The intent of this class is to return a mock certificate, when we don't want to enter manually
 * everytime the qwac certificate in case of test.
 */
public class MockCertificateFilter extends QwacCertificateFilter {

  public MockCertificateFilter(
      TppRoleValidationService tppRoleValidationService,
      TppInfoHolder tppInfoHolder
  ) {
    super(tppRoleValidationService, tppInfoHolder);
  }

  @Override
  public String getEncodedTppQwacCert(HttpServletRequest httpRequest) {
    return "-----BEGIN CERTIFICATE-----"
               + "MIIFQTCCAymgAwIBAgIESLvdaTANBgkqhkiG9w0BAQsFADB4MQswCQYDVQQGEwJERTEQMA4GA1UECA"
               + "wHQkFWQVJJQTESMBAGA1UEBwwJTnVyZW1iZXJnMSIwIAYDVQQKDBlUcnVzdCBTZXJ2aWNlIFByb3Zp"
               + "ZGVyIEFHMR8wHQYDVQQLDBZJbmZvcm1hdGlvbiBUZWNobm9sb2d5MB4XDTE5MDMwNTE1MTIwN1oXDT"
               + "IwMDMwNDAwMDAwMFowgcwxITAfBgNVBAoMGEZpY3Rpb25hbCBDb3Jwb3JhdGlvbiBBRzEJMAcGA1UE"
               + "AwwAMSUwIwYKCZImiZPyLGQBGRYVcHVibGljLmNvcnBvcmF0aW9uLmRlMR8wHQYDVQQLDBZJbmZvcm"
               + "1hdGlvbiBUZWNobm9sb2d5MRAwDgYDVQQGEwdHZXJtYW55MQ8wDQYDVQQIDAZCYXllcm4xEjAQBgNV"
               + "BAcMCU51cmVtYmVyZzEdMBsGA1UEYQwUUFNEREUtRkFLRU5DQS04N0IyQUMwggEiMA0GCSqGSIb3DQ"
               + "EBAQUAA4IBDwAwggEKAoIBAQCeDcYlVutZeGFtOkonIMGHwway2ASZl8p7/v7USIxeMo/5ppbAa6Ei"
               + "7i7jH9ORBoHV6qxAwNFkdd8JDneNiNn0NSvoYTemr5mqyXYhwpzLueXth1oBgjLYvcaLFXXQGS0dd6"
               + "sDcaCTbw9xdDmap+6xYDRzIrdviyiph1ewpUXlrEHNu5Oomk7R5Dpv4gM9uRwYiskRigZdnArfyQ3Z"
               + "YW4VZvMlFW3t1IVvSiOWvruF24w+j1g3BOHNM7tIAOlOUYQpHV1G1ChcFt5/ICArtsGAd4/ZUzlmuj"
               + "ktdO+hNA70fDHUxkG6vQRFQhSnszzJ/C/g632nMTJbAaGtO2OvdL9DAgMBAAGjfjB8MHoGCCsGAQUF"
               + "BwEDBG4wbAYGBACBmCcCMGIwOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAgwGUFNQX1BJMB"
               + "EGBwQAgZgnAQQMBlBTUF9JQwwZVHJ1c3QgU2VydmljZSBQcm92aWRlciBBRwwKREUtRkFLRU5DQTAN"
               + "BgkqhkiG9w0BAQsFAAOCAgEAK07yQviS7/zKm1EqQyyGkEbf/1sHb9FLPBr/BicYxc3IQGd4xG1SJ1"
               + "uLudX37Yq/o6exjixZ8ywib27jNLCpsF1dEQabHNXgS4enojf7CVTyKjDkKqE1mwqPmGeoWWwaWOUs"
               + "WQ2/Ja/UTW5Bn5iA+nHCXVrkcjFVnRvi+dSsRm4J3E0EdAAwBkSEqHGDZO1ZiAh20YkNExx8MKKiHA"
               + "VZ0ZFCXzYcaWzaK6yeCarvyPNCb+BAsc1wf3/88tLT9Nof/Ctzv2L9OjGHcalCLf/g/qTr6/50J4IM"
               + "VdBwoVkg27yRE5EC3RKJE5BFx6TNWeNGs7r8HpAhO/6hLKzVHjrsA8/SAwTWNQNWdP/azSV42DuVMj"
               + "Di5o5Ax9RkHXRvjsuwTR19AKvIc6nv/8XUtwORjHW+FTXTGa28PqCD1ZACiHytIBXrETevmLIlFuh6"
               + "ZaWKBYPUc3DmJbFSZkhRFybh1SEtl/WzeQjIKqkRw0MGzDIRwD0sYqeE8ENkJbXJG+Cy4c42mZmEwG"
               + "6E7HQQtiT9Irt1cnUiFDRe6g+h4GaxhOC5Pluxhij4DaNHCIZm30IHcyA4vZOyj7rXcvvfGMwPgbSd"
               + "qSdEeNB25FEmFmJnavESxyJKYNx3JONm//0yRpacfWos/MjmbLWynYz8Bv8EK7mCS84bmSlUrUgHoN"
               + "vDeBc=-----END CERTIFICATE-----";
  }
}
