package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTxt;
    TextView nbQuestionsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreTxt = findViewById(R.id.score);
        nbQuestionsTxt = findViewById(R.id.nbQuestions);

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            int score = extras.getInt("score");
            scoreTxt.setText(String.valueOf(score));
            int nbQuestions = extras.getInt("nbQuestion");
            nbQuestionsTxt.setText(String.valueOf(nbQuestions));
        }
    }
}