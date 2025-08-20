package com.example.androidgmail.api;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class LabelConverter {

    private static final Gson gson = new Gson();
    private static final Type LIST_STRING =
            new TypeToken<List<String>>() {}.getType();

    /** Converts a List to Json */
    @TypeConverter
    public static String fromList(List<String> list) {
        return (list == null || list.isEmpty())
                ? "[]"
                : gson.toJson(list);
    }

    /** Converts a Json to a list */
    @TypeConverter
    public static List<String> toList(String json) {
        return (json == null || json.isEmpty())
                ? Collections.emptyList()
                : gson.fromJson(json, LIST_STRING);
    }
}
