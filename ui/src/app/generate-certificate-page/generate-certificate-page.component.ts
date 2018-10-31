import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../certificate.service';
import { CertificateRequest } from '../../models/certificateRequest';
import { PspRole } from '../../models/PspRole';

@Component({
  selector: 'app-generate-certificate-page',
  templateUrl: './generate-certificate-page.component.html',
  styleUrls: ['./generate-certificate-page.component.scss']
})
export class GenerateCertificatePageComponent implements OnInit {
  certData: CertificateRequest;
  pspRolesKeys = Object.keys(PspRole);
  error: any;

  constructor(private router: Router, private route: ActivatedRoute, private certService: CertificateService) {
  }

  ngOnInit() {
    this.certData = {
      roles: [PspRole.PIS],
      authorizationNumber: '87B2AC',
      countryName: 'Germany',
      domainComponent: 'public.corporation.de',
      localityName: 'Nuremberg',
      organizationName: 'Fictional Corporation AG',
      organizationUnit: 'Information Technology',
      stateOrProvinceName: 'Bayern',
      validity: 365
    };
  }

  onClickContinue() {
    this.certService.createCertificate(this.certData).subscribe(
      data => {
        this.error = undefined;
        this.certService.saveCertResponse(data);
        this.router.navigate(['success']);
      },
      error => {
        this.error = error;
      }
    );
  }

  onClickCancel() {
  }

  onSelectPspRole(pspRole: string) {
    if (this.isPspRoleSelected(pspRole)) {
      this.certData.roles = this.certData.roles.filter(psp => psp !== PspRole[pspRole]);
    } else {
      this.certData.roles.push(PspRole[pspRole]);
    }
  }

  isPspRoleSelected(pspRole: string): boolean {
    return this.certData.roles.includes(PspRole[pspRole]);
  }
}
