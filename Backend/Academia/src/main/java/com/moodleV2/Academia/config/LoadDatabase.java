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

            // teachers loading
            Profesor ciorbea =  new Profesor("Zaco", "Andrei", "andrei74c0@gmail.com", Grad.PROFESOR, Asociere.TITULAR, "afiliat suta la suta");
            Profesor vasilache = new Profesor("Misu", "Andrei", "misussusus@gmail.com", Grad.CONFERENTIAR, Asociere.EXTERN, "afiliat 20 la suta");
            Profesor misu = new Profesor("Vasile", "Geamantan", "Vasile@gmail.com", Grad.SEF_LUCRARI, Asociere.EXTERN, "afiliat 10 la suta");
            Profesor barbotea = new Profesor("Barbotea", "Geamantan", "barbotea@gmail.com", Grad.SEF_LUCRARI, Asociere.ASOCIAT, "afiliat 1 la suta");
            Profesor guzgan = new Profesor("Guzgan", "Geamantan", "guzgan@gmail.com", Grad.SEF_LUCRARI, Asociere.ASOCIAT, "afiliat 1 la suta");
            log.info("Preloading " + profesorRepository.save(ciorbea));
            log.info("Preloading " + profesorRepository.save(vasilache));
            log.info("Preloading " + profesorRepository.save(misu));
            log.info("Preloading " + profesorRepository.save(barbotea));
            log.info("Preloading " + profesorRepository.save(guzgan));

            // courses loading
            Disciplina apd = new Disciplina("1234", ciorbea, "ADP", 4, TipDisciplina.IMPUSA, Categorie.ADIACENTA, TipExaminare.COLOCVIU);
            Disciplina ia = new Disciplina("77777", ciorbea, "IA", 1, TipDisciplina.LIBER_ALEASA, Categorie.SPECIALITATE, TipExaminare.EXAMEN);
            log.info("Preloading " + disciplinaRepository.save(apd));
            log.info("Preloading " + disciplinaRepository.save(ia));

            // students loading
            Student picu = new Student("Picunigu", "Toader", "niguteo516@yahoo.com", Ciclu.LICENTA, 3, 10);
            picu.getClasses().add(apd);
            picu.getClasses().add(ia);
            Student tudorel = new Student("Tudorel", "Toader", "suntunmicmic@yahoo.com", Ciclu.LICENTA, 1, 11);
            Student zh = new Student("Zaharia", "Stefan", "alfaqteo615@yahoo.com", Ciclu.MASTER, 1, 7);
            log.info("Preloading " + studentRepository.save(picu));
            log.info("Preloading " + studentRepository.save(tudorel));
            log.info("Preloading " + studentRepository.save(zh));
            zh.getClasses().add(ia);
            log.info("Add disciplines: " + studentRepository.save(zh));
        };
    }
}
