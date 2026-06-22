import { Component, inject } from '@angular/core';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../services/private/user.service';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-public-layout',
  imports: [MatCard, MatIcon, RouterLink],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomeComponent {
  fullname = 'Usuario';

  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  ngOnInit(): void {
    const userId = this.authService.getUserId();

    if (!userId) {
      return;
    }

    this.userService.findById(userId).subscribe({
      next: (user) => {
        this.fullname = user.fullname;
      },
      error: () => {
        this.fullname = 'Usuario';
      },
    });
  }
}
