package com.example.demo;
import static org.junit.jupiter.api.Assertions.*;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class NoteServiceTest2 {

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    @Transactional
    void setUp(){
        Note existingNote = new Note();
        existingNote.setContent("This is my content");
        noteRepository.save(existingNote);
    }

    @Test
    void deleteNoteByContentFail() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            noteRepository.deleteNoteByContent("This is my content");
        });
    }
}