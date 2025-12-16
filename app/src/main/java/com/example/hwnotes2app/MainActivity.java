package com.example.hwnotes2app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.WindowInsets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NotesViewModel viewModel;

    private FloatingActionButton fab;
    private Animation fabPulseAnimation;
    private boolean isFabBounced = false;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();}

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSafeArea();

        initViews();
        initViewModel();
        initRecyclerView();
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.POST_NOTIFICATIONS)) {

                Toast.makeText(this,
                        "Разрешите уведомления, чтобы получать напоминания о заметках",
                        Toast.LENGTH_LONG).show();
            }

            // Запрашиваем разрешение
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Разрешение на уведомления предоставлено",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Без разрешения уведомления не будут работать",
                        Toast.LENGTH_SHORT).show();
            }
        }
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

        fab = findViewById(R.id.fab);
        fabPulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_pulse);

        // Анимация появления FAB с задержкой
        new Handler().postDelayed(() -> {
            showFabWithBounce();
        }, 300);

        fab.setOnClickListener(v -> {
            animateFabPulse();
            // Добавляем небольшую задержку
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
                // Анимация
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            }, 200);
        });
    }

    private void showFabWithBounce() {
        // Используем ObjectAnimator
        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "translationY", 200f, 0f);
        animator.setDuration(800);
        animator.setInterpolator(new OvershootInterpolator(1.0f));
        animator.start();

        fab.setScaleX(0.5f);
        fab.setScaleY(0.5f);
        fab.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    private void animateFabPulse() {
        fab.startAnimation(fabPulseAnimation);

        // Дополнительная анимация
        fab.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    fab.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .setInterpolator(new BounceInterpolator())
                            .start();
                })
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Анимация при возврате на главный экран
        if (fab != null && fab.getVisibility() == View.VISIBLE) {
            fab.animate()
                    .translationY(0)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isFabBounced) {
            // Анимация "подпрыгивания" при первом получении фокуса
            fab.animate()
                    .translationY(-20)
                    .setDuration(200)
                    .setStartDelay(500)
                    .withEndAction(() -> {
                        fab.animate()
                                .translationY(0)
                                .setDuration(200)
                                .setInterpolator(new BounceInterpolator())
                                .start();
                    })
                    .start();
            isFabBounced = true;
        }
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
        popupMenu.getMenu().add(1, 1, 1, R.string.popupmenu_edit);
        popupMenu.getMenu().add(1, 2, 2, R.string.popupmenu_delete);

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
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_NOTE, note);
            intent.putExtra(AddEditNoteActivity.EXTRA_POSITION, position);
            startActivityForResult(intent, EDIT_NOTE_REQUEST);
        }
    }

    private void deleteNote(int position) {
        viewModel.deleteNote(position);
        Toast.makeText(this, R.string.toast_note_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Note note = data.getParcelableExtra(AddEditNoteActivity.EXTRA_NOTE);
            if (note == null) {
                Toast.makeText(this, R.string.toast_error_data_not_received, Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestCode == ADD_NOTE_REQUEST) {
                viewModel.addNote(note);
                Toast.makeText(this, R.string.toast_note_added, Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                int position = data.getIntExtra(AddEditNoteActivity.EXTRA_POSITION, -1);
                if (position != -1) {
                    viewModel.updateNote(position, note);
                    Toast.makeText(this, R.string.toast_note_updated, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}