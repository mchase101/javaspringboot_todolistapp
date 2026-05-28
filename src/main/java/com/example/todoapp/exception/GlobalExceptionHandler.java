package com.example.todoapp.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Order
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

       @ExceptionHandler(ResourceNotFoundException.class)
       protected ResponseEntity<Object> httpEntityNotFound(ResourceNotFoundException e){

           // store our response as a HashMap
           Map<String, String> errorResponse = new HashMap<>();
           errorResponse.put("error", e.getMessage());

           return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
       }

    //2. When user request for a resource (e.g. Customer or Feedback not found),
    // ResourceNotFoundException will be thrown, managed by httpEntityNotFound
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        // Use the custom exception, called MessageNotReadableException
        MessageNotReadableException messageNotReadableException = new MessageNotReadableException();

        // store our response as a HashMap
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", messageNotReadableException.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 3. When user sends a requestbody that is empty OR incomplete,
    // handleMethodArugmentNotValid is invoked (tracked by @Valid annotation)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        // create a hashmap to store the field(s) and its related error
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((err)->{
            String field = ((FieldError) err).getField();
            String errMessage = err.getDefaultMessage();
            errors.put(field, errMessage);
        });

        // store the errors as an Object in errorResponse
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}





