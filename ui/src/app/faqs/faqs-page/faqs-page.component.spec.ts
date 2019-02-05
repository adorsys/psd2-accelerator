import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FaqsPageComponent } from './faqs-page.component';
import { FaqsModule } from '../faqs.module';
import { RouterModule } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('FaqsPageComponent', () => {
  let component: FaqsPageComponent;
  let fixture: ComponentFixture<FaqsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FaqsModule],
    })
      .overrideModule(FaqsModule, {
        remove: {
          imports: [RouterModule],
        },
        add: {
          imports: [HttpClientTestingModule, RouterTestingModule],
        },
      })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FaqsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
