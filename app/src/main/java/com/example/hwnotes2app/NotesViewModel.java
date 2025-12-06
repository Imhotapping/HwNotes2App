package com.example.hwnotes2app;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class NotesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Note>> notesLiveData;
    private NotesStorage notesStorage;

    public NotesViewModel(Application application) {
        super(application);
        notesLiveData = new MutableLiveData<>();
        notesStorage = new NotesStorage(application);
        loadNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return notesLiveData;
    }

    private void loadNotes() {
        List<Note> notes = notesStorage.loadNotes();
        notesLiveData.setValue(notes);
    }

    public void addNote(Note note) {
        List<Note> currentNotes = notesLiveData.getValue();
        if (currentNotes == null) {
            currentNotes = new ArrayList<>();
        }
        currentNotes.add(note);
        notesLiveData.setValue(currentNotes);
        notesStorage.saveNotes(currentNotes);
    }

    public void updateNote(int position, Note note) {
        List<Note> currentNotes = notesLiveData.getValue();
        if (currentNotes != null && position >= 0 && position < currentNotes.size()) {
            currentNotes.set(position, note);
            notesLiveData.setValue(currentNotes);
            notesStorage.saveNotes(currentNotes);
        }
    }

    public void deleteNote(int position) {
        List<Note> currentNotes = notesLiveData.getValue();
        if (currentNotes != null && position >= 0 && position < currentNotes.size()) {
            currentNotes.remove(position);
            notesLiveData.setValue(currentNotes);
            notesStorage.saveNotes(currentNotes);
        }
    }
}