package com.example.pl_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {

    private TextView resultScore;
    private Button playAgain, exit, shareScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultScore = findViewById(R.id.resultScore);
        playAgain = findViewById(R.id.playAgain);
        exit = findViewById(R.id.exit);
        shareScore = findViewById(R.id.shareScore);

        int score = getIntent().getIntExtra("score", 0);
        resultScore.setText("Your Score: " + score);

        shareScore.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "I scored " + score + " in the Math Quiz!");
            startActivity(Intent.createChooser(shareIntent, "Share your score"));
        });

        playAgain.setOnClickListener(v -> {
            Intent intent = new Intent(Result.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Button viewScoresButton = findViewById(R.id.viewScoresButton);
        viewScoresButton.setOnClickListener(v -> {
            Intent intent = new Intent(Result.this, ScoresActivity.class);
            startActivity(intent);
        });
        exit.setOnClickListener(v -> finish());
    }
}
