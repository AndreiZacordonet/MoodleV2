package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ControllerAdvice
public class UniversalExceptionHandler {

    // DONE: add link to api documentation
    private static final RepresentationModel<?> links = new RepresentationModel<>()
            .add(linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10), null, null, null, null, null, null, null))
                    .withRel("profesori"))
            .add(linkTo(methodOn(DisciplinaController.class).getAll(PageRequest.of(0, 10), null, null, null, null, null, null, null, null))
                    .withRel("discipline"))
            .add(Link.of("/v3/api-docs").withRel("api-docs").withTitle("API Documentation"));
//        links.add(linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10))).withRel("profesori"));

    private static ResponseEntity<?> bodyBuild(HttpStatus status, String error, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ProfesorNotFoundException.class)
    public ResponseEntity<?> handleProfesorNotFoundException(ProfesorNotFoundException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.NOT_FOUND, "Profesor Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(LengthPaginationException.class)
    public ResponseEntity<?> handleLengthPaginationException(LengthPaginationException ex, HttpServletRequest request) {
        // TODO: add to a pseudo-blacklist the request initiator
        return bodyBuild(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Request Range Not Satisfiable", ex.getMessage(), request);
    }

    @ExceptionHandler(PropertyReferenceException.class)     // 422 Unprocessable Content? or 400 Bad Request?
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(SearchParamException.class)
    public ResponseEntity<?> handleSearchParamException(SearchParamException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<?> handleIndexOutOfBoundsException(IndexOutOfBoundsException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Request Range Not Satisfiable", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<?> handleInvalidFieldException(InvalidFieldException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)    // for the parsing errors from enums (should this be 406 Not Acceptable?)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Request is not in a proper format. Check api documentation", request);
    }

    @ExceptionHandler(ProfesorArchivedException.class)
    public ResponseEntity<?> handleProfesorAlreadyArchivedException(ProfesorArchivedException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), request);
    }

    @ExceptionHandler(DisciplinaNotFoundException.class)
    public ResponseEntity<?> handleDisciplinaNotFoundException(DisciplinaNotFoundException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.NOT_FOUND, "Disciplina not found", ex.getMessage(), request);
    }

    @ExceptionHandler(DisciplinaArchivedException.class)
    public ResponseEntity<?> handleDisciplinaArchivedException(DisciplinaArchivedException ex, HttpServletRequest request) {
        return bodyBuild(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 406);
        body.put("error", "Your target representation doesn't exist.");
        body.put("message", "Try another representation (examples):");

        Map<String, Object> examples = new LinkedHashMap<>();
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("nume", "Zaco");
        content.put("prenume", "Andrei");
        content.put("email", "andreiqwe@gmail.com");
        content.put("gradDidactic", "ASISTENT");
        content.put("tipAsociere", "TITULAR");
        content.put("afiliere", "afiliere maxima");
        examples.put("ProfesorDto", content);

        content.clear();
        content.put("cod", "MATH69");
        content.put("idTitular", 2);
        content.put("numeDisciplina", "Matematica amuzanta");
        content.put("anStudiu", 2);
        content.put("tipDisciplina", "IMPUSA");
        content.put("categorie", "SPECIALITATE");
        content.put("tipExaminare", "COLOCVIU");
        examples.put("DisciplinaDto", content);

        content.clear();
        content.put("nume", "MIrcea");
        content.put("prenume", "CelBatran");
        content.put("email", "mircea@elbatran.io");
        content.put("cicluStudii", "MASTER");
        content.put("anStudiu", 2);
        content.put("grupa", 12);
        examples.put("StudentDto", content);

        body.put("examples", examples);
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(body);
    }
}
