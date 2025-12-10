package com.example.hwnotes2app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEditNoteActivity extends AppCompatActivity {

    private NotificationHelper notificationHelper;
    public static final String EXTRA_NOTE = "note";
    public static final String EXTRA_POSITION = "position";

    private EditText titleEditText;
    private EditText contentEditText;
    private Note note;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        notificationHelper = new NotificationHelper(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView titleTextView = findViewById(R.id.titleTextView);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_NOTE)) {

                note = intent.getParcelableExtra(EXTRA_NOTE);
                position = intent.getIntExtra(EXTRA_POSITION, -1);

                if (note != null) {
                    titleTextView.setText(R.string.edit_note);
                    titleEditText.setText(note.getTitle());
                    contentEditText.setText(note.getContent());
                }
            } else {
                titleTextView.setText(R.string.new_note);
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        Intent data = new Intent();
        String currentDate = getCurrentDate();

        if (note != null) {
            note.setTitle(title);
            note.setContent(content);
            note.setUpdatedDate(currentDate);
            data.putExtra(EXTRA_NOTE, note);
            data.putExtra(EXTRA_POSITION, position);

            if (notificationHelper.canShowNotification()) {
                notificationHelper.showNotification(
                        "Заметка обновлена",
                        "Заметка \"" + title + "\" была обновлена"
                );
            }
        } else {
            String id = UUID.randomUUID().toString();
            Note newNote = new Note(id, title, content, currentDate);
            data.putExtra(EXTRA_NOTE, newNote);

            if (notificationHelper.canShowNotification()) {
                notificationHelper.showNotification(
                        "Новая заметка создана",
                        "Заметка \"" + title + "\" была добавлена"
                );
            }


        }

        setResult(RESULT_OK, data);
        finish();
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}