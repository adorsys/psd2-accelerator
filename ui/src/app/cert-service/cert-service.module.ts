import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './cert-service.routing';
import { FormsModule } from '@angular/forms';
import { GenerateCertificatePageComponent } from './generate-certificate-page/generate-certificate-page.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
  ],
  declarations: [
    GenerateCertificatePageComponent
  ]
})
export class CertServiceModule {
}
