import { ProjectStatus } from '../../../shared/enums/project';

export interface ProjectResponse {
  id: string;
  name: string;
  description: string | null;
  status: ProjectStatus;
  leaderId: string;
  leaderName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  name: string;
  description: string;
}
