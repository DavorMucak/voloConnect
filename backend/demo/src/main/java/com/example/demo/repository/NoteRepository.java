package com.example.demo.repository;

import com.example.demo.model.Note;
import com.example.demo.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

//repository sučelja nude korištenje klasičnih zahtjeva nad bazom

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(MyUser user); // Dohvati sve bilješke za određenog korisnika
    void deleteNoteByContent(String content);
}

