package de.adorsys.psd2.sandbox.certificate.domain;

import org.bouncycastle.asn1.DERUTF8String;

public class NcaId extends DERUTF8String {

  public NcaId(String string) {
    super(string);
  }
}
