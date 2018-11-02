package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.DERUTF8String;

public class NCAId extends DERUTF8String {

  public NCAId(String string) {
    super(string);
  }
}
