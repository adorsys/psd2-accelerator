package de.adorsys.certificateserver.utils;

import de.adorsys.certificateserver.domain.NCAId;
import de.adorsys.certificateserver.domain.NCAName;
import de.adorsys.certificateserver.domain.RolesOfPSP;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;

public class Psd2Utils {
    public static DERSequence psd2QcType(RolesOfPSP rolesOfPSP, NCAName nCAName, NCAId nCAId){
        return new DERSequence(new ASN1Encodable[] { rolesOfPSP, nCAName, nCAId });
    }
}
