package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitPackage.MovieClass;
import retrofitPackage.MovieInterface;

public class ExpertActivity extends AppCompatActivity {

    TextView movieGenreTxt;
    TextView scoreTxt;
    TextView nbQuestionsTxt;
    TextView actor1Txt;
    TextView actor2Txt;
    TextView movieTaglineTxt;
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    Button answer5Button;
    Button answer6Button;
    Button nextQuestionButton;
    Button correctButtonAnswer;
    static int score;
    static int nbQuestions;

    ArrayList<Button> allButtons = new ArrayList<Button>();
    String movieTitleAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);

        movieGenreTxt = findViewById(R.id.movieGenreExpertLevel);
        movieTaglineTxt = findViewById(R.id.taglineExpertLevel);
        nbQuestionsTxt = findViewById(R.id.nbQuestionsExpertLevel);
        scoreTxt = findViewById(R.id.scoreExpertLevel);
        actor1Txt = findViewById(R.id.actorName1ExpertLevel);
        actor2Txt = findViewById(R.id.actorName2ExpertLevel);
        answer1Button = findViewById(R.id.Answer1buttonExpertLevel);
        answer2Button = findViewById(R.id.Answer2buttonExpertLevel);
        answer3Button = findViewById(R.id.Answer3buttonExpertLevel);
        answer4Button = findViewById(R.id.Answer4buttonExpertLevel);
        answer5Button = findViewById(R.id.Answer5buttonExpertLevel);
        answer6Button = findViewById(R.id.Answer6buttonExpertLevel);
        nextQuestionButton = findViewById(R.id.nextQuestionButtonExpertLevel);
        allButtons.add(answer1Button);
        allButtons.add(answer2Button);
        allButtons.add(answer3Button);
        allButtons.add(answer4Button);
        allButtons.add(answer5Button);
        allButtons.add(answer6Button);


        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            int score = extras.getInt("score");
            scoreTxt.setText(String.valueOf(score));
        }
        else {
            score = 0;
            nbQuestions = 0;
        }

        nbQuestions += 1;
        nbQuestionsTxt.setText(String.valueOf(nbQuestions));

        if (nbQuestions == 10) {
            nextQuestionButton.setText("Finish");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(400, 0, 0, 0);
            nextQuestionButton.setLayoutParams(params);
        }

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);

        // define where the correct answer will be
        int randomButton = getRandom();
        correctButtonAnswer = allButtons.get(randomButton);
        getPopularMoviesLevelThree(movieInterface);

    }

    private int getRandom (){
        Random r = new Random();
        int low = 0; //included
        int high = 6; //not included
        int randomNumber = r.nextInt(high-low) + low;
        Log.d("random button : ", String.valueOf(randomNumber));
        return randomNumber;
    }


    private void getPopularMoviesLevelThree(MovieInterface movieInterface) {
        // Generate random page number between 21-60
        Random r = new Random();
        int low = 41; //included
        int high = 61; //not included
        int randomPageNumber = r.nextInt(high-low) + low;

        // Generate movieId between 0-19
        Random r1 = new Random();
        int low1 = 0; //included
        int high1 = 20; //not included
        int randomMovieId = r1.nextInt(high1-low1) + low1;

        Call<JsonObject> call = movieInterface.getPopularMovies(randomPageNumber);
        Log.d("call popular movies", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("Popular movie", String.valueOf(res));
                JsonElement results = res.get("results");
                int popularMovieId = results.getAsJsonArray().get(randomMovieId).getAsJsonObject().get("id").getAsInt();
                Log.d("Popular movie Id", String.valueOf(popularMovieId));

                //call the other methods
                movieDetailsLevelThree(movieInterface, popularMovieId);
                movieActorsLevelThree(movieInterface, popularMovieId);
                //call random proposition answers (parameter randomPageNumber to take the next page)
                generateRandomAnswers(movieInterface, randomPageNumber);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void movieDetailsLevelThree(MovieInterface movieInterface,int movieId) {
        Call<JsonObject> call = movieInterface.getMovieDetails(movieId);
        Log.d("call movie details level 3", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
                String originalTitle = res.get("title").getAsString();
                movieTitleAnswer = originalTitle;

                //get movie tagline, check if not JsonNull and not empty
                if (!res.get("tagline").isJsonNull() && !res.get("tagline").getAsString().isEmpty()) {
                    JsonElement movieTagline = res.get("tagline");
                    Log.d("Movie tagline", String.valueOf(movieTagline));
                    movieTaglineTxt.setText(movieTagline.toString());
                }
                else {
                    movieTaglineTxt.setText("no tagline found");
                }

                //get movie genres
                JsonArray movieGenre = res.get("genres").getAsJsonArray();
                Log.d("Movie Genre", String.valueOf(movieGenre));
                ArrayList<String> movieGenreArray = new ArrayList<String>();
                int genreArraySize = movieGenre.size();
                for (int i=0; i<genreArraySize;i++){
                    String genre = movieGenre.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                    movieGenreArray.add(genre);
                }
                String genre = movieGenreArray.toString().replace("[", "").replace("]", "");
                //display movie genres
                movieGenreTxt.setText(genre);

                correctButtonAnswer.setText(originalTitle);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void generateRandomAnswers(MovieInterface movieInterface, int randomPageNumber) {
        // Generate 5 movieIds between 0-19 on the next page as the right answer
        int num;
        for(num=0; num<5;num++){
            Log.d("random array", String.valueOf(num));

            Call<JsonObject> call = movieInterface.getPopularMovies(randomPageNumber + 1);
            Log.d("call answers options", String.valueOf(call.request().url()));
            int finalNum = num;
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject res = response.body();
                    JsonElement results = res.get("results");
                    String answerMovieTitle = results.getAsJsonArray().get(finalNum).getAsJsonObject().get("title").getAsString();
                    Log.d("Answer option movie title", answerMovieTitle);

                    // display in random button
                    for (Button b: allButtons){
                        if (b != correctButtonAnswer && b.getText().toString().isEmpty()){
                            b.setText(answerMovieTitle);
                            Log.d("test answer", answerMovieTitle);
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("failure", String.valueOf(t));
                }

            });
        }
    }
    private void movieActorsLevelThree (MovieInterface movieInterface, int movieId ) {
        Call<JsonObject> call = movieInterface.getMovieActors(movieId);
        Log.d("call movie actors", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                JsonElement cast = res.get("cast");

                // check if there are actors in the API
                if (cast.getAsJsonArray().size() != 0 ) {
                    String actorName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                    String actorName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString();

                    actor1Txt.setText(actorName1);
                    actor2Txt.setText(actorName2);
                }
                else {
                    actor1Txt.setText("no actors found");
                    actor2Txt.setText("no actors found");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    public void onButtonClick3(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();

        if (buttonText == movieTitleAnswer) {
            b.setBackgroundColor(Color.GREEN);
            score +=1;
            scoreTxt.setText(String.valueOf(score));
        }
        else {
            b.setBackgroundColor(Color.RED);
            correctButtonAnswer.setBackgroundColor(Color.GREEN);
        }
        // disable other buttons
        answer1Button.setClickable(false);
        answer2Button.setClickable(false);
        answer3Button.setClickable(false);
        answer4Button.setClickable(false);
        answer5Button.setClickable(false);
        answer6Button.setClickable(false);
        // enable nextQuestion button
        nextQuestionButton.setEnabled(true);
    }

    public void nextQuestionClick3(View view) {
        Log.d("scoreIntent", String.valueOf(score));

        if (nbQuestions < 10) {
            Intent intent = new Intent(ExpertActivity.this, ExpertActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", score);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, ScoreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", score);
            bundle.putInt("nbQuestion", nbQuestions);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
        }
    }
}