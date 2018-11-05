package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.DERUTF8String;

public class NcaName extends DERUTF8String {

  public NcaName(String string) {
    super(string);
  }

}
