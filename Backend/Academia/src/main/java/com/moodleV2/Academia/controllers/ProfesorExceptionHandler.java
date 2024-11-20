package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.exceptions.SearchParamException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.mapping.PropertyReferenceException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ControllerAdvice
public class ProfesorExceptionHandler {

    private RepresentationModel<?> links = new RepresentationModel<>()
            .add(linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10), null, null, null, null, null))
                    .withRel("profesori"));
//        links.add(linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10))).withRel("profesori"));

    @ExceptionHandler(ProfesorNotFoundException.class)
    public ResponseEntity<?> handleProfesorNotFoundException(ProfesorNotFoundException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Profesor Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(LengthPaginationException.class)
    public ResponseEntity<?> handleLengthPaginationException(LengthPaginationException ex, HttpServletRequest request) {
        // TODO: add to a pseudo-blacklist the request initiator

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
        body.put("error", "Request Range Not Satisfiable");
        body.put("message", ex.getMessage());
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                        request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).body(body);
    }

    @ExceptionHandler(PropertyReferenceException.class)     // 422 Unprocessable Content? or 400 Bad Request?
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "Unprocessable Entity");
        body.put("message", ex.getMessage());
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                        request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(SearchParamException.class)
    public ResponseEntity<?> handleSearchParamException(SearchParamException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "Unprocessable Entity");
        body.put("message", ex.getMessage());
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<?> handleIndexOutOfBoundsException(IndexOutOfBoundsException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
        body.put("error", "Request Range Not Satisfiable");
        body.put("message", ex.getMessage());
        body.put("path", request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" +
                request.getQueryString());
        body.put("_links", links.getLinks());

        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).body(body);
    }
}
