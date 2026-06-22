import { ProjectStatus } from '../enums/project';

export const PROJECT_STATUS_LABELS: Record<ProjectStatus, string> = {
  [ProjectStatus.ACTIVO]: 'Activo',
  [ProjectStatus.ARCHIVADO]: 'Archivado',
};
