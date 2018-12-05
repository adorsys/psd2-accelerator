package de.adorsys.psd2.sandbox.xs2a.web.filter;

import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * The intend of this class is to return a mock certificate, when we don't want to enter manually
 * everytime the qwac certificate in case of test.
 */
public class MockCertificateFilter extends QwacCertificateFilter {

  @Override
  public String getEncodedTppQwacCert(HttpServletRequest httpRequest) {
    return "-----BEGIN CERTIFICATE-----"
        + "MIIEBjCCAu6gAwIBAgIEAmCHWTANBgkqhkiG9w0BAQsFADCBlDELMAkGA1UEBhMCREUxDzANBgNVBAgMBkhlc3Nl"
        + "bjESMBAGA1UEBwwJRnJhbmtmdXJ0MRUwEwYDVQQKDAxBdXRob3JpdHkgQ0ExCzAJBgNVBAsMAklUMSEwHwYDVQQD"
        + "DBhBdXRob3JpdHkgQ0EgRG9tYWluIE5hbWUxGTAXBgkqhkiG9w0BCQEWCmNhQHRlc3QuZGUwHhcNMTgwODE3MDcx"
        + "NzAyWhcNMTgwOTAzMDc1NzMxWjB6MRMwEQYDVQQDDApUUFAgU2FtcGxlMQwwCgYDVQQKDANvcmcxCzAJBgNVBAsM"
        + "Am91MRAwDgYDVQQGEwdHZXJtYW55MQ8wDQYDVQQIDAZCYXllcm4xEjAQBgNVBAcMCU51cmVtYmVyZzERMA8GA1UE"
        + "YQwIMTIzNDU5ODcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCMMnLvNLvqxkHbxdcWRcyUrZ4oy++R"
        + "/7hWMiWH4U+5kLTLICnlFofN3EgIuP5hZz9Zm8aPoJkr8Y1xEyP8X4a5YTFtMmrXwAOgW6BVTaBeO7eV6Me1yc2N"
        + "awzWMNp0Zz/Lsnrmj2h7/dRYaYofFHjWPFRW+gjVwv95NFhcD9+H5rr+fMwoci0ERFvy70TYnLfuRrG1BpYOwEV+"
        + "wVFRIciXE3CKjEh2wbz1Yr4DhD+6FtOElU8VPkWqGRZmr1n54apuLrxL9vIbt7qsaQirsUp5ez2SFGFTydUv+WqZ"
        + "aPGzONVptAymOfTcIsgcxDWx/liKlpdqwyXpJaOIrrXcEnQ1AgMBAAGjeTB3MHUGCCsGAQUFBwEDBGkwZwYGBACB"
        + "mCcCMF0wTDARBgcEAIGYJwEBDAZQU1BfQVMwEQYHBACBmCcBAgwGUFNQX1BJMBEGBwQAgZgnAQMMBlBTUF9BSTAR"
        + "BgcEAIGYJwEEDAZQU1BfSUMMBEF1dGgMBzEyMTkwODgwDQYJKoZIhvcNAQELBQADggEBAKrHWMriNquiC1vfNKkJ"
        + "FPINi2T2J5FmRQfamrkzS3AI5zPPXx32MzbrTkQb+Zl7qTvClmIFpDG45YC+JVYz+4/gMSJChJfW+JYtyW/Am6ee"
        + "IYZ1sk+VPvXgxuTA0aZLQsVHsaeTHnQ7lZzN3S0Ao5O35AGKqBITu6Mo1t4WglNJLZHZ0iFL92yfezfV7LF9JYAD"
        + "/6JFVTeuBwKKHNjPupjeVBku/C7qVDbogo1Ubiowt+hMMPLVLPjxe6Xo9SUtkGj3+5ID4Z8NGHDaaF2IGVGaJkHK"
        + "9+PYTYEBRDsbc1GwgzTzbds5lao6eMyepL/Kl7iUNtn3Vox/XiSymunGCmQ="
        + "-----END CERTIFICATE-----";
  }
}
