package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class LaunchActivity extends AppCompatActivity {

    Spinner levelDifficulty;
    ImageView imageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        imageView = findViewById(R.id.imageView);
        levelDifficulty = findViewById(R.id.levelDifficulty);
        String[] dropdownList = {"Beginner", "Intermediate", "Expert"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dropdownList);
        levelDifficulty.setAdapter(adapter);
    }

    public void onButtonClick(View view) {
        Intent intent;
        if (levelDifficulty.getSelectedItem().toString() == "Beginner") {
            intent = new Intent(this, BeginnerActivity.class);
        }
        else if (levelDifficulty.getSelectedItem().toString() == "Intermediate") {
            intent = new Intent(this, IntermediateActivity.class);
        }
        else {
            intent = new Intent(this, ExpertActivity.class);
        }
        startActivity(intent);
    }
}