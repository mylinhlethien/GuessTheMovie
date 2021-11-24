package retrofitPackage;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieInterface {
    @GET("movie/{movieId}?api_key=64a67a88869b332a993437d0b110879b&language=en-US")
    Call<JsonObject> getMovieDetails(@Path("movieId") String movieId);

    @GET("movie/{movieId}/credits?api_key=64a67a88869b332a993437d0b110879b&language=en-US")
    Call<JsonObject> getMovieActors(@Path("movieId") String movieId);

    @GET("{picture_path}")
    Call<JsonObject> getMovieActorPicture(@Path("picture_path") String picture_path);
}
