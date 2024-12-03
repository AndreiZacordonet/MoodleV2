package com.moodleV2.Academia.service;

import com.moodleV2.Academia.models.Student;
import com.moodleV2.Academia.repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> studentSearch(Pageable pageable) {

        return studentRepository.findAll(pageable);
    }
}
