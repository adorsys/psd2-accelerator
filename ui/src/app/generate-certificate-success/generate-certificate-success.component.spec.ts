import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CertificateService } from '../certificate.service';
import { GenerateCertificateSuccessComponent } from './generate-certificate-success.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CertificateResponse } from '../../models/certificateResponse';
import { of } from 'rxjs';

describe('GenerateCertificateSuccessComponent', () => {
  let component: GenerateCertificateSuccessComponent;
  let fixture: ComponentFixture<GenerateCertificateSuccessComponent>;
  let certService: CertificateService;
  const certResponse: CertificateResponse = {
    encodedCert: '-----BEGIN CERTIFICATE-----BAR-----END CERTIFICATE-----',
    privateKey: '-----BEGIN RSA PRIVATE KEY-----FOO-----END RSA PRIVATE KEY-----',
    keyId: '1612748784',
    algorithm: 'SHA256WITHRSA'
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [CertificateService],
      imports: [HttpClientTestingModule],
      declarations: [GenerateCertificateSuccessComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateCertificateSuccessComponent);
    component = fixture.componentInstance;
    certService = TestBed.get(CertificateService);
    spyOn(certService, 'loadCertResponse').and.returnValue(of(certResponse));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should generate an url for downloading zip', async(() => {
    expect(component.downloadCertAndKeyUrl).toBe(undefined);
    const blob = new Blob(['Test']);
    spyOn(GenerateCertificateSuccessComponent, 'generateZipFile').and.returnValue(Promise.resolve(blob));
    component.generateDownloadZipUrl();
    fixture.whenStable().then(() => {
      expect(component.downloadCertAndKeyUrl).not.toBe(undefined);
    });
  }));

  it('should create zip file', async( () => {
    const blob1 = new Blob(['Blob1']);
    const blob2 = new Blob(['Blob2']);
    const zip = GenerateCertificateSuccessComponent.generateZipFile(blob1, blob2);
    fixture.whenStable().then(() => {
      expect(zip).not.toBe(undefined);
    });
  }));
});