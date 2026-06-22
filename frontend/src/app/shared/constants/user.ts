import { UserRole } from '../enums/user';

export const ROLE_NAME_LABELS: Record<UserRole, string> = {
  [UserRole.DESARROLLADOR]: 'Desarrollador',
  [UserRole.LIDER]: 'Líder',
  [UserRole.ADMINISTRADOR]: 'Admnistrador',
};
