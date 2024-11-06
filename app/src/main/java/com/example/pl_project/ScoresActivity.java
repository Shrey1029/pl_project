package com.example.pl_project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    private ListView scoresListView;
    private ArrayAdapter<Integer> adapter;
    private List<Integer> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        scoresListView = findViewById(R.id.scoresListView);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Retrieve scores
        scores = dbHelper.getAllScores();

        // Create an adapter and set it to the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scores);
        scoresListView.setAdapter(adapter);
    }
}
