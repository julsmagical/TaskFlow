import { TaskStatus } from '../enums/task';
import { TaskPriority } from '../enums/task';

export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  [TaskStatus.PENDIENTE]: 'Pendiente',
  [TaskStatus.EN_PROGRESO]: 'En progreso',
  [TaskStatus.EN_REVISION]: 'En revisión',
  [TaskStatus.COMPLETADA]: 'Completada',
};

export const TASK_PRIORITY_LABELS: Record<TaskPriority, string> = {
  [TaskPriority.ALTO]: 'Alta',
  [TaskPriority.MEDIO]: 'Media',
  [TaskPriority.BAJO]: 'Baja',
};
