import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../language.service';
import { Language } from '../../../models/language';
import { Observable } from 'rxjs';

@Component({
  selector: 'sb-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  public selectedLanguage$: Observable<Language>;
  public languages = Language;

  constructor(private languageService: LanguageService) {
    this.selectedLanguage$ = languageService.getLanguage$();
  }

  ngOnInit() {}

  onSelectLanguage(selectedLanguage: Language) {
    this.languageService.setLanguage(selectedLanguage);
  }
}
