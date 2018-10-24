package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.DERSequence;

public class RolesOfPSP extends DERSequence {
    public RolesOfPSP(RoleOfPSP[] array) {
        super(array);
    }
}
