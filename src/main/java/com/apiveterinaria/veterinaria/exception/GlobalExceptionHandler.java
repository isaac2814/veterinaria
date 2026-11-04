package com.apiveterinaria.veterinaria.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Uno o más campos contienen errores de validación.");
        problem.setTitle("Error de validación");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        Map<String, String> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String detail = "El cuerpo de la solicitud no es legible o tiene un formato inválido.";
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ifx && ifx.getTargetType() != null && ifx.getTargetType().isEnum()) {
            detail = "Valor inválido para campo de enumeración. Valores permitidos: " + Arrays.toString(ifx.getTargetType().getEnumConstants());
        } else {
            String msg = ex.getMessage();
            if (msg != null && (msg.contains("values accepted for Enum class") || msg.contains("not one of the values accepted"))) {
                int start = msg.indexOf('[');
                int end = msg.indexOf(']');
                if (start != -1 && end != -1 && end > start) {
                    detail = "Valor inválido para campo de enumeración. Valores permitidos: " + msg.substring(start, end + 1);
                } else {
                    detail = "Valor inválido para campo de enumeración. Verifique los valores permitidos.";
                }
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Solicitud no legible");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String detail = "El parámetro '" + ex.getName() + "' tiene un formato o valor inválido.";
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null && requiredType.isEnum()) {
            detail = "Valor inválido para el parámetro '" + ex.getName() + "'. Valores permitidos: " + Arrays.toString(requiredType.getEnumConstants());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Parámetro inválido");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleRecursoNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ProblemDetail> handleReglaNegocio(ReglaNegocioException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Error de regla de negocio");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problem.setTitle("Error en la solicitud");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode()).body(problem);
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ProblemDetail> handleDirectPSQLException(PSQLException ex, HttpServletRequest request) {
        return construirRespuestaPSQLException(ex, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        PSQLException psqlException = buscarPSQLException(ex);
        if (psqlException != null) {
            return construirRespuestaPSQLException(psqlException, request);
        }
        log.error("Violación de integridad de datos en {}", request.getRequestURI(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Conflicto de integridad de datos en el sistema.");
        problem.setTitle("Conflicto de datos");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, HttpServletRequest request) {
        PSQLException psqlException = buscarPSQLException(ex);
        if (psqlException != null) {
            return construirRespuestaPSQLException(psqlException, request);
        }

        log.error("Error no controlado al procesar solicitud en {}", request.getRequestURI(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor. Por favor comuníquese con el administrador."
        );
        problem.setTitle("Error interno del servidor");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ResponseEntity<ProblemDetail> construirRespuestaPSQLException(PSQLException ex, HttpServletRequest request) {
        String sqlState = ex.getSQLState();
        ServerErrorMessage serverError = ex.getServerErrorMessage();
        String detalleMensaje = serverError != null ? serverError.getMessage() : ex.getMessage();

        if (detalleMensaje != null && detalleMensaje.startsWith("ERROR: ")) {
            detalleMensaje = detalleMensaje.substring(7);
            int newlineIndex = detalleMensaje.indexOf('\n');
            if (newlineIndex != -1) {
                detalleMensaje = detalleMensaje.substring(0, newlineIndex).trim();
            }
        }

        HttpStatus status;
        String titulo;
        String detalleFinal;

        if ("P0001".equals(sqlState)) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            titulo = "Regla de negocio violada";
            detalleFinal = detalleMensaje;
        } else if ("23505".equals(sqlState)) {
            status = HttpStatus.CONFLICT;
            titulo = "Registro duplicado";
            detalleFinal = "Ya existe un registro con los mismos datos únicos en el sistema.";
        } else if ("23503".equals(sqlState)) {
            status = HttpStatus.CONFLICT;
            titulo = "Conflicto de integridad referencial";
            detalleFinal = "La operación no se puede completar porque el registro está relacionado con otros datos o hace referencia a una entidad inexistente.";
        } else if ("23514".equals(sqlState)) {
            status = HttpStatus.BAD_REQUEST;
            titulo = "Restricción de validación violada";
            detalleFinal = "Los datos proporcionados no cumplen con las reglas y restricciones de la base de datos.";
        } else if ("23502".equals(sqlState)) {
            status = HttpStatus.BAD_REQUEST;
            titulo = "Campo obligatorio faltante";
            detalleFinal = "Uno o más campos obligatorios no fueron proporcionados.";
        } else {
            log.error("Error de base de datos con SQLState {} en {}", sqlState, request.getRequestURI(), ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            titulo = "Error de base de datos";
            detalleFinal = "Ocurrió un error al procesar la operación en la base de datos.";
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalleFinal);
        problem.setTitle(titulo);
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(status).body(problem);
    }

    private PSQLException buscarPSQLException(Throwable throwable) {
        Throwable actual = throwable;
        while (actual != null) {
            if (actual instanceof PSQLException psqlEx) {
                return psqlEx;
            }
            actual = actual.getCause();
        }
        return null;
    }
}
