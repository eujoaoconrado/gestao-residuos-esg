package br.com.fiap.gestaoresiduos.exception;

import br.com.fiap.gestaoresiduos.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalhes = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(f ->
                detalhes.add(f.getField() + ": " + f.getDefaultMessage())
        );
        ErrorResponse body = new ErrorResponse(
                java.time.LocalDateTime.now(),
                400,
                "Bad Request",
                "Dados invalidos na requisicao",
                req.getRequestURI(),
                detalhes
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(404, "Not Found", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(
                409,
                "Conflict",
                "Violacao de integridade de dados. Verifique se ja existe registro com os mesmos valores unicos.",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(403, "Forbidden", "Voce nao tem permissao para acessar este recurso", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(401, "Unauthorized", "Email ou senha invalidos", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(400, "Bad Request", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado no servidor", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
