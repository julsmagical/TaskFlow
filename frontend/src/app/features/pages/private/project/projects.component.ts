import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProjectService } from '../../../services/private/project.service';
import { ProjectResponse } from '../../../interfaces/private/project.interface';
import { TaskService } from '../../../services/private/task.service';
import { TaskResponse } from '../../../interfaces/private/task.interface';
import { SessionStore } from '../../../services/auth/session-store.service';
import { ProjectStatus } from '../../../../shared/enums/project';
import { CommonModule } from '@angular/common';
import { ProjectCardComponent } from './project-card/project-card';
import { MatDialog } from '@angular/material/dialog';
import { ProjectFormComponent } from './project-form/project-form';
import { ConfirmDialogComponent } from './project-confirm/project-confirm';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [
    ProjectCardComponent,
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatSelectModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.scss',
})
export class ProjectsComponent implements OnInit {
  private readonly projectService = inject(ProjectService);
  private readonly taskService = inject(TaskService);
  readonly sessionStore = inject(SessionStore);

  private readonly dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  readonly projects = signal<ProjectResponse[]>([]);
  readonly loading = signal(false);
  readonly selectedStatus = signal<ProjectStatus | null>(null);

  readonly search = signal('');

  readonly tasks = signal(new Map<string, TaskResponse[]>());

  readonly loadedProjects = signal(new Set<string>());

  readonly filteredProjects = computed(() => {
    const text = this.search().trim().toLowerCase();

    return this.projects().filter((project) => project.name.toLowerCase().includes(text));
  });

  readonly leaderProjects = computed(() =>
    this.filteredProjects().filter((project) => project.leaderId === this.sessionStore.user()?.id),
  );

  readonly memberProjects = computed(() =>
    this.filteredProjects().filter((project) => project.leaderId !== this.sessionStore.user()?.id),
  );

  get showLeaderSection(): boolean {
    return this.sessionStore.isLeader() || this.sessionStore.isAdmin();
  }

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading.set(true);

    this.projectService.findAll(this.selectedStatus() ?? undefined).subscribe({
      next: (projects) => {
        this.projects.set(projects);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  canManage(project: ProjectResponse): boolean {
    return (
      this.sessionStore.isAdmin() ||
      (this.sessionStore.isLeader() && project.leaderId === this.sessionStore.user()?.id)
    );
  }

  canCreateTask(project: ProjectResponse): boolean {
    return this.canManage(project);
  }

  onStatusChange(status: ProjectStatus | null): void {
    this.selectedStatus.set(status);
    this.loadProjects();
  }

  loadTasks(projectId: string): void {
    if (this.loadedProjects().has(projectId)) {
      return;
    }

    this.taskService.findByProject(projectId).subscribe((tasks) => {
      const map = new Map(this.tasks());

      map.set(projectId, tasks);

      this.tasks.set(map);

      const loaded = new Set(this.loadedProjects());

      loaded.add(projectId);

      this.loadedProjects.set(loaded);
    });
  }

  getTasks(projectId: string): TaskResponse[] {
    return this.tasks().get(projectId) ?? [];
  }

  createProject(): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '600px',
    });

    dialogRef.afterClosed().subscribe((created) => {
      if (!created) {
        return;
      }

      this.projects.update((projects) => [created, ...projects]);
    });
  }

  editProject(project: ProjectResponse): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '600px',
      data: {
        project,
      },
    });

    dialogRef.afterClosed().subscribe((updated) => {
      if (!updated) {
        return;
      }

      this.projects.update((projects) => projects.map((p) => (p.id === updated.id ? updated : p)));
    });
  }

  deleteProject(project: ProjectResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '380px',
      data: {
        title: 'Eliminar proyecto',
        message: `¿Seguro que deseas eliminar "${project.name}"? Esta acción no se puede deshacer.`,
        confirmLabel: 'Eliminar',
        variant: 'danger',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.projectService.delete(project.id).subscribe(() => {
        this.projects.update((projects) => projects.filter((p) => p.id !== project.id));
      });
    });
  }

  archiveProject(project: ProjectResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '380px',
      data: {
        title: 'Archivar proyecto',
        message: `"${project.name}" pasará a estado archivado y dejará de aparecer en proyectos activos. Podrás encontrarlo filtrando por estado.`,
        confirmLabel: 'Archivar',
        variant: 'neutral',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.projectService.archive(project.id).subscribe({
        next: (archived) => {
          this.projects.update((projects) =>
            projects.map((p) => (p.id === archived.id ? archived : p)),
          );
        },
        error: (err) => {
          this.snackBar.open(
            err.error?.message ?? 'Ocurrió un error al archivar el proyecto.',
            'Cerrar',
            {
              duration: 5000,
            },
          );
        },
      });
    });
  }

  createTask(project: ProjectResponse): void {}
}
