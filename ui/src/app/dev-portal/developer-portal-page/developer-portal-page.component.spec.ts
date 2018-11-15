import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeveloperPortalPageComponent } from './developer-portal-page.component';

describe('DeveloperPortalPageComponent', () => {
  let component: DeveloperPortalPageComponent;
  let fixture: ComponentFixture<DeveloperPortalPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeveloperPortalPageComponent ]
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
