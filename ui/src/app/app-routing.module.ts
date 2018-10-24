import { NgModule } from '@angular/core';
import { GenerateCertificatePageComponent } from './generate-certificate-page/generate-certificate-page.component';
import { RouterModule, Routes } from '@angular/router';


const routes: Routes = [
  {path: '', component: GenerateCertificatePageComponent}, // home
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
