import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../services/language.service';
import { Language } from '../../../models/language';
import { Observable } from 'rxjs';
import { ConfigService } from '../services/config.service';
import { Config } from '../../../models/config';

@Component({
  selector: 'sb-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  language$: Observable<Language>;
  config: Config;

  constructor(
    private languageService: LanguageService,
    private configService: ConfigService
  ) {
    this.language$ = this.languageService.getLanguage$();
    this.config = configService.getConfig();
  }

  ngOnInit() {}

  updateLanguage(language: Language) {
    this.languageService.setLanguage(language);
  }
}
