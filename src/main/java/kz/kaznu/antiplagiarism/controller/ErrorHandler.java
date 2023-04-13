package kz.kaznu.antiplagiarism.controller;

import kz.kaznu.antiplagiarism.exception.BadRequestException;
import kz.kaznu.antiplagiarism.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError(Exception e) {
        log.error("Uncaught error", e);
        String message = e.getMessage();
        int status = e instanceof InternalServerErrorException ? 500
                : e instanceof BadRequestException ? 400 : 500;
        return ResponseEntity.status(status).body(message);
    }
}
