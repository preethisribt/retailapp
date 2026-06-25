package com.preethisri.retailapp.Exception;

import com.mysql.cj.x.protobuf.Mysqlx;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException exp) {
        ErrorResponse error = new ErrorResponse(404, exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyExistsException(ProductAlreadyExistsException exp) {
        ErrorResponse error = new ErrorResponse(409, exp.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        String message = exp.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exp) {
        String message = exp.getMessage();

        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = invalidFormatException.getPath()
                    .stream()
                    .findFirst()
                    .map(ref -> ref.getPropertyName())
                    .orElse("field");

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, field + " has invalid value"));
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, "Invalid request body"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exp) {
        String message = exp.getConstraintViolations()
                .stream()
                .map(v -> {
                    String str = v.getPropertyPath().toString();
                    return str.substring(str.lastIndexOf(".") + 1) + " " + v.getMessage();
                })
                .findFirst()
                .orElse("Invalid request");

        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
