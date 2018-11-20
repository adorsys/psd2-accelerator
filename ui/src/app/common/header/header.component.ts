import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'sb-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @Input() text: string;

  constructor() { }

  ngOnInit() {
  }

}
