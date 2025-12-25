package com.capstone.huddle.common.exception;

import com.capstone.huddle.common.ApiResponse;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.LazyInitializationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler to prevent stack traces from being exposed to users.
 * All exceptions are caught and returned with user-friendly messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired(required = false)
    private Counter apiErrorCounter;

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed: {}", errors);
        incrementErrorCounter();

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle authentication errors
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication failed. Please check your credentials."));
    }

    /**
     * Handle bad credentials
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password."));
    }

    /**
     * Handle access denied errors
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You do not have permission to access this resource."));
    }

    /**
     * Handle resource not found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NoHandlerFoundException ex) {
        logger.warn("Resource not found: {}", ex.getRequestURL());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("The requested resource was not found."));
    }

    /**
     * Handle entity not found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("The requested item was not found."));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle data integrity violations (duplicate entries, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("A data conflict occurred. This might be due to duplicate data."));
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        logger.warn("Missing parameter: {}", ex.getParameterName());
        incrementErrorCounter();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Required parameter '" + ex.getParameterName() + "' is missing."));
    }

    /**
     * Handle method argument type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch for parameter: {}", ex.getName());
        incrementErrorCounter();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid value for parameter '" + ex.getName() + "'."));
    }

    /**
     * Handle HTTP method not supported
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.warn("Method not supported: {}", ex.getMethod());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("HTTP method '" + ex.getMethod() + "' is not supported for this endpoint."));
    }

    /**
     * Handle unsupported media type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        logger.warn("Media type not supported: {}", ex.getContentType());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error("Media type '" + ex.getContentType() + "' is not supported."));
    }

    /**
     * Handle malformed JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Malformed request body: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Malformed request body. Please check your JSON format."));
    }

    /**
     * Handle file upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        logger.warn("File upload size exceeded: {}", ex.getMessage());
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File size exceeds the maximum allowed limit."));
    }

    /**
     * Handle Hibernate LazyInitializationException
     */
    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<ApiResponse<Object>> handleLazyInitializationException(LazyInitializationException ex) {
        logger.error("Lazy initialization error - this indicates a code issue: ", ex);
        incrementErrorCounter();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unable to load data. Please try again."));
    }

    /**
     * Handle RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime error occurred: {}", ex.getMessage());
        incrementErrorCounter();

        // Check if it's a "not found" type message
        String message = ex.getMessage();
        if (message != null && (message.contains("not found") || message.contains("Not found"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(message));
        }

        // For other runtime exceptions, return a generic message
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred while processing your request."));
    }

    /**
     * Handle all other exceptions - this is the catch-all handler
     * that prevents stack traces from being exposed to users
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        // Log the full exception for debugging purposes
        logger.error("Unexpected error occurred: ", ex);
        incrementErrorCounter();

        // Return a generic message to the user without exposing internal details
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }

    private void incrementErrorCounter() {
        if (apiErrorCounter != null) {
            apiErrorCounter.increment();
        }
    }
}
