package com.moodleV2.Academia.config;

import com.moodleV2.Academia.models.*;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import com.moodleV2.Academia.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ProfesorRepository profesorRepository, StudentRepository studentRepository) {
        return args -> {
            log.info("Preloading " + profesorRepository.save(new Profesor("Zaco", "Andrei", "andrei74c0@gmail.com", Grad.PROFESOR, Asociere.TITULAR, "afiliat suta la suta")));
            log.info("Preloading " + profesorRepository.save(new Profesor("Misu", "Geamantan", "misussusus@gmail.com", Grad.CONFERENTIAR, Asociere.EXTERN, "afiliat 20 la suta")));

            log.info("Preloading " + studentRepository.save(new Student("Picunigu", "Toader", "niguteo516@yahoo.com", Ciclu.LICENTA, 3, 10)));
        };
    }
}
