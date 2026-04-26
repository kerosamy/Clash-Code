import type { UserRole } from '../enums/UserRole';
import { getUserRole } from './jwtDecoder';

export function hasAnyRole(requiredRoles: UserRole[]): boolean {
  const role = getUserRole();
  if (!role) return false;
  return requiredRoles.includes(role);
}