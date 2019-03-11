import { Injectable } from '@angular/core';
import { Language } from '../../../models/language';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MockLanguageService {
  setLanguage(newLanguage: Language) {}

  getLanguage$(): Observable<Language> {
    return of(Language.en);
  }
}
