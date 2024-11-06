package com.example.pl_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    TextView score, lives, time, question;
    EditText answer;
    Button ok, buttonNext;
    int correctAnswer = 0, userScore = 0, userLives = 3;
    String operation;
    CountDownTimer timer;
    private final long timeInMillis = 30000; // 30 seconds
    long timeLeft = timeInMillis;
    boolean soundEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        createNotificationChannel(); // Create notification channel

        // Initialize UI elements
        score = findViewById(R.id.score);
        lives = findViewById(R.id.lives);
        time = findViewById(R.id.time);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        ok = findViewById(R.id.ok);
        buttonNext = findViewById(R.id.buttonNext);

        // Retrieve extras from Intent
        operation = getIntent().getStringExtra("operation");
        soundEnabled = getIntent().getBooleanExtra("soundEnabled", false);

        // Generate the first question
        generateQuestion();

        // Handle answer checking
        ok.setOnClickListener(v -> checkAnswer());
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Game Channel";
            String description = "Channel for game notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(50) + 1;
        int num2 = random.nextInt(50) + 1;

        switch (operation) {
            case "addition":
                correctAnswer = num1 + num2;
                question.setText(String.format(Locale.getDefault(), "%d + %d", num1, num2));
                break;
            case "subtraction":
                correctAnswer = num1 - num2;
                question.setText(String.format(Locale.getDefault(), "%d - %d", num1, num2));
                break;
            case "multiplication":
                correctAnswer = num1 * num2;
                question.setText(String.format(Locale.getDefault(), "%d × %d", num1, num2));
                break;
            case "division":
                while (num2 == 0 || num1 % num2 != 0) {
                    num1 = random.nextInt(50) + 1;
                    num2 = random.nextInt(50) + 1;
                }
                correctAnswer = num1 / num2;
                question.setText(String.format(Locale.getDefault(), "%d ÷ %d", num1, num2));
                break;
        }

        startTimer(); // Start the timer when a new question is generated
    }

    private void startTimer() {
        timeLeft = timeInMillis; // Reset time left
        time.setText(String.valueOf(timeLeft / 1000)); // Update UI to show time in seconds

        // Cancel any existing timer to avoid multiple timers running simultaneously
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(timeLeft, 1000) { // Count down every second
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                time.setText(String.valueOf(timeLeft / 1000)); // Update UI with the remaining time
            }

            public void onFinish() {
                // Handle what happens when the time runs out
                Toast.makeText(GameActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                userLives -= 1; // Deduct a life
                lives.setText(String.valueOf(userLives));
                if (userLives == 0) {
                    endGame();
                } else {
                    generateQuestion(); // Generate a new question after time runs out
                }
            }
        }.start(); // Start the timer
    }

    private void checkAnswer() {
        String userAnswerString = answer.getText().toString();
        if (userAnswerString.isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer = Integer.parseInt(userAnswerString);
        if (userAnswer == correctAnswer) {
            userScore += 10;
            score.setText(String.valueOf(userScore));
            generateQuestion(); // Move to the next question
            answer.setText("");
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            userLives -= 1;
            lives.setText(String.valueOf(userLives));
            if (userLives == 0) {
                endGame();
            } else {
                Toast.makeText(this, "Wrong answer. Try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void endGame() {
        if (userLives == 0) {
            DatabaseHelper db = new DatabaseHelper(this);
            db.addScore(userScore);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Game Over")
                    .setContentText("You lost all your lives. Your score: " + userScore)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());

            Intent intent = new Intent(GameActivity.this, Result.class);
            intent.putExtra("score", userScore);
            startActivity(intent);
            finish();
        }
    }
}
