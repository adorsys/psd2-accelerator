import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient} from '@angular/common/http';
import { CertificateResponse } from '../models/certificateResponse';
import { CertificateData } from '../models/certificateData';


@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  CREATE_CERT_URL = `/backend/api/cert-generator`;
  certResponse: CertificateResponse;
  constructor(private httpClient: HttpClient) {}


  createCertificate(certData: CertificateData): Observable<CertificateResponse> {
    return this.httpClient.post<CertificateResponse>(this.CREATE_CERT_URL, certData);
  }

  saveCertResponse(data: CertificateResponse) {
    this.certResponse = data;
  }

  loadCertResponse(): CertificateResponse {
    return this.certResponse;
  }
}
