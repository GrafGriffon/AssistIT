package com.example.griffoassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ShowActivity extends AppCompatActivity {

    private TextView tvName, tvAuthor, tvText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        init();
        addIntent();
    }

    private void init() {
        tvName = findViewById(R.id.nameArt);
        tvAuthor = findViewById(R.id.nameAuthor);
        tvText = findViewById(R.id.textArt);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(3);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                setSize(Integer.parseInt(spinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setSize(int size) {
        tvName.setTextSize(size + 2);
        tvAuthor.setTextSize(size);
        tvText.setTextSize(size);
    }

    private void addIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            tvName.setText(intent.getStringExtra("art_name"));
            tvAuthor.setText(intent.getStringExtra("art_auth"));
            tvText.setText(intent.getStringExtra("art_text"));
        }
    }
}