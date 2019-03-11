import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { LanguageSwitchComponent } from './language-switch.component';
import { HeaderComponent } from '../header/header.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Language } from '../../../models/language';
import { By } from '@angular/platform-browser';
import { MockModule } from '../mock/mock.module';

describe('LanguageSwitchComponent', () => {
  let component: LanguageSwitchComponent;
  let fixture: ComponentFixture<LanguageSwitchComponent>;
  let buttonDe;
  let buttonEn;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LanguageSwitchComponent, HeaderComponent],
      imports: [
        RouterTestingModule,
        FormsModule,
        HttpClientTestingModule,
        MockModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageSwitchComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    buttonDe = fixture.debugElement.query(
      By.css('input[id = language-switcher-de]')
    ).nativeElement;
    buttonEn = fixture.debugElement.query(
      By.css('input[id = language-switcher-en]')
    ).nativeElement;
  });

  it('should set german button to active when language is german', () => {
    component.selectedLanguage = Language.de;
    fixture.detectChanges();
    expect(buttonDe.checked).toBeTruthy();
    expect(buttonEn.checked).toBeFalsy();
  });

  it('should emit english when english button is clicked', () => {
    component.selectedLanguage = Language.de;
    fixture.detectChanges();
    spyOn(component.languageChange, 'emit');
    buttonEn.click();
    fixture.detectChanges();
    expect(component.languageChange.emit).toHaveBeenCalledWith(Language.en);
    expect(component.languageChange.emit).not.toHaveBeenCalledWith(Language.de);
  });

  it('should not do anything and return no error when no language is set', () => {
    fixture.detectChanges();
    expect(buttonDe.checked).toBeFalsy();
    expect(buttonEn.checked).toBeFalsy();
    spyOn(component.languageChange, 'emit');
    expect(component.languageChange.emit).not.toHaveBeenCalled();
  });
});
