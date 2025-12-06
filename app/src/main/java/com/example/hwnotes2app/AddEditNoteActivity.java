package com.example.hwnotes2app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity {
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

        TextView titleTextView = findViewById(R.id.titleTextView);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE)) {

            note = (Note) intent.getSerializableExtra(EXTRA_NOTE);
            position = intent.getIntExtra(EXTRA_POSITION, -1);

            if (note != null) {
                titleTextView.setText(R.string.edit_note);
                titleEditText.setText(note.getTitle());
                contentEditText.setText(note.getContent());
            }
        } else {

            note = new Note("", "");
            titleTextView.setText(R.string.new_note);
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

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        note.setTitle(title);
        note.setContent(content);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        resultIntent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}