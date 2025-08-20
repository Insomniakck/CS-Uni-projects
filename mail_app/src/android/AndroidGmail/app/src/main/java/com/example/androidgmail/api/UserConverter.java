package com.example.androidgmail.api;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class UserConverter {
    private static final Gson gson = new Gson();
    private static final Type MAP_TYPE =
            new TypeToken<Map<String,String>>(){}.getType();


    /** Converts a Map to Json */
    @TypeConverter
    public static String mapToJson(Map<String,String> map) {
        return map == null ? null : gson.toJson(map);
    }

    /** Converts a Json to a Map */
    @TypeConverter
    public static Map<String,String> jsonToMap(String json) {
        return json == null ? null : gson.fromJson(json, MAP_TYPE);
    }
}

