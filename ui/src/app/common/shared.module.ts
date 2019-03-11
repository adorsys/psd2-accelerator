import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { RouterModule } from '@angular/router';
import { LanguageSwitchComponent } from './language-switch/language-switch.component';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [HeaderComponent, LanguageSwitchComponent],
  imports: [CommonModule, RouterModule, TranslateModule],
  exports: [HeaderComponent, TranslateModule],
})
export class SharedModule {}
