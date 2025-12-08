package com.example.hwnotes2app;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String id;
    private String title;
    private String content;
    private String createdDate;
    private String updatedDate;

    // Конструктор для новой заметки
    public Note(String id, String title, String content, String createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = createdDate; // При создании обе даты одинаковы
    }

    // Пустой конструктор (для Gson или других случаев)
    public Note() {
        // Пустой конструктор
    }


    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }

    protected Note(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        createdDate = in.readString();
        updatedDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(createdDate);
        dest.writeString(updatedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}