package ru.ersted.module_1spirngmvc.exception.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.exception.dto.ExceptionResponse;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class BusinessLogicAdviceController {

    @ExceptionHandler(exception = NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build()
                );
    }

}
