package com.example.mytestapplication.Others;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class Preferences implements SharedPreferences {

    private static final String ATHLETE_NAME_KEY = "athleteName";
    private static final String ATHLETE_NAME_NOT_FOUND = "Name not inserted";
    private static final String ATHLETE_SURNAME_KEY = "athleteSurname";
    private static final String ATHLETE_SURNAME_NOT_FOUND = "Surname not inserted";
    private static final String ATHLETE_AGE_KEY = "athleteAge";
    private static final String ATHLETE_AGE_NOT_FOUND = "Age not inserted";
    private static final String ATHLETE_WEIGHT_KEY = "athleteWeight";
    private static final String ATHLETE_WEIGHT_NOT_FOUND = "Weight not inserted";
    private static final String ATHLETE_HEIGHT_KEY = "athleteHeight";
    private static final String ATHLETE_HEIGHT_NOT_FOUND = "Height not inserted";
    private static final String FEEDBACK_RATING = "listRating";
    private static final String FEEDBACK_COMMENT = "addComment";


    private static Preferences myPreferences;
    private static SharedPreferences sharedPreferences;
    private static Editor editor;

    private Preferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public static Preferences getPreferences(Context context) {
        if (myPreferences == null) myPreferences = new Preferences(context);
        return myPreferences;
    }

    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        return sharedPreferences.getString(s,s1);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        return sharedPreferences.getStringSet(s,set);
    }

    @Override
    public int getInt(String s, int i) {
        return sharedPreferences.getInt(s,i);
    }

    @Override
    public long getLong(String s, long l) {
        return sharedPreferences.getLong(s,l);
    }

    @Override
    public float getFloat(String s, float v) {
        return sharedPreferences.getFloat(s,v);
    }

    @Override
    public boolean getBoolean(String s, boolean defaultValue) {
        return sharedPreferences.getBoolean(s,defaultValue);
    }

    @Override
    public boolean contains(String s) {
        return sharedPreferences.contains(s);
    }

    @Override
    public Editor edit() {
        return sharedPreferences.edit();
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static void setAthleteName(String name){
        editor.putString(ATHLETE_NAME_KEY, name);
        editor.apply();
    }

    public static String getAtheleteName(){
        return sharedPreferences.getString(ATHLETE_NAME_KEY, ATHLETE_NAME_NOT_FOUND);
    }

    public static String getAthleteSurname(){
        return sharedPreferences.getString(ATHLETE_SURNAME_KEY, ATHLETE_SURNAME_NOT_FOUND);
    }

    public static String getAthleteAge(){
        return sharedPreferences.getString(ATHLETE_AGE_KEY, ATHLETE_AGE_NOT_FOUND);
    }

    public static String getAthleteWeight(){
        return sharedPreferences.getString(ATHLETE_WEIGHT_KEY, ATHLETE_WEIGHT_NOT_FOUND);
    }

    public static String getAthleteHeight(){
        return sharedPreferences.getString(ATHLETE_HEIGHT_KEY, ATHLETE_HEIGHT_NOT_FOUND);
    }

    public static String getFeedbackComment(){
        return sharedPreferences.getString(FEEDBACK_COMMENT, "Comment not inserted");
    }

    public static String getFeedbackRating(){
        return sharedPreferences.getString(FEEDBACK_RATING, "Comment not inserted");
    }

}
