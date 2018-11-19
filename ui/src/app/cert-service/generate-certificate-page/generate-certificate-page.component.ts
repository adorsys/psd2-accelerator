import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../certificate.service';
import { CertificateRequest } from '../../../models/certificateRequest';
import { PspRole } from '../../../models/pspRole';
import { CertificateResponse } from '../../../models/certificateResponse';
import JSZip from 'jszip';
import { ErrorHandler } from '../../common/error-handler';
import { HttpError } from '../../../models/httpError';

@Component({
  selector: 'sb-generate-certificate-page',
  templateUrl: './generate-certificate-page.component.html',
  styleUrls: ['./generate-certificate-page.component.scss']
})
export class GenerateCertificatePageComponent implements OnInit {
  @ViewChild('certForm') certForm;
  certData: CertificateRequest;
  pspRolesKeys = Object.keys(PspRole);
  errors: Array<HttpError>;
  certResponse: CertificateResponse;

  static generateZipFile(certBlob, keyBlob): Promise<any> {
    const zip = new JSZip();
    zip.file('certificate.pem', certBlob);
    zip.file('private.key', keyBlob);
    return zip.generateAsync({type: 'blob'});
  }

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

  createAndDownloadCert() {
    this.certService.createCertificate(this.certData).subscribe(
      data => {
        this.errors = undefined;
        this.certResponse = data;
        this.generateAndDownloadZip();
      },
      error => {
        this.errors = ErrorHandler.getErrorMessages(error);
      }
    );
  }

  onSelectPspRole(pspRole: string) {
    if (this.isPspRoleSelected(pspRole)) {
      this.certData.roles = this.certData.roles.filter(psp => psp !== PspRole[pspRole]);
      if (!this.isPspRoleValid()) {
        this.certForm.form.setErrors({invalid: true});
      }
    } else {
      this.certData.roles.push(PspRole[pspRole]);
      this.certForm.form.setErrors(null);
    }
  }

  isPspRoleSelected(pspRole: string): boolean {
    return this.certData.roles.includes(PspRole[pspRole]);
  }

  isPspRoleValid(): boolean {
    return this.certData.roles.length > 0;
  }

  generateAndDownloadZip() {
    this.createZipUrl().then(url => this.downloadFile(url));
  }

  createZipUrl(): Promise<string> {
    const blobCert = new Blob([this.certResponse.encodedCert], {type: 'text/plain'});
    const blobKey = new Blob([this.certResponse.privateKey], {type: 'text/plain'});
    return GenerateCertificatePageComponent.generateZipFile(blobCert, blobKey).then( zip => {
      return window.URL.createObjectURL(zip);
    });
  }

  downloadFile(url: string) {
    const element = document.createElement('a');
    element.setAttribute('href', url);
    element.setAttribute('download', 'psu_cert.zip');
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }
}
