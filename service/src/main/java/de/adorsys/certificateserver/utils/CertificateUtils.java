package de.adorsys.certificateserver.utils;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.certificateserver.domain.IssuerData;
import de.adorsys.certificateserver.domain.SubjectData;
import de.adorsys.certificateserver.service.CertificateService;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

    /**
     *
     * @param filename Name of the key file. Suffix should be .pem
     * @return X509Certificate
     */
    public static X509Certificate getCertificateFromFile(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("certificates/" + filename);

        if (is != null) {
            try {
                byte[] bytes = IOUtils.toByteArray(is);
                return X509CertUtils.parse(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     * @param filename Name of the key file. Suffix should be .key
     * @return PrivateKey
     */
    public static PrivateKey getKeyFromFile(String filename) {
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
            LOGGER.error("Error at key extraction from file: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Generates new X.509 Certificate
     * @param subjectData
     * @param issuerData
     * @param statement
     * @return X509Certificate
     */
    public static X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, QCStatement statement) {
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
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts Objects like Certificates/Keys into Strings without '\n' or '\r'
     * @param obj
     * @return String
     */
    public static String convertObjectToString(Object obj) {
        String test = obj.toString();
        test.replaceAll("\n", "").replaceAll("\r", "");

        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        try {
            pemWriter.writeObject(obj);
            pemWriter.flush();
            pemWriter.close();
            String response = writer.toString();
            return response.replaceAll("\n", "").replaceAll("\r", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}