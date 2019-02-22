import { Component, OnInit } from '@angular/core';

import { LanguageService } from '../../language.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'sb-faqs-page',
  templateUrl: './faqs-page.component.html',
  styleUrls: ['./faqs-page.component.scss'],
})
export class FaqsPageComponent implements OnInit {
  public localizedContent$: Observable<string>;

  constructor(private languageService: LanguageService) {}

  ngOnInit() {
    this.localizedContent$ = this.languageService
      .getLanguage$()
      .pipe(map(lang => `assets/docs/${lang}/faq-page.md`));
  }
}
