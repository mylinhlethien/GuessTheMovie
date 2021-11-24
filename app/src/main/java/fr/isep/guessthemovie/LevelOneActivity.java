package fr.isep.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

        MovieInterface movieInterface = MovieClass.getMovieInstance().create(MovieInterface.class);
        MovieInterface pictureInterface = PictureClass.getPictureInstance().create(MovieInterface.class);


        movieDetailsLevelOne(movieInterface);

        movieActors(movieInterface, pictureInterface);
        movieActors(movieInterface,pictureInterface);
    }

    private void movieActors(MovieInterface retrofitInterface, MovieInterface pictureInterface) {
        Call<JsonObject> call = retrofitInterface.getMovieActors("299534");
        Log.d("call", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
                JsonElement cast = res.get("cast");
                Log.d("cast", String.valueOf(cast));
                JsonElement actorName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("name");
                JsonElement characterName1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("character");
                JsonElement actorPicture1 = cast.getAsJsonArray().get(0).getAsJsonObject().get("profile_path");
                Log.d("actorName 0", String.valueOf(actorName1));
                Log.d("actorCharacter 0", String.valueOf(characterName1));
                Log.d("actorPicture 0 ", String.valueOf(actorPicture1));

                JsonElement actorName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("name");
                JsonElement characterName2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("character");
                JsonElement actorPicture2 = cast.getAsJsonArray().get(1).getAsJsonObject().get("profile_path");
                Log.d("actorName 0", String.valueOf(actorName2));
                Log.d("actorCharacter 0", String.valueOf(characterName2));
                Log.d("actorPicture 0 ", String.valueOf(actorPicture2));

                actor1Txt.setText(String.valueOf(actorName1));
                actor2Txt.setText(String.valueOf(actorName2));
                character1Txt.setText(String.valueOf(characterName1));
                character2Txt.setText(String.valueOf(characterName2));
                //getPictures(pictureInterface, actorPicture.getAsString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void movieDetailsLevelOne(MovieInterface retrofitInterface) {
        Call<JsonObject> call = retrofitInterface.getMovieDetails("299534");
        Log.d("call", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
                JsonElement originalTitle = res.get("original_title");
                JsonElement overview = res.get("overview");
                JsonElement releaseDate = res.get("release_date");
                Log.d("title", String.valueOf(originalTitle));
                Log.d("overview", String.valueOf(overview));
                Log.d("releaseDate", String.valueOf(releaseDate));

                releaseDateTxt.setText(String.valueOf(releaseDate));
                overviewTxt.setText(String.valueOf(overview));
                // title à afficher dans une des cases propositions
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }

    private void getPictures(MovieInterface pictureInterface, String picture_path) {
        Call<JsonObject> call = pictureInterface.getMovieActorPicture(picture_path);
        Log.d("call", String.valueOf(call.request().url()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                Log.d("res", String.valueOf(res));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("failure", String.valueOf(t));
            }

        });
    }
}