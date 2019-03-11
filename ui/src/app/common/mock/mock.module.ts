import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MockMarkdownComponent } from './mock-markdown.component';
import { MockTranslatePipe } from './mock-translate.pipe';
import { LanguageService } from '../services/language.service';
import { MockLanguageService } from './mock-language.service';

@NgModule({
  declarations: [MockMarkdownComponent, MockTranslatePipe],
  providers: [{ provide: LanguageService, useClass: MockLanguageService }],
  imports: [CommonModule],
  exports: [MockMarkdownComponent, MockTranslatePipe],
})
export class MockModule {}
