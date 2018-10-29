import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateCertificatePageComponent } from './generate-certificate-page.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CertificateData } from '../../models/certificateData';

describe('GenerateCertificatePageComponent', () => {
  let component: GenerateCertificatePageComponent;
  let fixture: ComponentFixture<GenerateCertificatePageComponent>;
  let certData: CertificateData;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [ GenerateCertificatePageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateCertificatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    certData = {
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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set Aisp Role to true', () => {
    component.onSelectAisp();
    expect(component.certData.aisp).toBe(true);
  });

  it('should set Aisp Role to false', () => {
    component.certData.aisp = true;
    component.onSelectAisp();
    expect(component.certData.aisp).toBe(false);
  });
});
