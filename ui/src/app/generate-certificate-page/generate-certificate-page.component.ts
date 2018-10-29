import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../certificate.service';
import {CertificateData} from '../../models/certificateData';

@Component({
  selector: 'app-generate-certificate-page',
  templateUrl: './generate-certificate-page.component.html',
  styleUrls: ['./generate-certificate-page.component.css']
})
export class GenerateCertificatePageComponent implements OnInit {
  certData: CertificateData;

  constructor(private router: Router, private route: ActivatedRoute, private certService: CertificateService) {
  }

  ngOnInit() {
    this.certData = {
      aisp: false,
      piisp: false,
      pisp: false,
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
        this.certService.saveCertResponse(data);
        this.router.navigate(['success']);
      },
      // TODO Refactoring
      error => {
        alert(error.message);
      }
    );
  }

  onSelectPiisp() {
    this.certData.piisp = !this.certData.piisp;
  }

  onSelectAisp() {
    this.certData.aisp = !this.certData.aisp;
  }

  onSelectPisp() {
    this.certData.pisp = !this.certData.pisp;
  }

  onClickCancel() {
  }
}
