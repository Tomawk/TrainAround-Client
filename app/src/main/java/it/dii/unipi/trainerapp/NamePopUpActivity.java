package it.dii.unipi.trainerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.dii.unipi.trainerapp.Others.Preferences;

public class NamePopUpActivity extends Activity {

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
                Preferences.setAthleteName(inputData);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("athleteName",inputData);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                }
        });


    }

}