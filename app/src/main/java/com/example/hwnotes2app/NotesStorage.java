package com.example.hwnotes2app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotesStorage {
    private static final String PREFS_NAME = "notes_prefs";
    private static final String KEY_NOTES = "notes_list";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public NotesStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveNotes(List<Note> notes) {
        String json = gson.toJson(notes);
        sharedPreferences.edit().putString(KEY_NOTES, json).apply();
    }

    public List<Note> loadNotes() {
        String json = sharedPreferences.getString(KEY_NOTES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Note>>(){}.getType();
        List<Note> notes = gson.fromJson(json, type);
        return notes != null ? notes : new ArrayList<>();
    }

    public void clearNotes() {
        sharedPreferences.edit().remove(KEY_NOTES).apply();
    }
}