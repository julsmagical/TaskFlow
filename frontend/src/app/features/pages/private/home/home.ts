import { Component } from '@angular/core';
import { MatCard } from "@angular/material/card";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-public-layout',
  imports: [MatCard, MatIcon, RouterLink],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomeComponent {
  fullname = 'Usuario';

  constructor() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.fullname = user?.fullname || 'Usuario';
  }
}
