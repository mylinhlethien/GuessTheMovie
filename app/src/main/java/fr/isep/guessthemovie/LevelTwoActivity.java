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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two);

        releaseDateTxt = findViewById(R.id.releaseDateLevelTwo);
        movieGenreTxt = findViewById(R.id.movieGenreLevelTwo);
        answer1Button = findViewById(R.id.Answer1buttonLevelTwo);

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);

        getPopularMoviesLevelTwo(movieInterface, pictureInterface);

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
                JsonElement originalTitle = res.get("original_title");

                //Il faut récupérer les genre ids
                //JsonArray movieGenre = res.get("genre_ids").getAsJsonArray();

               // Log.d("Movie Genre", String.valueOf(movieGenre));

                JsonElement releaseDate = res.get("release_date");
                Log.d("releaseDate", String.valueOf(releaseDate));


                releaseDateTxt.setText(String.valueOf(releaseDate));
                //movieGenreTxt.setText(String.valueOf(movieGenre));

                answer1Button.setText(String.valueOf(originalTitle));


                String backdropPicture = res.get("backdrop_path").getAsString();
                Log.d("backdropPicture", String.valueOf(backdropPicture));
                //display backdrop movie picture
                Call<JsonObject> pictureCall2 = pictureInterface.getMovieBackdropPicture(backdropPicture);
                Log.d("backdropPicture", String.valueOf(call.request().url()));
                new DownloadImageTask((ImageView) findViewById(R.id.backdropImageLevelTwo))
                        .execute(String.valueOf(pictureCall2.request().url()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }
}