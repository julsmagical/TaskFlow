import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-public-layout',
  imports: [RouterOutlet],
  templateUrl: './private-layout.html',
  styleUrls: ['./private-layout.scss'],
})
export class PrivateLayout {}
