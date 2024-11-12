package com.moodleV2.Academia.config;

import com.moodleV2.Academia.models.*;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
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
    CommandLineRunner initDatabase(
            ProfesorRepository profesorRepository,
            StudentRepository studentRepository,
            DisciplinaRepository disciplinaRepository) {

        return args -> {

            Profesor ciorbea =  new Profesor("Zaco", "Andrei", "andrei74c0@gmail.com", Grad.PROFESOR, Asociere.TITULAR, "afiliat suta la suta");
            Profesor vasilache = new Profesor("Misu", "Geamantan", "misussusus@gmail.com", Grad.CONFERENTIAR, Asociere.EXTERN, "afiliat 20 la suta");
            // teachers loading
            log.info("Preloading " + profesorRepository.save(ciorbea));
            log.info("Preloading " + profesorRepository.save(vasilache));

            // students loading
            log.info("Preloading " + studentRepository.save(new Student("Picunigu", "Toader", "niguteo516@yahoo.com", Ciclu.LICENTA, 3, 10)));
            log.info("Preloading " + studentRepository.save(new Student("Tudorel", "Toader", "suntunmicmic@yahoo.com", Ciclu.LICENTA, 1, 11)));
            log.info("Preloading " + studentRepository.save(new Student("Zaharia", "Stefan", "alfaqteo615@yahoo.com", Ciclu.MASTER, 1, 7)));

            // courses loading
            log.info("Preloading " + disciplinaRepository.save(new Disciplina("1234", ciorbea, "ADP", 4, TipDisciplina.IMPUSA, Categorie.ADIACENTA, TipExaminare.COLOCVIU)));
            log.info("Preloading " + disciplinaRepository.save(new Disciplina("77777", ciorbea, "IA", 1, TipDisciplina.LIBER_ALEASA, Categorie.SPECIALITATE, TipExaminare.EXAMEN)));
        };
    }
}
