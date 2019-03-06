import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { RouterModule } from '@angular/router';
import { LanguageSwitchComponent } from './language-switch/language-switch.component';

@NgModule({
  declarations: [HeaderComponent, LanguageSwitchComponent],
  imports: [CommonModule, RouterModule],
  exports: [HeaderComponent],
})
export class SharedModule {}
