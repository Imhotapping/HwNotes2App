package com.example.hwnotes2app;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NotesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSafeArea();

        initViews();
        initViewModel();
        initRecyclerView();
    }

    private void setupSafeArea() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        // Применяем отступы для заголовка
        View appTitle = findViewById(R.id.appTitle);
        if (appTitle != null) {
            ViewCompat.setOnApplyWindowInsetsListener(appTitle, (v, insets) -> {

                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;

                v.setPadding(v.getPaddingLeft(), statusBarHeight,
                        v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }
    }
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        viewModel.getNotes().observe(this, notes -> {
            adapter.setNotes(notes);
            updateEmptyView(notes);
        });
    }

    private void initRecyclerView() {
        adapter = new NotesAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new NotesItemDecoration(8));

        adapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                editNote(position);
            }

            @Override
            public void onItemLongClick(int position, View view) {
                showPopupMenu(position, view);
            }
        });
    }

    private void updateEmptyView(java.util.List<Note> notes) {
        View emptyView = findViewById(R.id.emptyView);
        if (notes == null || notes.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showPopupMenu(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(1, 1, 1, "Редактировать");
        popupMenu.getMenu().add(1, 2, 2, "Удалить");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == 1) {
                    editNote(position);
                    return true;
                } else if (itemId == 2) {
                    deleteNote(position);
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void editNote(int position) {
        Note note = adapter.getNote(position);
        if (note != null) {
            Intent intent = new Intent(this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_NOTE, note);
            intent.putExtra(AddEditNoteActivity.EXTRA_POSITION, position);
            startActivityForResult(intent, EDIT_NOTE_REQUEST);
        }
    }

    private void deleteNote(int position) {
        viewModel.deleteNote(position);
        Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Note note = (Note) data.getSerializableExtra(AddEditNoteActivity.EXTRA_NOTE);
            if (note == null) {
                Toast.makeText(this, "Ошибка: данные заметки не получены", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestCode == ADD_NOTE_REQUEST) {
                viewModel.addNote(note);
                Toast.makeText(this, "Заметка добавлена", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                int position = data.getIntExtra(AddEditNoteActivity.EXTRA_POSITION, -1);
                if (position != -1) {
                    viewModel.updateNote(position, note);
                    Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}