import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';
import { LanguageSwitchComponent } from '../language-switch/language-switch.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MockModule } from '../mock/mock.module';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HeaderComponent, LanguageSwitchComponent],
      imports: [HttpClientTestingModule, MockModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
