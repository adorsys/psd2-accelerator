import { Component, Output, OnInit, EventEmitter, Input } from '@angular/core';
import { Language } from '../../../models/language';

@Component({
  selector: 'sb-language-switch',
  templateUrl: './language-switch.component.html',
  styleUrls: ['./language-switch.component.scss'],
})
export class LanguageSwitchComponent {
  languages = Language;

  // tslint:disable-next-line:no-input-rename
  @Input('language') selectedLanguage: Language;

  @Output() languageChange = new EventEmitter<Language>();

  onLanguageChange(language: Language) {
    this.languageChange.emit(language);
  }
}
