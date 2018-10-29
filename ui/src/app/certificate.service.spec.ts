import { TestBed } from '@angular/core/testing';

import { CertificateService } from './certificate.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('CertificateService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [RouterTestingModule, HttpClientTestingModule]
  }));

  it('should be created', () => {
    const service: CertificateService = TestBed.get(CertificateService);
    expect(service).toBeTruthy();
  });
});
