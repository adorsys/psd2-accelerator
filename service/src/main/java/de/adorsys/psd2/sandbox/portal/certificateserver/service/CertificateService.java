package de.adorsys.psd2.sandbox.portal.certificateserver.service;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.psd2.sandbox.portal.certificateserver.CertificateException;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.CertificateResponse;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.IssuerData;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.NcaId;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.NcaName;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.PspRole;
import de.adorsys.psd2.sandbox.portal.certificateserver.domain.SubjectData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
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


@Service
public class CertificateService {

  private static final String ISSUER_CERTIFICATE = "MyRootCA.pem";
  private static final String ISSUER_PRIVATE_KEY = "MyRootCA.key";
  private static final ASN1ObjectIdentifier ETSI_QC_STATEMENT =
      new ASN1ObjectIdentifier("0.4.0.19495.2");
  static final String NCA_SHORT_NAME = "FAKENCA";

  private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

  /**
   * Load x509 cert from classpath.
   *
   * @param filename Name of the key file. Suffix should be .pem
   * @return X509Certificate
   */
  static X509Certificate getCertificateFromClassPath(String filename) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream is = loader.getResourceAsStream("certificates/" + filename);

    if (is == null) {
      throw new CertificateException("Could not find certificate in classpath");
    }

    try {
      byte[] bytes = IOUtils.toByteArray(is);
      return X509CertUtils.parse(bytes);
    } catch (IOException ex) {
      throw new CertificateException("Could not read certificate from classpath", ex);
    }
  }

  /**
   * Load private key from classpath.
   *
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
    } catch (IOException ex) {
      throw new CertificateException("Could not read private key from classpath", ex);
    }
  }

  /**
   * Generates new X.509 Certificate
   *
   * @return X509Certificate
   */
  static X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData,
      QCStatement statement) {
    JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");

    ContentSigner contentSigner;

    X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
        new BigInteger(subjectData.getSerialNumber().toString()), subjectData.getStartDate(),
        subjectData.getEndDate(),
        subjectData.getX500name(), subjectData.getPublicKey());

    JcaX509CertificateConverter certConverter;

    try {
      contentSigner = builder.build(issuerData.getPrivateKey());
      certGen.addExtension(Extension.qCStatements, false, statement);

      X509CertificateHolder certHolder = certGen.build(contentSigner);

      certConverter = new JcaX509CertificateConverter();

      return certConverter.getCertificate(certHolder);
    } catch (Exception ex) {
      throw new CertificateException("Could not create certificate", ex);
    }
  }

  static String exportToString(Object obj) {
    try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(
        writer)) {
      pemWriter.writeObject(obj);
      pemWriter.flush();
      return writer.toString(); //.replaceAll("\n", ""); // comment for testing purposes
    } catch (IOException ex) {
      throw new CertificateException("Could not export certificate", ex);
    }
  }

  static DERSequence createQcInfo(RolesOfPsp rolesOfPsp, NcaName ncaName, NcaId ncaId) {
    return new DERSequence(new ASN1Encodable[]{rolesOfPsp, ncaName, ncaId});
  }

  private final IssuerData issuerData;

  public CertificateService() {
    issuerData = generateIssuerData();
  }

  /**
   * Create a new base64 encoded X509 certificate for authentication at the XS2A API with the
   * corresponding private key and meta data.
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
    } catch (Exception ex) {
      throw new CertificateException("Could not format certificate", ex);
    }

    return CertificateResponse.builder()
        .privateKey(exportToString(subjectData.getPrivateKey()))
        .encodedCert(exportToString(cert))
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
        buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i)))
            .append(nl);
      }
    }

    TBSCertificateStructure tbs = TBSCertificateStructure
        .getInstance(ASN1Sequence.fromByteArray(cert.getTBSCertificate()));
    X509Extensions extensions = tbs.getExtensions();

    if (extensions != null) {
      Enumeration oids = extensions.oids();

      if (oids.hasMoreElements()) {
        buf.append("       Extensions: \n");
      }

      while (oids.hasMoreElements()) {
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) oids.nextElement();
        X509Extension ext = extensions.getExtension(oid);

        if (ext.getValue() != null) {
          byte[] octs = ext.getValue().getOctets();
          ASN1InputStream asn1InputStream = new ASN1InputStream(octs);
          buf.append("                       critical(").append(ext.isCritical()).append(") ");
          try {
            if (oid.equals(Extension.basicConstraints)) {
              buf.append(BasicConstraints.getInstance(asn1InputStream.readObject())).append(nl);
            } else if (oid.equals(Extension.keyUsage)) {
              buf.append(KeyUsage.getInstance(asn1InputStream.readObject())).append(nl);
            } else if (oid.equals(MiscObjectIdentifiers.netscapeCertType)) {
              buf.append(new NetscapeCertType((DERBitString) asn1InputStream.readObject()))
                  .append(nl);
            } else if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
              buf.append(new NetscapeRevocationURL((DERIA5String) asn1InputStream.readObject()))
                  .append(nl);
            } else if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
              buf.append(new VerisignCzagExtension((DERIA5String) asn1InputStream.readObject()))
                  .append(nl);
            } else {
              buf.append(oid.getId());
              buf.append(" value = ").append(ASN1Dump.dumpAsString(asn1InputStream.readObject()))
                  .append(nl);
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

  QCStatement generateQcStatement(CertificateRequest certificateRequest) {

    NcaName ncaName = getNcaNameFromIssuerData();
    NcaId ncaId = getNcaIdFromIssuerData();
    ASN1Encodable qcStatementInfo = createQcInfo(
        RolesOfPsp.fromCertificateRequest(certificateRequest), ncaName, ncaId
    );

    return new QCStatement(ETSI_QC_STATEMENT, qcStatementInfo);
  }

  private NcaName getNcaNameFromIssuerData() {
    return new NcaName(IETFUtils.valueToString(
        issuerData.getX500name().getRDNs(BCStyle.O)[0]
            .getFirst().getValue())
    );
  }

  private NcaId getNcaIdFromIssuerData() {
    // TODO: map NcaName to NcaShortName -> dynamic generation?
    String country = IETFUtils
        .valueToString(issuerData.getX500name().getRDNs(BCStyle.C)[0].getFirst().getValue());

    return new NcaId(country + "-" + NCA_SHORT_NAME);
  }

  SubjectData generateSubjectData(CertificateRequest cerData) {

    X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
    builder.addRDN(BCStyle.O, cerData.getOrganizationName());

    builder.addRDN(BCStyle.CN, "");

    if (cerData.getDomainComponent() != null) {
      builder.addRDN(BCStyle.DC, cerData.getDomainComponent());
    }
    if (cerData.getOrganizationUnit() != null) {
      builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
    }
    if (cerData.getCountryName() != null) {
      builder.addRDN(BCStyle.C, cerData.getCountryName());
    }
    if (cerData.getStateOrProvinceName() != null) {
      builder.addRDN(BCStyle.ST, cerData.getStateOrProvinceName());
    }
    if (cerData.getLocalityName() != null) {
      builder.addRDN(BCStyle.L, cerData.getLocalityName());
    }

    builder.addRDN(BCStyle.ORGANIZATION_IDENTIFIER,
        "PSD" + getNcaIdFromIssuerData() + "-" + cerData.getAuthorizationNumber());

    Date expiration = Date.from(
        LocalDate.now().plusDays(cerData.getValidity()).atStartOfDay(ZoneOffset.UTC).toInstant()
    );
    KeyPair keyPairSubject = generateKeyPair();
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
    } catch (GeneralSecurityException ex) {
      throw new CertificateException("Could not generate key pair", ex);
    }
  }

  IssuerData generateIssuerData() {
    IssuerData issuerData = new IssuerData();

    X509Certificate cert = getCertificateFromClassPath(ISSUER_CERTIFICATE);

    log.debug("Source for issuer data: {} from {}", cert, ISSUER_CERTIFICATE);

    try {
      issuerData.setX500name(new JcaX509CertificateHolder(cert).getSubject());
    } catch (CertificateEncodingException ex) {
      throw new CertificateException("Could not read issuer data from certificate", ex);
    }

    PrivateKey privateKey = getKeyFromClassPath(ISSUER_PRIVATE_KEY);
    issuerData.setPrivateKey(privateKey);

    return issuerData;
  }

  private static class RolesOfPsp extends DERSequence {

    public static RolesOfPsp fromCertificateRequest(CertificateRequest certificateRequest) {
      List<RoleOfPsp> roles = new ArrayList<>();

      if (certificateRequest.getRoles().contains(PspRole.AISP)) {
        roles.add(RoleOfPsp.PSP_AI);
      }

      if (certificateRequest.getRoles().contains(PspRole.PISP)) {
        roles.add(RoleOfPsp.PSP_PI);
      }

      if (certificateRequest.getRoles().contains(PspRole.PIISP)) {
        roles.add(RoleOfPsp.PSP_IC);
      }

      return new RolesOfPsp(roles.toArray(new RoleOfPsp[]{}));
    }

    public RolesOfPsp(RoleOfPsp[] array) {
      super(array);
    }
  }

  private static class RoleOfPsp extends DERSequence {

    public static final RoleOfPsp PSP_PI = new RoleOfPsp(RoleOfPspOid.ID_PSD_2_ROLE_PSP_PI,
        RoleOfPspName.PSP_PI);
    public static final RoleOfPsp PSP_AI = new RoleOfPsp(RoleOfPspOid.ID_PSD_2_ROLE_PSP_AI,
        RoleOfPspName.PSP_AI);
    public static final RoleOfPsp PSP_IC = new RoleOfPsp(RoleOfPspOid.ROLE_OF_PSP_OID,
        RoleOfPspName.PSP_IC);

    private RoleOfPsp(RoleOfPspOid roleOfPspOid, RoleOfPspName roleOfPspName) {
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

    public static final ASN1ObjectIdentifier ETSI_PSD_2_ROLES = new ASN1ObjectIdentifier(
        "0.4.0.19495.1");
    public static final RoleOfPspOid ID_PSD_2_ROLE_PSP_AS = new RoleOfPspOid(
        ETSI_PSD_2_ROLES.branch("1"));
    public static final RoleOfPspOid ID_PSD_2_ROLE_PSP_PI = new RoleOfPspOid(
        ETSI_PSD_2_ROLES.branch("2"));
    public static final RoleOfPspOid ID_PSD_2_ROLE_PSP_AI = new RoleOfPspOid(
        ETSI_PSD_2_ROLES.branch("3"));
    public static final RoleOfPspOid ROLE_OF_PSP_OID = new RoleOfPspOid(
        ETSI_PSD_2_ROLES.branch("4"));

    public RoleOfPspOid(ASN1ObjectIdentifier identifier) {
      super(identifier.getId());
    }
  }
}
