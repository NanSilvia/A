package com.reservations.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reservations.dtos.EntityDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JSON utility class for handling API requests and responses
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper;
    private static final Validator validator;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Parse JSON request body to object
     */
    public static <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        return objectMapper.readValue(request.getReader(), clazz);
    }

    /**
     * Write JSON response
     */
    public static void writeJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }

    /**
     * Write success response
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        writeJsonResponse(response, data);
    }

    /**
     * Write error response
     */
    public static void writeErrorResponse(HttpServletResponse response, int statusCode, String error)
            throws IOException {
        response.setStatus(statusCode);
        writeJsonResponse(response, error);
    }

    /**
     * Write error response with multiple errors
     */
    public static void writeErrorResponse(HttpServletResponse response, int statusCode, List<String> errors)
            throws IOException {
        response.setStatus(statusCode);
        writeJsonResponse(response, errors);
    }

    /**
     * Validate object using Bean Validation
     */
    public static <T> List<String> validateObject(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    /**
     * Parse and validate request body
     */
    public static <T> ValidationResult<T> parseAndValidateRequestBody(HttpServletRequest request, Class<T> clazz) {
        try {
            T object = parseRequestBody(request, clazz);
            List<String> errors = validateObject(object);
            return new ValidationResult<>(object, errors);
        } catch (IOException e) {
            logger.error("Error parsing request body", e);
            return new ValidationResult<>(null, List.of("Invalid JSON in request body"));
        }
    }

    /**
     * Handle common servlet exceptions and send appropriate error responses
     */
    public static void handleException(HttpServletResponse response, Exception e) {
        logger.error("Servlet error", e);
        try {
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error occurred");
        } catch (IOException ioException) {
            logger.error("Error writing error response", ioException);
        }
    }

    /**
     * Validation result wrapper
     */
    public static class ValidationResult<T> {
        private final T object;
        private final List<String> errors;

        public ValidationResult(T object, List<String> errors) {
            this.object = object;
            this.errors = errors;
        }

        public T getObject() {
            return object;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }
    }
}
