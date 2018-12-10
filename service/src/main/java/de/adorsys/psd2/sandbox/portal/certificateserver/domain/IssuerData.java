package de.adorsys.psd2.sandbox.portal.certificateserver.domain;

import java.security.PrivateKey;
import lombok.Data;
import org.bouncycastle.asn1.x500.X500Name;

@Data
public class IssuerData {

  private X500Name x500name;
  private PrivateKey privateKey;
}
