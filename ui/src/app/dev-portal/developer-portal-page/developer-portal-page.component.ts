import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../language.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sb-developer-portal-page',
  templateUrl: './developer-portal-page.component.html',
  styleUrls: ['./developer-portal-page.component.scss'],
})
export class DeveloperPortalPageComponent implements OnInit {
  public localizedContent$: Observable<string>;

  constructor(private languageService: LanguageService) {}

  ngOnInit() {
    this.localizedContent$ = this.languageService
      .getLanguage$()
      .pipe(map(lang => `assets/docs/${lang}/developer-portal-page.md`));
  }
}
