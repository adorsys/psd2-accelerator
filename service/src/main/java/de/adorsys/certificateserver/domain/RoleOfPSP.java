package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;

public class RoleOfPSP extends DERSequence {
    public static final RoleOfPSP PSP_AS = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_as, RoleOfPspName.PSP_AS);
    public static final RoleOfPSP PSP_PI = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_pi, RoleOfPspName.PSP_PI);
    public static final RoleOfPSP PSP_AI = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_ai, RoleOfPspName.PSP_AI);
    public static final RoleOfPSP PSP_IC = new RoleOfPSP(RoleOfPspOid.id_psd2_role_psp_ic, RoleOfPspName.PSP_IC);

    private RoleOfPSP(RoleOfPspOid roleOfPspOid, RoleOfPspName roleOfPspName) {
        super(new ASN1Encodable[]{roleOfPspOid, roleOfPspName});
    }
}
