import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateCertificatePageComponent } from './generate-certificate-page.component';

describe('GenerateCertificatePageComponent', () => {
  let component: GenerateCertificatePageComponent;
  let fixture: ComponentFixture<GenerateCertificatePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GenerateCertificatePageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateCertificatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
