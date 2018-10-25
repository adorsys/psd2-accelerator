package de.adorsys.certificateserver.service;

import de.adorsys.certificateserver.domain.*;
import de.adorsys.certificateserver.utils.CertificateUtils;
import de.adorsys.certificateserver.utils.Psd2Utils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CertificateService.class);

    public CertificateResponse newCertificate(CertificateData certData) {
        SubjectData subjectData = generateSubjectData(certData);
        IssuerData issuerData = generateIssuerData();
        QCStatement qcStatement = qcStatement(certData);

        X509Certificate cert = CertificateUtils.generateCertificate(subjectData, issuerData, qcStatement);

        return CertificateResponse.builder()
                       .privateKey(CertificateUtils.convertObjectToString(subjectData.getPrivateKey()))
                       .encodedCert(CertificateUtils.convertObjectToString(cert))
                       .keyId(cert.getSerialNumber().toString())
                       .algorithm(cert.getSigAlgName())
                       .build();
    }

    private QCStatement qcStatement(CertificateData cerData) {
        List<RoleOfPSP> roles = new ArrayList<>();
        //if (cerData.isASPSP()) roles.add(RoleOfPSP.PSP_AS); // TODO: How does the role ASPSP differ to the others?
        if (cerData.isPISP()) roles.add(RoleOfPSP.PSP_PI);
        if (cerData.isAISP()) roles.add(RoleOfPSP.PSP_AI);
        if (cerData.isPIISP()) roles.add(RoleOfPSP.PSP_IC);

        RolesOfPSP rolesOfPSP = new RolesOfPSP(roles.toArray(new RoleOfPSP[0]));
        // TODO: extract NCAName and NCAId from Issuer instead of hard-coded Strings?
        NCAName nCAName = new NCAName("Fictional National Authority");
        NCAId nCAId = new NCAId("DE-FICTNCA");
        ASN1Encodable qcStatementInfo = Psd2Utils.psd2QcType(rolesOfPSP, nCAName, nCAId);

        return new QCStatement(PSD2QCObjectIdentifiers.id_etsi_psd2_qcStatement, qcStatementInfo);
    }

    private SubjectData generateSubjectData(CertificateData cerData) {
        KeyPair keyPairSubject = generateKeyPair(2048, "RSA");

        Date startDate = new Date(System.currentTimeMillis());

        // Usage of calendar is necessary because "cerData.getValidity * 86400000" results into an overflow of a data type "long"
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, cerData.getValidity());

        Date endDate = new Date(calendar.getTimeInMillis());

        Random rand = new Random();
        Integer serialNumber = rand.nextInt(Integer.MAX_VALUE);

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.O, cerData.getOrganizationName());
        if (cerData.getDomainComponent() != null) builder.addRDN(BCStyle.DC, cerData.getDomainComponent());
        if (cerData.getOrganizationUnit() != null) builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
        if (cerData.getCountryName() != null) builder.addRDN(BCStyle.CN, cerData.getCountryName());
        if (cerData.getStateOrProvinceName() != null) builder.addRDN(BCStyle.ST, cerData.getStateOrProvinceName());
        if (cerData.getLocalityName() != null) builder.addRDN(BCStyle.L, cerData.getLocalityName());

        // Organization-Identifier should be something like: PSDDE-FICTNCA-820B3A; Authorization Number is just the last part
        // TODO: Generate method which is building the organization_identifier with the values we have
        builder.addRDN(BCStyle.ORGANIZATION_IDENTIFIER, cerData.getAuthorizationNumber());

        return new SubjectData(keyPairSubject.getPrivate(), keyPairSubject.getPublic(), builder.build(), serialNumber, startDate, endDate);
    }

    private KeyPair generateKeyPair(int keySize, String keyType) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(keySize, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error at key pair generation due to Algorithm: {}", e.getMessage());
        } catch (NoSuchProviderException e) {
            LOGGER.error("Error at key pair generation due to Provider: {}", e.getMessage());
        }
        return null;
    }

    private IssuerData generateIssuerData() {
        IssuerData issuerData = new IssuerData();

        X509Certificate cert = CertificateUtils.getCertificateFromFile("MyRootCA.pem");

        LOGGER.debug("Information about generated X509Certificate: {}", cert);

        X500Name issuerName;
        try {
            issuerName = new JcaX509CertificateHolder(cert).getSubject();
            issuerData.setX500name(issuerName);
        } catch (CertificateEncodingException e) {
            LOGGER.error("Error at generation of issuer data: {}", e.getMessage());
        }

        PrivateKey privateKey = CertificateUtils.getKeyFromFile("MyRootCA.key");
        issuerData.setPrivateKey(privateKey);

        return issuerData;
    }
}
