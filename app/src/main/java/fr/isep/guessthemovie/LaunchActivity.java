package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class LaunchActivity extends AppCompatActivity {

    Spinner levelDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        levelDifficulty = findViewById(R.id.levelDifficulty);
        String[] dropdownList = {"Beginner", "Intermediate", "Expert"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dropdownList);
        levelDifficulty.setAdapter(adapter);

        //String levelChosen = levelDifficulty.getSelectedItem().toString();
        //if levelChosen == "Beginner" --> rediriger vers LevelOneActivity
        //if levelChosen == "Intermediate" --> rediriger vers LevelTwoActivity
        //if levelChosen == "Expert" --> rediriger vers LevelThreeActivity
    }
}