package com.example.demo.dto;

public class NoteDTO {
    private Long id;
    private String content;

    public NoteDTO(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
