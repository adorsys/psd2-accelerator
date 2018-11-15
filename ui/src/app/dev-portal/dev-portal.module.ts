import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './dev-portal.routing';
import { FormsModule } from '@angular/forms';
import { DeveloperPortalPageComponent } from './developer-portal-page/developer-portal-page.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
  ],
  declarations: [
    DeveloperPortalPageComponent
  ]
})
export class DevPortalModule {
}
