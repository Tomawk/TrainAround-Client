package com.example.mytestapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.mytestapplication.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

     Button button = (Button)findViewById(R.id.btn_1);
     button.setFocusable(true);
     button.setFocusableInTouchMode(true);
     button.requestFocus();

     Button button2 = (Button)findViewById(R.id.btn_2);
     button2.setFocusable(true);
     button2.setFocusableInTouchMode(true);
     button2.requestFocus();

     mTextView = binding.text;
    }
}