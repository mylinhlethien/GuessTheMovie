package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
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
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    ArrayList<Button> allButtons = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two);

        releaseDateTxt = findViewById(R.id.releaseDateLevelTwo);
        movieGenreTxt = findViewById(R.id.movieGenreLevelTwo);
        answer1Button = findViewById(R.id.Answer1buttonLevelTwo);
        answer1Button = findViewById(R.id.Answer1buttonLevelTwo);
        answer2Button = findViewById(R.id.Answer2buttonLevelTwo);
        answer3Button = findViewById(R.id.Answer3buttonLevelTwo);
        answer4Button = findViewById(R.id.Answer4buttonLevelTwo);
        allButtons.add(answer1Button);
        allButtons.add(answer2Button);
        allButtons.add(answer3Button);
        allButtons.add(answer4Button);

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);

        getPopularMoviesLevelTwo(movieInterface, pictureInterface);

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


    private void movieDetailsLevelTwo(MovieInterface movieInterface,MovieInterface pictureInterface, int movieId) {
        Call<JsonObject> call = movieInterface.getMovieDetails(movieId);
        Log.d("call movie details level 2", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
                String originalTitle = res.get("original_title").getAsString();

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


                //display backdrop image of the movie
                String backdropPicture = res.get("backdrop_path").getAsString();
                Log.d("backdropPicture", String.valueOf(backdropPicture));
                //display backdrop movie picture
                Call<JsonObject> pictureCall2 = pictureInterface.getMovieBackdropPicture(backdropPicture);
                Log.d("backdropPicture", String.valueOf(call.request().url()));
                new DownloadImageTask((ImageView) findViewById(R.id.backdropImageLevelTwo))
                        .execute(String.valueOf(pictureCall2.request().url()));

                // display in random button (0-3)
                Random r = new Random();
                int low = 0; //included
                int high = 3; //not included
                int randomButton = r.nextInt(high-low) + low;
                Log.d("random num", String.valueOf(randomButton));
                allButtons.get(randomButton).setText(originalTitle);
                allButtons.remove(randomButton);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }
}