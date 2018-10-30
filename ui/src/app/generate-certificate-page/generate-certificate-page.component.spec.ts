import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { GenerateCertificatePageComponent } from './generate-certificate-page.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CertificateRequest } from '../../models/certificateRequest';
import { PspRole } from '../../models/pspRole';

describe('GenerateCertificatePageComponent', () => {
  let component: GenerateCertificatePageComponent;
  let fixture: ComponentFixture<GenerateCertificatePageComponent>;
  let certData: CertificateRequest;

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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add a new role', () => {
    component.onSelectPspRole('AIS');
    expect(component.certData.roles.includes(PspRole['AIS'])).toBe(true);
  });

  it('should remove a role', () => {
    const role = 'AIS';
    component.onSelectPspRole(role);
    component.onSelectPspRole(role);
    expect(component.certData.roles.includes(PspRole[role])).toBe(false);
  });

  it('should check for current roles', () => {
    expect(component.isPspRoleSelected('PIS')).toBe(true);
    expect(component.isPspRoleSelected('AIS')).toBe(false);
  });
});
