package com.example.demo;
import static org.junit.jupiter.api.Assertions.*;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.NoteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
@Transactional
public class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void updateNoteThatIsNotFound() {
        Note existingNote = new Note();
        existingNote.setContent("Old content");
        existingNote = noteRepository.save(existingNote);


        Long nonExistentNoteId = existingNote.getId() + 1;
        String newContent = "Updated content";

        assertThrows(IllegalArgumentException.class, () -> {
            noteService.updateNote(nonExistentNoteId, newContent);
        });
    }
}