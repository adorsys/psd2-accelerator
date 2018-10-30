import { NgModule } from '@angular/core';
import { GenerateCertificatePageComponent } from './generate-certificate-page/generate-certificate-page.component';
import { RouterModule, Routes } from '@angular/router';
import { GenerateCertificateSuccessComponent } from './generate-certificate-success/generate-certificate-success.component';

const routes: Routes = [
  {path: '', component: GenerateCertificatePageComponent}, // home
  {path: 'success', component: GenerateCertificateSuccessComponent},
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes),
  ],
  exports: [
    RouterModule
  ],
})
export class AppRoutingModule { }
