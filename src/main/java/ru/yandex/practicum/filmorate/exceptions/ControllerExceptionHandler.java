package ru.yandex.practicum.filmorate.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({ElementNotFoundException.class,ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, String> elementNotFoundExceptionHandler(RuntimeException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> exceptionHandler(Throwable exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
