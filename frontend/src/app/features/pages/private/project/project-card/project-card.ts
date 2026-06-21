import { CommonModule, NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProjectResponse } from '../../../../interfaces/private/project.interface';
import { TaskResponse } from '../../../../interfaces/private/task.interface';
import { TaskPriority, TaskStatus } from '../../../../../shared/enums/task';
import { ProjectStatus } from '../../../../../shared/enums/project';

@Component({
  selector: 'app-project-card',
  standalone: true,
  imports: [
    CommonModule,
    NgClass,
    MatExpansionModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
    MatChipsModule,
  ],
  templateUrl: './project-card.html',
  styleUrl: './project-card.scss',
})
export class ProjectCardComponent {
  readonly ProjectStatus = ProjectStatus;
  readonly TaskStatus = TaskStatus;
  readonly TaskPriority = TaskPriority;

  @Input({ required: true })
  project!: ProjectResponse;

  @Input({ required: true })
  tasks: TaskResponse[] = [];

  @Input()
  canManage = false;

  @Input()
  canCreateTask = false;

  @Output()
  loadTasks = new EventEmitter<string>();

  @Output()
  createTask = new EventEmitter<ProjectResponse>();

  @Output()
  edit = new EventEmitter<ProjectResponse>();

  @Output()
  archive = new EventEmitter<ProjectResponse>();

  @Output()
  delete = new EventEmitter<ProjectResponse>();

  onOpened(): void {
    this.loadTasks.emit(this.project.id);
  }

  onCreateTask(): void {
    this.createTask.emit(this.project);
  }

  onEdit(): void {
    this.edit.emit(this.project);
  }

  onArchive(): void {
    this.archive.emit(this.project);
  }

  onDelete(): void {
    this.delete.emit(this.project);
  }
}
