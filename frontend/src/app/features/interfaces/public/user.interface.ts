import { UserRole } from '../../../shared/enums/user';

export interface Role {
  id: String;
  name: String;
}

export interface User {
  id: String;
  username: String;
  fullname: String;
  email: String;
  password: String;
  role: Role;
  createdAt: String;
}

export interface UserRequest {
  username: String;
  fullname: String;
  email: String;
  password: String;
  role: UserRole;
}

export interface AuthenticatedUser {
  id: string;
  username: string;
  role: string;
}

// para asignar tarea
export interface SelectableUser {
  id: string;
  username: string;
  fullname: string;
  role: UserRole;
}
