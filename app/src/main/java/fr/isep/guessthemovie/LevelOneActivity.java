package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

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
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    ImageView imageActor1;
    ImageView imageActor2;
    ArrayList<Button> allButtons = new ArrayList<Button>();
    String movieTitleAnswer;
    Button correctButtonAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_one);

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
        allButtons.add(answer1Button);
        allButtons.add(answer2Button);
        allButtons.add(answer3Button);
        allButtons.add(answer4Button);
        imageActor1 = findViewById(R.id.imageActor1);
        imageActor2 = findViewById(R.id.imageActor2);

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

        Collections.shuffle(list);
        Integer[] randomArray = list.subList(0, 3).toArray(new Integer[3]);


        for(Integer num:randomArray){
                Log.d("random", String.valueOf(num));

                Call<JsonObject> call = movieInterface.getPopularMovies(randomPageNumber + 1);
                Log.d("call answers options", String.valueOf(call.request().url()));
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject res = response.body();
                        JsonElement results = res.get("results");
                        String answerMovieTitle = results.getAsJsonArray().get(num).getAsJsonObject().get("original_title").getAsString();
                        Log.d("Answer option movie title", answerMovieTitle);
                        // display in random button (0-2)
                        Random r = new Random();
                        int low = 0; //included
                        int high = allButtons.size(); //not included
                        int randomButton = r.nextInt(high-low) + low;
                        allButtons.get(randomButton).setText(answerMovieTitle);
                        allButtons.remove(randomButton);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("failure", String.valueOf(t));
                    }

                });
        }


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

                // display in random button (0-3)
                Random r = new Random();
                int low = 0; //included
                int high = 3; //not included
                int randomButton = r.nextInt(high-low) + low;
                allButtons.get(randomButton).setText(originalTitle);
                correctButtonAnswer = allButtons.get(randomButton);
                allButtons.remove(randomButton);
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
        // update score
    }

    public void nextQuestionClick(View view) {
        //start same activity and pass score through Intent
    }
}