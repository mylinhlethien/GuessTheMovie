package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitPackage.DownloadImageTask;
import retrofitPackage.MovieInterface;
import retrofitPackage.MovieClass;
import retrofitPackage.PictureClass;

public class LevelOneActivity extends AppCompatActivity {

    TextView releaseDateTxt;
    TextView overviewTxt;
    TextView actor1Txt;
    TextView actor2Txt;
    TextView character1Txt;
    TextView character2Txt;
    TextView scoreTxt;
    TextView nbQuestionsTxt;
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    Button nextQuestionButton;
    ImageView imageActor1;
    ImageView imageActor2;
    ArrayList<Button> allButtons = new ArrayList<Button>();
    String movieTitleAnswer;
    Button correctButtonAnswer;
    static int score;
    static int nbQuestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_one);
        nbQuestions += 1;

        nbQuestionsTxt = findViewById(R.id.nbQuestions);
        scoreTxt = findViewById(R.id.score);
        releaseDateTxt = findViewById(R.id.releaseDate);
        overviewTxt = findViewById(R.id.overview);
        actor1Txt = findViewById(R.id.nameActor1);
        actor2Txt = findViewById(R.id.nameActor2);
        character1Txt = findViewById(R.id.nameCharacter1);
        character2Txt = findViewById(R.id.nameCharacter2);
        answer1Button = findViewById(R.id.Answer1button);
        answer2Button = findViewById(R.id.Answer2button);
        answer3Button = findViewById(R.id.Answer3button);
        answer4Button = findViewById(R.id.Answer4button);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        allButtons.add(answer1Button);
        allButtons.add(answer2Button);
        allButtons.add(answer3Button);
        allButtons.add(answer4Button);
        imageActor1 = findViewById(R.id.imageActor1);
        imageActor2 = findViewById(R.id.imageActor2);

        nbQuestionsTxt.setText(String.valueOf(nbQuestions));

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            int score = extras.getInt("score");
            scoreTxt.setText(String.valueOf(score));
        }

        if (nbQuestions == 10) {
            nextQuestionButton.setText("Finish");
        }

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);

        getPopularMovies(movieInterface, pictureInterface);

    }
    private void generateRandomAnswers(MovieInterface movieInterface, int randomPageNumber) {
        // Generate 3 movieIds between 0-19 on the next page as the right answer
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            list.add(i);
        }

        int num;
        for(num=0; num<4;num++){
                Log.d("random", String.valueOf(num));

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
                        // display in random button (salma)
                        for (int i=0; i<4;i++){
                            Button b = allButtons.get(i);
                            if (b.getText().equals("")){
                                b.setText(answerMovieTitle);
                                Log.d("test answer", i+ answerMovieTitle);
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

    private int getRandom (){
        Random r = new Random();
        int low = 0; //included
        int high = 4; //not included
        int randomNumber = r.nextInt(high-low) + low;
        Log.d("random button : ", String.valueOf(randomNumber));
        return randomNumber;
    }
    
    private void getPopularMovies(MovieInterface movieInterface, MovieInterface pictureInterface) {
        // Generate random page number between 1-20
        Random r = new Random();
        int low = 1; //included
        int high = 21; //not included
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
                movieDetailsLevelOne(movieInterface, popularMovieId);
                movieActors(movieInterface, pictureInterface, popularMovieId);
                movieActors(movieInterface,pictureInterface, popularMovieId);
                //call random proposition answers (parameter randomPageNumber to take the next page)
                generateRandomAnswers(movieInterface, randomPageNumber);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void movieActors(MovieInterface movieInterface, MovieInterface pictureInterface, int movieId) {
        Call<JsonObject> call = movieInterface.getMovieActors(movieId);
        Log.d("call movie actors", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                JsonElement cast = res.get("cast");
                String actorName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                String characterName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("character").getAsString();
                String actorPicture1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("profile_path").getAsString();
                Log.d("actorPicture 0 ", String.valueOf(actorPicture1));

                String actorName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("name").getAsString();
                String characterName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("character").getAsString();
                String actorPicture2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("profile_path").getAsString();
                Log.d("actorPicture 1 ", String.valueOf(actorPicture2));

                actor1Txt.setText(actorName1);
                actor2Txt.setText(actorName2);
                character1Txt.setText(characterName1);
                character2Txt.setText(characterName2);

                //display picture 1
                Call<JsonObject> pictureCall = pictureInterface.getMovieActorPicture(actorPicture1);
                Log.d("picture 1", String.valueOf(call.request().url()));
                new DownloadImageTask((ImageView) findViewById(R.id.imageActor1))
                        .execute(String.valueOf(pictureCall.request().url()));
                //display picture 2
                Call<JsonObject> pictureCall2 = pictureInterface.getMovieActorPicture(actorPicture2);
                Log.d("picture 2", String.valueOf(call.request().url()));
                new DownloadImageTask((ImageView) findViewById(R.id.imageActor2))
                        .execute(String.valueOf(pictureCall2.request().url()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void movieDetailsLevelOne(MovieInterface movieInterface, int movieId) {
        Call<JsonObject> call = movieInterface.getMovieDetails(movieId);
        Log.d("call movie details level 1", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                String originalTitle = res.get("original_title").getAsString();
                String overview = res.get("overview").getAsString();
                String releaseDate = res.get("release_date").getAsString();
                Log.d("title", originalTitle);
                movieTitleAnswer = originalTitle;

                releaseDateTxt.setText(releaseDate);
                overviewTxt.setText(overview);

                int randomButton = getRandom();
                correctButtonAnswer = allButtons.get(randomButton);
                correctButtonAnswer.setText(originalTitle);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    public void onButtonClick(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();

        if (buttonText == movieTitleAnswer) {
            b.setBackgroundColor(Color.GREEN);
            score+=1;
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
    }

    public void nextQuestionClick(View view) {
        Log.d("scoreIntent", String.valueOf(score));

        if (nbQuestions < 10) {
            Intent intent = new Intent(LevelOneActivity.this, LevelOneActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", score);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
            //LevelOneActivity.this.finish();
        }
        else {
            Intent intent = new Intent(this, ScoreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("score", score);
            bundle.putInt("nbQuestion", nbQuestions);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
            //LevelOneActivity.this.finish();
        }


    }
}