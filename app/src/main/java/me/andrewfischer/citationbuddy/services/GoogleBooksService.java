package me.andrewfischer.citationbuddy.services;

import android.app.Service;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import me.andrewfischer.citationbuddy.models.GoogleBookResult;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by afischer on 11/27/17.
 */

public class GoogleBooksService {
    private final String TAG = "GoogleBookService";
    private static Service service;


    public interface Service {
        @GET("volumes")
        Call<GetResponse> getBook(@Query("q") String query);
    }

    public class GetResponse {
        @SerializedName("items")
        public List<GoogleBookResult> books;
    }


    // Pulls out list of results
    public static class GetResponseSerializer implements JsonDeserializer<GetResponse> {

        private final String TAG = "BookServiceDeserializer";

        @Override
        public GetResponse deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            // change form id to book_id to allow for DB saving without drama
            String replaceId = je.getAsJsonObject().toString().replace("\"id\"", "\"book_id\"");
            GsonBuilder builder = new GsonBuilder();
            Log.d(TAG, replaceId);
            return new Gson().fromJson(replaceId, GetResponse.class);
        }
    }



    public static Service getService() {
        if (service == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();


            gsonBuilder.registerTypeHierarchyAdapter(GetResponse.class, new GetResponseSerializer());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/books/v1/")
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .build();

            service = retrofit.create(Service.class);
        }
        return service;
    }

}
