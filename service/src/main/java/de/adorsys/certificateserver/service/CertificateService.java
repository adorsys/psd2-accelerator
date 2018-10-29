package de.adorsys.certificateserver.service;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.certificateserver.CertificateException;
import de.adorsys.certificateserver.domain.*;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Random;

@Service
public class CertificateService {

    private static final String ISSUER_CERTIFICATE = "MyRootCA.pem";
    private static final String ISSUER_PRIVATE_KEY = "MyRootCA.key";

    private final static Logger log = LoggerFactory.getLogger(CertificateService.class);

    /**
     * @param filename Name of the key file. Suffix should be .pem
     * @return X509Certificate
     */
    static X509Certificate getCertificateFromClassPath(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("certificates/" + filename);

        if (is == null)
            throw new CertificateException("Could not find certificate in classpath");

        try {
            byte[] bytes = IOUtils.toByteArray(is);
            return X509CertUtils.parse(bytes);
        } catch (IOException e) {
            throw new CertificateException("Could not read certificate from classpath", e);
        }
    }

    /**
     * @param filename Name of the key file. Suffix should be .key
     * @return PrivateKey
     */
    static PrivateKey getKeyFromClassPath(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("certificates/" + filename);

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {
            Security.addProvider(new BouncyCastleProvider());
            PEMParser pp = new PEMParser(br);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
            KeyPair kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
            pp.close();
            return kp.getPrivate();
        } catch (IOException e) {
            throw new CertificateException("Could not read private key from classpath", e);
        }
    }

    /**
     * Generates new X.509 Certificate
     *
     * @param subjectData
     * @param issuerData
     * @param statement
     * @return X509Certificate
     */
    static X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, QCStatement statement) {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");

        ContentSigner contentSigner;

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                new BigInteger(subjectData.getSerialNumber().toString()), subjectData.getStartDate(), subjectData.getEndDate(),
                subjectData.getX500name(), subjectData.getPublicKey());

        JcaX509CertificateConverter certConverter;

        try {
            contentSigner = builder.build(issuerData.getPrivateKey());
            certGen.addExtension(Extension.qCStatements, false, statement);

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
            throw new CertificateException("Could not create certificate", e);
        }
    }

    /**
     * Converts Objects like Certificates/Keys into Strings without '\n' or '\r'
     *
     * @param obj
     * @return String
     */
    static String exportToString(Object obj) {
        try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(obj);
            pemWriter.flush();
            String response = writer.toString();
            return response.replaceAll("\n", "").replaceAll("\r", "");
        } catch (IOException e) {
            throw new CertificateException("Could not export certificate", e);
        }
    }

    static DERSequence createQcInfo(RolesOfPSP rolesOfPSP, NCAName nCAName, NCAId nCAId) {
        return new DERSequence(new ASN1Encodable[]{rolesOfPSP, nCAName, nCAId});
    }

    private final IssuerData issuerData;

    public CertificateService() {
        issuerData = generateIssuerData();
    }

    /**
     * Create a new base64 encoded X509 certificate for authentication at
     * the XS2A API with the corresponding private key and meta data
     *
     * @param certificateRequest data needed for certificate generation
     * @return CertificateResponse base64 encoded cert + private key
     */
    public CertificateResponse newCertificate(CertificateRequest certificateRequest) {
        SubjectData subjectData = generateSubjectData(certificateRequest);
        QCStatement qcStatement = generateQcStatement(certificateRequest);

        X509Certificate cert = generateCertificate(subjectData, issuerData, qcStatement);

        return CertificateResponse.builder()
                .privateKey(exportToString(subjectData.getPrivateKey()))
                .encodedCert(exportToString(cert))
                .keyId(cert.getSerialNumber().toString())
                .algorithm(cert.getSigAlgName())
                .build();
    }

    private QCStatement generateQcStatement(CertificateRequest certificateRequest) {

        NCAName nCAName = getNcaNameFromIssuerData();
        NCAId nCAId = getNcaIdFromIssuerData();
        ASN1Encodable qcStatementInfo = createQcInfo(
                RolesOfPSP.fromCertificateRequest(certificateRequest), nCAName, nCAId
        );

        return new QCStatement(PSD2QCObjectIdentifiers.id_etsi_psd2_qcStatement, qcStatementInfo);
    }

    private NCAName getNcaNameFromIssuerData() {
        return new NCAName(IETFUtils.valueToString(
                issuerData.getX500name().getRDNs(BCStyle.O)[0]
                        .getFirst().getValue())
        );
    }

    private NCAId getNcaIdFromIssuerData() {
        // TODO: extract NCAId from Issuer instead of hard-coded Strings? Which field?
        return new NCAId("DE-ADORSYS");
    }

    private SubjectData generateSubjectData(CertificateRequest cerData) {
        KeyPair keyPairSubject = generateKeyPair();

        Date expiration = Date.from(
                LocalDate.now().plusDays(cerData.getValidity()).atStartOfDay(ZoneOffset.UTC).toInstant()
        );

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

        Random rand = new Random();
        Integer serialNumber = rand.nextInt(Integer.MAX_VALUE);
        return new SubjectData(
                keyPairSubject.getPrivate(), keyPairSubject.getPublic(), builder.build(),
                serialNumber, new Date(), expiration
        );
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new CertificateException("Could not generate key pair", e);
        }
    }

    private IssuerData generateIssuerData() {
        IssuerData issuerData = new IssuerData();

        X509Certificate cert = getCertificateFromClassPath(ISSUER_CERTIFICATE);

        log.debug("Source for issuer data: {} from {}", cert, ISSUER_CERTIFICATE);

        try {
            issuerData.setX500name(new JcaX509CertificateHolder(cert).getSubject());
        } catch (CertificateEncodingException e) {
            throw new CertificateException("Could not read issuer data from certificate", e);
        }

        PrivateKey privateKey = getKeyFromClassPath(ISSUER_PRIVATE_KEY);
        issuerData.setPrivateKey(privateKey);

        return issuerData;
    }
}
