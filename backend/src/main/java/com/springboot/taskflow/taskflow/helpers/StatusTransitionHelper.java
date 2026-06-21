package com.springboot.taskflow.taskflow.helpers;

import org.springframework.stereotype.Component;

import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.BusinessRuleException;

@Component
public class StatusTransitionHelper {
    public void validateTransition(TaskStatus current, TaskStatus next) {
        boolean valid = switch (current) {
            case PENDIENTE   -> next == TaskStatus.EN_PROGRESO;
            case EN_PROGRESO -> next == TaskStatus.EN_REVISION
                             || next == TaskStatus.COMPLETADA
                             || next == TaskStatus.PENDIENTE;
            case EN_REVISION -> next == TaskStatus.EN_PROGRESO
                             || next == TaskStatus.COMPLETADA;
            case COMPLETADA  -> false;
        };

        if (!valid) {
            throw new BusinessRuleException(
                "Transición de estado inválida: %s → %s.".formatted(current, next)
            );
        }
    }
}
