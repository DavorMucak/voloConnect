package com.example.demo.Controller;

import com.example.demo.dto.NoteDTO;
import com.example.demo.model.MyUser;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NoteService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/biljeske")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public NoteController(NoteService noteService, UserRepository userRepository, NoteRepository noteRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }


    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @PathVariable Long id,
            @RequestBody String content) {
        System.out.println("Received content: " + content);
        Note note = noteService.updateNote(id, content);

        return ResponseEntity.ok(note);
    }


    @GetMapping
    public ResponseEntity<List<NoteDTO>> getNotes(@RequestParam String username) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NoteDTO> notes = user.getNotes().stream()
                .map(note -> new NoteDTO(note.getId(), note.getContent()))  // Vraćanje id i content
                .collect(Collectors.toList());

        return ResponseEntity.ok(notes);
    }

    @PostMapping("/{username}")
    public ResponseEntity<NoteDTO> createNote(@PathVariable String username, @RequestBody String content) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setContent(content);
        note.setUser(user);

        Note savedNote = noteRepository.save(note);

        return ResponseEntity.ok(new NoteDTO(savedNote.getId(), savedNote.getContent()));
    }

    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String username, @PathVariable Long id) {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        noteRepository.delete(note);
        return ResponseEntity.ok().build();
    }
}



