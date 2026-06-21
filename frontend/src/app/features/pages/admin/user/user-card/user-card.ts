import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { SelectableUser } from '../../../../interfaces/public/user.interface';
import { UserRole } from '../../../../../shared/enums/user';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './user-card.html',
  styleUrl: './user-card.scss',
})
export class UserCardComponent {
  readonly user = input.required<SelectableUser>();

  private readonly roleColors: Record<UserRole, string> = {
    [UserRole.ADMINISTRADOR]: '#ad1457',
    [UserRole.LIDER]: '#6a4ea3',
    [UserRole.DESARROLLADOR]: '#2e7d6f',
  };

  get initials(): string {
    return this.user()
      .fullname.split(' ')
      .map((part) => part[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  get avatarColor(): string {
    return this.roleColors[this.user().role];
  }
}
