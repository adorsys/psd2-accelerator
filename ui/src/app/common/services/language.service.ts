import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Language } from '../../../models/language';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private language = Language.en;

  private subject = new BehaviorSubject(Language.en);

  constructor(public ngxTranslate: TranslateService) {
    ngxTranslate.setDefaultLang(this.subject.getValue());
    ngxTranslate.addLangs(['en', 'de']);
  }

  setLanguage(newLanguage: Language) {
    this.language = newLanguage;
    this.ngxTranslate.use(this.language);
    this.subject.next(newLanguage);
  }

  getLanguage$(): Observable<Language> {
    return this.subject.asObservable();
  }
}
