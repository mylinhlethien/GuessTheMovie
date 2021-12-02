package retrofitPackage;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieInterface {
    @GET("movie/{movieId}?api_key=64a67a88869b332a993437d0b110879b&language=en-US")
    Call<JsonObject> getMovieDetails(@Path("movieId") int movieId);

    @GET("movie/{movieId}/credits?api_key=64a67a88869b332a993437d0b110879b&language=en-US")
    Call<JsonObject> getMovieActors(@Path("movieId") int movieId);

    @GET("{picture_path}")
    Call<JsonObject> getMovieActorPicture(@Path("picture_path") String picture_path);

    @GET("movie/popular?api_key=64a67a88869b332a993437d0b110879b&language=en-US")
    Call<JsonObject> getPopularMovies(@Query("page") int page_number);

    @GET("{picture_path}")
    Call<JsonObject> getMovieBackdropPicture(@Path("picture_path") String backdropPicture_path);
}
