package com.example.mytestapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class NamePopUpActivity extends Activity {

    private String fileName = "nameDump.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_pop_up);

        Button settingsConfirm = (Button) findViewById(R.id.confirmation_btn);

        settingsConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //get the trainer name from the text input
                EditText trainerNameInput = (EditText) findViewById(R.id.name_edit);
                String inputData = trainerNameInput.getText().toString();
                if (!writeToFile(inputData, getApplicationContext(), fileName)) {
                    Toast.makeText(getApplicationContext(), "Error: unable to save settings", Toast.LENGTH_SHORT).show();
                } else {
                    //send back to MainActivity an intent with trainer's name attached
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("athleteName",inputData);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });


    }

    private boolean writeToFile(String data, Context context, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return true;
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }


}