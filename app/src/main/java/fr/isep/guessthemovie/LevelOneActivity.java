package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    ImageView imageActor1;
    ImageView imageActor2;

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
        imageActor1 = findViewById(R.id.imageActor1);
        imageActor2 = findViewById(R.id.imageActor2);

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);

        getPopularMovies(movieInterface, pictureInterface);

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
                JsonElement popularMovieId = results.getAsJsonArray().get(randomMovieId).getAsJsonObject().get("id");
                Log.d("Popular movie Id", String.valueOf(popularMovieId));


                //call the other methods
                movieDetailsLevelOne(movieInterface, Integer.parseInt(String.valueOf(popularMovieId)));
                movieActors(movieInterface, pictureInterface, Integer.parseInt(String.valueOf(popularMovieId)));
                movieActors(movieInterface,pictureInterface, Integer.parseInt(String.valueOf(popularMovieId)));
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
                //Log.d("res", String.valueOf(res));
                JsonElement cast = res.get("cast");
                //Log.d("cast", String.valueOf(cast));
                JsonElement actorName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("name");
                JsonElement characterName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("character");
                JsonElement actorPicture1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("profile_path");
                //Log.d("actorName 0", String.valueOf(actorName1));
                //Log.d("actorCharacter 0", String.valueOf(characterName1));
                Log.d("actorPicture 0 ", String.valueOf(actorPicture1));

                JsonElement actorName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("name");
                JsonElement characterName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("character");
                JsonElement actorPicture2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("profile_path");
               // Log.d("actorName 1", String.valueOf(actorName2));
                //Log.d("actorCharacter 1", String.valueOf(characterName2));
                Log.d("actorPicture 1 ", String.valueOf(actorPicture2));

                actor1Txt.setText(String.valueOf(actorName1));
                actor2Txt.setText(String.valueOf(actorName2));
                character1Txt.setText(String.valueOf(characterName1));
                character2Txt.setText(String.valueOf(characterName2));

                //display picture 1
                Call<JsonObject> pictureCall = pictureInterface.getMovieActorPicture(actorPicture1.getAsString());
                Log.d("picture 1", String.valueOf(call.request().url()));
                new DownloadImageTask((ImageView) findViewById(R.id.imageActor1))
                        .execute(String.valueOf(pictureCall.request().url()));
                //display picture 2
                Call<JsonObject> pictureCall2 = pictureInterface.getMovieActorPicture(actorPicture2.getAsString());
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
                //Log.d("res", String.valueOf(res));
                JsonElement originalTitle = res.get("original_title");
                JsonElement overview = res.get("overview");
                JsonElement releaseDate = res.get("release_date");
                Log.d("title", String.valueOf(originalTitle));
                //Log.d("overview", String.valueOf(overview));
                //Log.d("releaseDate", String.valueOf(releaseDate));

                releaseDateTxt.setText(String.valueOf(releaseDate));
                overviewTxt.setText(String.valueOf(overview));
                answer1Button.setText(String.valueOf(originalTitle));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }
}