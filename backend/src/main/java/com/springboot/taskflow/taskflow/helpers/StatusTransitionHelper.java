package com.springboot.taskflow.taskflow.helpers;

import org.springframework.stereotype.Component;

import com.springboot.taskflow.taskflow.enums.RoleName;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.BusinessRuleException;

@Component
public class StatusTransitionHelper {
    public void validateTransition(TaskStatus current, TaskStatus next, String roleName) {
        boolean valid = switch (current) {
            case PENDIENTE   -> next == TaskStatus.EN_PROGRESO;
            case EN_PROGRESO -> next == TaskStatus.EN_REVISION
                             || next == TaskStatus.PENDIENTE;
            case EN_REVISION -> next == TaskStatus.EN_PROGRESO
                             || next == TaskStatus.COMPLETADA;
            case COMPLETADA  -> false;
        };

        if (!valid) {
            throw new BusinessRuleException("La transición de estado de '%s' a '%s' no está permitida.".formatted(current, next));
        }

        if (RoleName.DESARROLLADOR.name().equals(roleName)) {
            boolean allowedForDesarrollador = switch (current) {
                case EN_PROGRESO -> next == TaskStatus.EN_REVISION || next == TaskStatus.PENDIENTE;
                default -> false;
            };
 
            if (!allowedForDesarrollador) {
                throw new BusinessRuleException("Los desarrolladores solo pueden cambiar tareas 'EN_PROGRESO' a 'EN_REVISION' o 'PENDIENTE'.");
            }
        }
    }

    public void validateTransition(TaskStatus current, TaskStatus next) {
        validateTransition(current, next, RoleName.LIDER.name());
    }
}
