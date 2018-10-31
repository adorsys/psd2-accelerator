package de.adorsys.certificateserver.service;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.certificateserver.CertificateException;
import de.adorsys.certificateserver.domain.*;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
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
import org.bouncycastle.util.encoders.Hex;
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
import java.util.*;

@Service
public class CertificateService {

    private static final String ISSUER_CERTIFICATE = "MyRootCA.pem";
    private static final String ISSUER_PRIVATE_KEY = "MyRootCA.key";

    private final static Logger log = LoggerFactory.getLogger(CertificateService.class);
    public static final String NCA_ID = "DE-ADORSYS";

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
            return writer.toString();//.replaceAll("\n", ""); // comment for testing purposes
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

        try {
            String formattedCertificate = format(cert);
            log.debug(formattedCertificate);
        } catch (Exception e) {
            throw new CertificateException("Could not format certificate", e);
        }

        return CertificateResponse.builder()
                .privateKey(exportToString(subjectData.getPrivateKey()))
                .encodedCert(exportToString(cert))
                .keyId(cert.getSerialNumber().toString())
                .algorithm(cert.getSigAlgName())
                .build();
    }

    static String format(X509Certificate cert) throws Exception {
        StringBuffer buf = new StringBuffer();
        String nl = System.getProperty("line.separator");

        buf.append("  [0]         Version: ").append(cert.getVersion()).append(nl);
        buf.append("         SerialNumber: ").append(cert.getSerialNumber()).append(nl);
        buf.append("             IssuerDN: ").append(cert.getIssuerDN().toString()).append(nl);
        buf.append("           Start Date: ").append(cert.getNotBefore()).append(nl);
        buf.append("           Final Date: ").append(cert.getNotAfter()).append(nl);
        buf.append("            SubjectDN: ").append(cert.getSubjectDN().toString()).append(nl);
        buf.append("           Public Key: ").append(cert.getPublicKey()).append(nl);
        buf.append("  Signature Algorithm: ").append(cert.getSigAlgName()).append(nl);

        byte[] sig = cert.getSignature();

        buf.append("            Signature: ").append(new String(Hex.encode(sig, 0, 20))).append(nl);
        for (int i = 20; i < sig.length; i += 20) {
          if (i < sig.length - 20) {
            buf.append("                       ").append(new String(Hex.encode(sig, i, 20))).append(nl);
          } else {
            buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i))).append(nl);
          }
        }

        TBSCertificateStructure tbs = TBSCertificateStructure.getInstance(ASN1Sequence.fromByteArray(cert.getTBSCertificate()));
        X509Extensions extensions = tbs.getExtensions();

        if (extensions != null) {
            Enumeration e = extensions.oids();

            if (e.hasMoreElements()) {
                buf.append("       Extensions: \n");
            }

            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                X509Extension ext = extensions.getExtension(oid);

                if (ext.getValue() != null) {
                    byte[] octs = ext.getValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals(Extension.basicConstraints)) {
                            buf.append(BasicConstraints.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals(Extension.keyUsage)) {
                            buf.append(KeyUsage.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals(MiscObjectIdentifiers.netscapeCertType)) {
                            buf.append(new NetscapeCertType((DERBitString) dIn.readObject())).append(nl);
                        } else if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            buf.append(new NetscapeRevocationURL((DERIA5String) dIn.readObject())).append(nl);
                        } else if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            buf.append(new VerisignCzagExtension((DERIA5String) dIn.readObject())).append(nl);
                        } else {
                            buf.append(oid.getId());
                            buf.append(" value = ").append(ASN1Dump.dumpAsString(dIn.readObject())).append(nl);
                        }
                    } catch (Exception ex) {
                      buf.append(oid.getId());
                      buf.append(" value = ").append("*****").append(nl);
                    }
                } else {
                    buf.append(nl);
                }
            }
        }
        return buf.toString();
    }

    public QCStatement generateQcStatement(CertificateRequest certificateRequest) {

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
        return new NCAId(NCA_ID);
    }

    public SubjectData generateSubjectData(CertificateRequest cerData) {
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

        builder.addRDN(BCStyle.ORGANIZATION_IDENTIFIER, "PSD" + getNcaIdFromIssuerData() + "-" + cerData.getAuthorizationNumber());

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

    public IssuerData generateIssuerData() {
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

    private static class RolesOfPSP extends DERSequence {

        public static RolesOfPSP fromCertificateRequest(CertificateRequest certificateRequest) {
            List<RoleOfPSP> roles = new ArrayList<>();

            if (certificateRequest.getRoles().contains(PspRole.AISP)) {
                roles.add(RoleOfPSP.PSP_AI);
            }

            if (certificateRequest.getRoles().contains(PspRole.PISP)) {
                roles.add(RoleOfPSP.PSP_PI);
            }

            if (certificateRequest.getRoles().contains(PspRole.PIISP)) {
                roles.add(RoleOfPSP.PSP_IC);
            }

            return new RolesOfPSP(roles.toArray(new RoleOfPSP[]{}));
        }

        public RolesOfPSP(RoleOfPSP[] array) {
            super(array);
        }
    }

    private static class RoleOfPSP extends DERSequence {
        public static final RoleOfPSP PSP_PI = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_pi, RoleOfPspName.PSP_PI);
        public static final RoleOfPSP PSP_AI = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_ai, RoleOfPspName.PSP_AI);
        public static final RoleOfPSP PSP_IC = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_ic, RoleOfPspName.PSP_IC);

        private RoleOfPSP(RoleOfPspOid roleOfPspOid, RoleOfPspName roleOfPspName) {
            super(new ASN1Encodable[]{roleOfPspOid, roleOfPspName});
        }
    }

    private static class RoleOfPspName extends DERUTF8String {
        public static final RoleOfPspName PSP_AS = new RoleOfPspName("PSP_AS");
        public static final RoleOfPspName PSP_PI = new RoleOfPspName("PSP_PI");
        public static final RoleOfPspName PSP_AI = new RoleOfPspName("PSP_AI");
        public static final RoleOfPspName PSP_IC = new RoleOfPspName("PSP_IC");

        private RoleOfPspName(String string) {
            super(string);
        }
    }

    private static class RoleOfPspOid extends ASN1ObjectIdentifier {
        public static final ASN1ObjectIdentifier etsi_psd2_roles = new ASN1ObjectIdentifier("0.4.0.19495.1");
        public static final RoleOfPspOid id_psd2_role_psp_as = new RoleOfPspOid(etsi_psd2_roles.branch("1"));
        public static final RoleOfPspOid id_psd2_role_psp_pi = new RoleOfPspOid(etsi_psd2_roles.branch("2"));
        public static final RoleOfPspOid id_psd2_role_psp_ai = new RoleOfPspOid(etsi_psd2_roles.branch("3"));
        public static final RoleOfPspOid id_psd2_role_psp_ic = new RoleOfPspOid(etsi_psd2_roles.branch("4"));

        public RoleOfPspOid(ASN1ObjectIdentifier identifier) {
            super(identifier.getId());
        }
    }
}
