package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitPackage.DownloadImageTask;
import retrofitPackage.MovieClass;
import retrofitPackage.MovieInterface;
import retrofitPackage.PictureClass;

public class LevelTwoActivity extends AppCompatActivity {

    TextView releaseDateTxt;
    TextView movieGenreTxt;
    TextView scoreTxt;
    TextView nbQuestionsTxt;
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    Button nextQuestionButton;

    ArrayList<Button> allButtons = new ArrayList<Button>();
    String movieTitleAnswer;
    Button correctButtonAnswer;
    static int scoreLevelTwo;
    static int nbQuestionsLevelTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two);
        nbQuestionsLevelTwo += 1;

        nbQuestionsTxt = findViewById(R.id.nbQuestionsLevelTwo);
        releaseDateTxt = findViewById(R.id.releaseDateLevelTwo);
        movieGenreTxt = findViewById(R.id.movieGenreLevelTwo);
        scoreTxt = findViewById(R.id.scoreLevelTwo);
        answer1Button = findViewById(R.id.Answer1buttonLevelTwo);
        answer2Button = findViewById(R.id.Answer2buttonLevelTwo);
        answer3Button = findViewById(R.id.Answer3buttonLevelTwo);
        answer4Button = findViewById(R.id.Answer4buttonLevelTwo);
        nextQuestionButton = findViewById(R.id.nextQuestionButtonLevelTwo);
        allButtons.add(answer1Button);
        allButtons.add(answer2Button);
        allButtons.add(answer3Button);
        allButtons.add(answer4Button);

        nbQuestionsTxt.setText(String.valueOf(nbQuestionsLevelTwo));

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            int score = extras.getInt("score");
            scoreTxt.setText(String.valueOf(score));
        }

        if (nbQuestionsLevelTwo == 10) {
            nextQuestionButton.setText("Finish");
        }



        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);

        // define where the correct answer will be
        int randomButton = getRandom();
        correctButtonAnswer = allButtons.get(randomButton);
        getPopularMoviesLevelTwo(movieInterface, pictureInterface);
    }


    private void generateRandomAnswers(MovieInterface movieInterface, int randomPageNumber) {
        // Generate 3 movieIds between 0-19 on the next page as the right answer
        int num;
        for(num=0; num<3;num++){
            Log.d("random array", String.valueOf(num));

            Call<JsonObject> call = movieInterface.getPopularMovies(randomPageNumber + 1);
            Log.d("call answers options", String.valueOf(call.request().url()));
            int finalNum = num;
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject res = response.body();
                    JsonElement results = res.get("results");
                    String answerMovieTitle = results.getAsJsonArray().get(finalNum).getAsJsonObject().get("original_title").getAsString();
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

    private void getPopularMoviesLevelTwo(MovieInterface movieInterface, MovieInterface pictureInterface) {
        // Generate random page number between 21-40
        Random r = new Random();
        int low = 21; //included
        int high = 41; //not included
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
                movieDetailsLevelTwo(movieInterface, pictureInterface, popularMovieId);
                //call random proposition answers (parameter randomPageNumber to take the next page)
                generateRandomAnswers(movieInterface, randomPageNumber);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }


    private int getRandom (){
        Random r = new Random();
        int low = 0; //included
        int high = 4; //not included
        int randomNumber = r.nextInt(high-low) + low;
        Log.d("random button : ", String.valueOf(randomNumber));
        return randomNumber;
    }


    private void movieDetailsLevelTwo(MovieInterface movieInterface,MovieInterface pictureInterface, int movieId) {
        Call<JsonObject> call = movieInterface.getMovieDetails(movieId);
        Log.d("call movie details level 2", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
                String originalTitle = res.get("original_title").getAsString();
                movieTitleAnswer = originalTitle;

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

                //display release date of the movie
                String releaseDate = res.get("release_date").getAsString();
                releaseDateTxt.setText(String.valueOf(releaseDate));


                if (!res.get("backdrop_path").isJsonNull()) {
                    //display backdrop image of the movie
                    String backdropPicture = res.get("backdrop_path").getAsString();
                    Log.d("backdropPicture", String.valueOf(backdropPicture));
                    //display backdrop movie picture
                    Call<JsonObject> pictureCall2 = pictureInterface.getMovieBackdropPicture(backdropPicture);
                    Log.d("backdropPicture", String.valueOf(call.request().url()));
                    new DownloadImageTask((ImageView) findViewById(R.id.backdropImageLevelTwo))
                            .execute(String.valueOf(pictureCall2.request().url()));
                }
                else {
                    //afficher "no picture available for this movie"
                    Log.d("no picture available for this movie", "");
                }

                correctButtonAnswer.setText(originalTitle);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    public void onButtonClick2(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();

        if (buttonText == movieTitleAnswer) {
            b.setBackgroundColor(Color.GREEN);
            scoreLevelTwo+=1;
            scoreTxt.setText(String.valueOf(scoreLevelTwo));
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
    }

    public void nextQuestionClick2(View view) {
        Log.d("scoreIntent", String.valueOf(scoreLevelTwo));

        if (nbQuestionsLevelTwo < 10) {
            Intent intent = new Intent(LevelTwoActivity.this, LevelTwoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", scoreLevelTwo);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, ScoreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", scoreLevelTwo);
            bundle.putInt("nbQuestion", nbQuestionsLevelTwo);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
        }
    }
}