import { Component, OnInit } from '@angular/core';
import { CertificateService } from '../certificate.service';
import { CertificateResponse } from '../../models/certificateResponse';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import JSZip from 'jszip';

@Component({
  selector: 'app-generate-certificate-success',
  templateUrl: './generate-certificate-success.component.html',
  styleUrls: ['./generate-certificate-success.component.scss']
})
export class GenerateCertificateSuccessComponent implements OnInit {

  certResponse: CertificateResponse;
  downloadCertAndKeyUrl: SafeResourceUrl;

  constructor(private certService: CertificateService, private sanitizer: DomSanitizer) { }

  static generateZipFile(certBlob, keyBlob): Promise<any> {
    const zip = new JSZip();
    zip.file('certificate.pem', certBlob);
    zip.file('private.key', keyBlob);
    return zip.generateAsync({type: 'blob'});
  }

  ngOnInit() {
    this.certResponse = this.certService.loadCertResponse();
    this.generateDownloadZipUrl();
  }

  generateDownloadZipUrl() {
    const blobCert = new Blob([this.certResponse.encodedCert], {type: 'text/plain'});
    const blobKey = new Blob([this.certResponse.privateKey], {type: 'text/plain'});
    GenerateCertificateSuccessComponent.generateZipFile(blobCert, blobKey).then(zip => {
      this.downloadCertAndKeyUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(zip));
      }
    );
  }
}
