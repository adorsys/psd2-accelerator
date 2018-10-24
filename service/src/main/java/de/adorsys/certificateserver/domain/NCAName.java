package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.DERUTF8String;

public class NCAName extends DERUTF8String {
    public NCAName(String string) {
        super(string);
    }

}
