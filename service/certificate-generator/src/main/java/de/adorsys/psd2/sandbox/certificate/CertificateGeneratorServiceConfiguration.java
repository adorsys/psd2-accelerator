package de.adorsys.psd2.sandbox.certificate;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = CertificateGeneratorBasePackage.class)
public class CertificateGeneratorServiceConfiguration {
}
