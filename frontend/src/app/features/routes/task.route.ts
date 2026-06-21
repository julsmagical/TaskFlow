import { Routes } from "@angular/router";
import { TasksComponent } from "../pages/private/task/task.component";
import { TaskDetailComponent } from "../pages/private/task/task-detail/task-detail";

export const taskRoutes: Routes = [
    { path: '', component: TasksComponent },
    { path: ':id', component: TaskDetailComponent },
]