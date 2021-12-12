package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTxt;
    TextView nbQuestionsTxt;
    TextView message ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreTxt = findViewById(R.id.score);
        nbQuestionsTxt = findViewById(R.id.nbQuestions);
        message = findViewById(R.id.message);


        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            int score = extras.getInt("score");
            scoreTxt.setText(String.valueOf(score));
            int nbQuestions = extras.getInt("nbQuestion");
            nbQuestionsTxt.setText(String.valueOf(nbQuestions));
            if (score > 5){
                message.setText("Well done you are a cinema pro;) ");
            }
            else {
                message.setText("You need to watch more movies ! ");
            }
        }
    }

    public void onClickBackToMenu(View view) {
        Intent intent;
        intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);


    }
}