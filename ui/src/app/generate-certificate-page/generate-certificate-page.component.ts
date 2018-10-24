import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-generate-certificate-page',
  templateUrl: './generate-certificate-page.component.html',
  styleUrls: ['./generate-certificate-page.component.css']
})
export class GenerateCertificatePageComponent implements OnInit {

  constructor(private router: Router, private route: ActivatedRoute) { }
  selectPsp: any = 'PSP_PI';
  ngOnInit() {
  }

}
