package com.example.FindShareParking.Utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.FindShareParking.Objects.MySharedPref;
import com.example.FindShareParking.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefUtils {
    @SuppressLint("StaticFieldLeak")
    private static SharedPrefUtils instance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor prefPut;
    private final Context context;

    @SuppressLint("CommitPrefEdits")
    private SharedPrefUtils(Context context) {
        pref = context.getSharedPreferences("MY_SP", Context.MODE_PRIVATE);
        prefPut = pref.edit();
        this.context = context;
    }

    public static SharedPrefUtils getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtils(context);
        }
    }

    public void putString(String key, String value) {
        prefPut.putString(key, value);
        prefPut.apply();
    }

    public void putInt(String key, int value) {
        prefPut.putInt(key, value);
        prefPut.apply();

    }

    public void putBoolean(String key, boolean value) {
        prefPut.putBoolean(key, value);
        prefPut.apply();
    }

    public void putFloat(String key, float value) {
        prefPut.putFloat(key, value);
        prefPut.apply();

    }

    public String getString(String key, String def) {
        return pref.getString(key, def);
    }

    public int getInt(String key, int value) {
        return pref.getInt(key, value);


    }

    public boolean getBoolean(String key, boolean value) {
        return pref.getBoolean(key, value);

    }

    public float getFloat(String key, float value) {
        return pref.getFloat(key, value);


    }

    public void ClearCache(String key) {
        prefPut.remove(key);
        prefPut.apply();
    }

    public void setPassword(String password) {
        int len = password.length();
        len /= 2;
        StringBuilder b1 = new StringBuilder(password.substring(0, len));
        StringBuilder b2 = new StringBuilder(password.substring(len));
        b1.reverse();
        b2.reverse();
        password = b1.toString() + b2.toString();

        prefPut.putString("password", password);
        prefPut.apply();
    }

    public String getPassword() {
        String password = pref.getString("password", "0");
        int len = password.length();
        len /= 2;
        StringBuilder b1 = new StringBuilder(password.substring(0, len));
        StringBuilder b2 = new StringBuilder(password.substring(len));
        password = b1.reverse().toString() + b2.reverse().toString();
        return password;
    }

    /**
     * the method return ArrayList of object(dependent of your type you saved) from shared Preferences
     * using json
     *
     * @param key   set key of shared preferences
     * @param Class set class same as your arrayList type you saved
     * @return if there isn't data saved in the key value the method return empty arrayList, otherWise
     * return list from Shared Preferences that you saved
     */
    public <T> ArrayList<T> getListFromPrefJson(String key, Class<T> Class) {
        ArrayList<T> list;
        Gson gson = new Gson();
        String ttJson = SharedPrefUtils.getInstance().getString(key, null);
        if (ttJson == null) {
            list = new ArrayList<>();
        } else {
            Type collectionType = TypeToken.getParameterized(ArrayList.class, Class).getType();
            list = gson.fromJson(ttJson, collectionType);
        }
        return list;
    }

    public String getSummaryFromSettingsListPreferencePref() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context);
        String[] TypeOfParings = context.getResources().getStringArray(R.array.entriesTypeOfParking);
        int index = Integer.parseInt(settings.getString(MySharedPref.TYPE_OF_PARKING_KEY, "0"));
        return TypeOfParings[index];


    }
}