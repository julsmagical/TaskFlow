import { UserRole } from '../../../shared/enums/user';

export interface NavigationItem {
  label: string;
  icon: string;
  route: string;
  roles?: UserRole[];
}
