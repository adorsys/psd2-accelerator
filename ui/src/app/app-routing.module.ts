import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'developer-portal', pathMatch: 'full' },
  {
    path: 'certificate-service',
    loadChildren: './cert-service/cert-service.module#CertServiceModule',
  },
  {
    path: 'developer-portal',
    loadChildren: './dev-portal/dev-portal.module#DevPortalModule',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
