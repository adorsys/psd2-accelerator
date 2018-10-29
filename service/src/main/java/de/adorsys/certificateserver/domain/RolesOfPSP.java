package de.adorsys.certificateserver.domain;

import org.bouncycastle.asn1.DERSequence;

import java.util.ArrayList;
import java.util.List;

public class RolesOfPSP extends DERSequence {

    public static RolesOfPSP fromCertificateRequest(CertificateRequest certificateRequest) {
        List<RoleOfPSP> roles = new ArrayList<>();
        // TODO: How does the role ASPSP differ to the others?
        //if (certificateRequest.isASPSP()) roles.add(RoleOfPSP.PSP_AS);
        if (certificateRequest.isPisp()) roles.add(RoleOfPSP.PSP_PI);
        if (certificateRequest.isAisp()) roles.add(RoleOfPSP.PSP_AI);
        if (certificateRequest.isPiisp()) roles.add(RoleOfPSP.PSP_IC);

        return new RolesOfPSP(roles.toArray(new RoleOfPSP[]{}));
    }

    public RolesOfPSP(RoleOfPSP[] array) {
        super(array);
    }
}
