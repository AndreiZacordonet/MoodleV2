package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.jwt_security.GrpcClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/academia/links")
public class AcademiaDefinitionOpenapi {

    private final GrpcClientService grpcClientService;

    private final Map<String, Map<String, String>> roleAccessMap = new HashMap<>() {{
        put("/profesori", new HashMap<>() {{
            put("GET", "ADMIN; PROFESSOR; STUDENT");    // get all professors
            put("POST", "ADMIN");                       // create a new professor
        }});
        put("/profesori/{id}", new HashMap<>() {{
            put("GET", "ADMIN; PROFESSOR; STUDENT");    // get professor by id
            put("DELETE", "ADMIN");                     // archive a professor
            put("PATCH", "ADMIN");                      // update a professor
        }});
        put("/profesori/{id}/activate", new HashMap<>() {{
            put("POST", "ADMIN");                       // un-archive a professor
        }});
        put("/profesori/archive", new HashMap<>() {{
            put("GET", "ADMIN");                        // get all archived professors
        }});
        put("/profesori/{id}/my-disciplines", new HashMap<>() {{
            put("GET", "PROFESSOR");                    // get professor's courses
        }});
        put("/profesori/{id}/all-disciplines", new HashMap<>() {{
            put("GET", "PROFESSOR");                    // get all courses from professor's perspective
        }});
        put("/profesori/{id}/studenti", new HashMap<>() {{
            put("GET", "PROFESSOR");                    // get professor's students
        }});


        put("/studenti", new HashMap<>() {{
            put("GET", "ADMIN; PROFESSOR; STUDENT");       // get all students
            put("POST", "ADMIN");                          // create new student
        }});
        put("/studenti/{id}", new HashMap<>() {{
            put("GET", "ADMIN; PROFESSOR; STUDENT");       // get student by id
            put("DELETE", "ADMIN");                        // archive student
            put("PATCH", "ADMIN");                         // update student
        }});
        put("/studenti/{id}/activate", new HashMap<>() {{
            put("POST", "ADMIN");                          // un-archive student
        }});
        put("/studenti/archive", new HashMap<>() {{
            put("GET", "ADMIN");                           // get archived students
        }});
        put("/studenti/{id}/disciplines", new HashMap<>() {{
            put("GET", "STUDENT");                         // get student's courses
        }});


        put("/discipline", new HashMap<>() {{
            // TODO: add admin and student here?
            put("GET", "PROFESSOR");                // get all courses
            put("PUT", "PROFESSOR");                // create new course
        }});
        put("/discipline/{code}", new HashMap<>() {{
            put("GET", "PROFESSOR; STUDENT");        // get a course
            put("DELETE", "ADMIN");                  // archive a course
            put("PATCH", "PROFESSOR");               // update a course
        }});
        put("/discipline/{code}/activate", new HashMap<>() {{
            put("POST", "ADMIN");                   // un-archive a course
        }});
        put("discipline/archive/", new HashMap<>() {{
            put("GET", "ADMIN");                    // get all from archive
        }});
    }};

    @GetMapping
    public List<Map<String, String>> getAllowedLinks(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        String role;

        try {
            role = grpcClientService.validateTokenAndGetRole(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token: " + e.getMessage());
        }

        // Filter the roleAccessMap based on the user's role
        List<Map<String, String>> allowedLinks = roleAccessMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .filter(methodEntry -> Arrays.asList(methodEntry.getValue().split("; ")).contains(role))
                        .map(methodEntry -> {
                            Map<String, String> linkDetails = new HashMap<>();
                            linkDetails.put("uri", entry.getKey());
                            linkDetails.put("method", methodEntry.getKey());
                            return linkDetails;
                        })
                )
                .collect(Collectors.toList());

        return allowedLinks;
    }

}
