package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.model.MyUser;
import com.example.demo.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesByUser(MyUser user) {
        return noteRepository.findByUser(user);
    }

    public Note createNote(MyUser user, String content) {
        Note note = new Note();
        note.setUser(user);
        note.setContent(content);
        return noteRepository.save(note);
    }

    public Note updateNote(Long noteId, String content) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Bilješka nije pronađena."));
        note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNoteById(Long noteId) {
        noteRepository.deleteById(noteId);
    }
}

