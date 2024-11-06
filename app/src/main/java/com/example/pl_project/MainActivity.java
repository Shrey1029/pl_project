package com.example.pl_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "default"; // Define the CHANNEL_ID
    private RadioGroup operationsGroup;
    private Spinner difficultySpinner;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operationsGroup = findViewById(R.id.radioGroupOperations);
        difficultySpinner = findViewById(R.id.spinnerDifficulty);
        startButton = findViewById(R.id.startButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        // Create notification channel
        createNotificationChannel();

        startButton.setOnClickListener(v -> {
            // Get selected operation based on checked radio button
            String operation = getSelectedOperation();
            if (operation == null) {
                // Handle case when no operation is selected
                return; // Optionally show a message to the user
            }

            String difficulty = difficultySpinner.getSelectedItem().toString();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("operation", operation);
            intent.putExtra("difficulty", difficulty);
            startActivity(intent);
        });
    }

    private String getSelectedOperation() {
        int selectedOperation = operationsGroup.getCheckedRadioButtonId();
        if (selectedOperation == -1) {
            return null; // No radio button selected
        }

        // Find the selected RadioButton by ID
        RadioButton selectedRadioButton = findViewById(selectedOperation);
        return selectedRadioButton.getText().toString().toLowerCase(); // Return the operation as a string
    }

    private void createNotificationChannel() {
        // Check if the API level is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Channel for default notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
