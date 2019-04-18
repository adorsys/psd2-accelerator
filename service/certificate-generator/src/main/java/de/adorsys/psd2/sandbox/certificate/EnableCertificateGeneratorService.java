package de.adorsys.psd2.sandbox.certificate;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import(CertificateGeneratorServiceConfiguration.class)
public @interface EnableCertificateGeneratorService {
}
