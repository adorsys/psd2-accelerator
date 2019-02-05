import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './faqs.routing';
import { FormsModule } from '@angular/forms';
import { FaqsPageComponent } from './faqs-page/faqs-page.component';
import { SharedModule } from '../common/shared.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    SharedModule,
  ],
  declarations: [FaqsPageComponent],
})
export class FaqsModule {}
