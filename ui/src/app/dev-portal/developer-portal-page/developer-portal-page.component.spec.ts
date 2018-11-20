import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeveloperPortalPageComponent } from './developer-portal-page.component';
import { DevPortalModule } from '../dev-portal.module';
import { RouterModule } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('DeveloperPortalPageComponent', () => {
  let component: DeveloperPortalPageComponent;
  let fixture: ComponentFixture<DeveloperPortalPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [DevPortalModule]
    }).overrideModule(DevPortalModule, {
      remove: {
        imports: [RouterModule]
      },
      add: {
        imports: [HttpClientTestingModule, RouterTestingModule]
      }
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeveloperPortalPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
