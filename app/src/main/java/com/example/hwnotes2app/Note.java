package com.example.hwnotes2app;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private Date createdDate;
    private Date updatedDate;

    public Note() {
    }

    public Note(String title, String content) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedDate = new Date();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedDate = new Date();
    }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}