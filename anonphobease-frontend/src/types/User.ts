import type { Role } from "./Role";

export interface User {
  id: string;
  username: string;
  isActive: boolean;
  createdAt: string;
  role: Role;
}
