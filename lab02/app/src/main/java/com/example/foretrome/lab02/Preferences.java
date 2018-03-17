package com.example.foretrome.lab02;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.List;

public class Preferences extends AppCompatActivity {

    // Activity items
    Spinner spin;
    EditText textField;
    Button saveBtn;

    //Preferences
    SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "MyPrefs" ;

    // Needed values
    String url;
    int updateTime;
    int maxItem;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        addTypesItemToSpin();
        addMaxItemToSpin();
        addFetchItemToSpin();

        getPrefValues();

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                savePref();
            }
        });

    }

    //Loads the preferences to
    private void getPrefValues()
    {
        int savedPos;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //String urlString = sharedpreferences.getString("url", "http://rss.cnn.com/rss/edition_football.rss");

        textField = findViewById(R.id.urlInput);
        textField.setText(sharedpreferences.getString("url", "https://www.vg.no/rss/feed/"));

        spin = findViewById(R.id.prefTypeSpin);
        savedPos = sharedpreferences.getInt("type",0 );
        spin.setSelection(savedPos);

        spin = findViewById(R.id.prefCountSpin);
        savedPos = sharedpreferences.getInt("amount",0 );
        spin.setSelection(savedPos);

        spin = findViewById(R.id.prefFetchSpin);
        savedPos = sharedpreferences.getInt("updateTime",0 );
        spin.setSelection(savedPos);
    }

    private void savePref()
    {
        int position;

        SharedPreferences.Editor editor = sharedpreferences.edit();

        spin = findViewById(R.id.prefTypeSpin);
        position = spin.getSelectedItemPosition();
        editor.putInt("type", position);

        spin = findViewById(R.id.prefCountSpin);
        position = spin.getSelectedItemPosition();
        editor.putInt("amount", position);

        spin = findViewById(R.id.prefFetchSpin);
        position = spin.getSelectedItemPosition();
        editor.putInt("updateTime", position);

        editor.putString("url", textField.getText().toString());
        editor.commit(); // commit changes

        Toast.makeText(getApplicationContext(), "Your changes are saved!", Toast.LENGTH_LONG).show();
    }

    // add items into spinner dynamically
    public void addTypesItemToSpin()
    {
        spin = findViewById(R.id.prefTypeSpin);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typeName, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spin.setAdapter(adapter);

        spin = null;
    }

    // add items into spinner dynamically
    public void addMaxItemToSpin()
    {
        spin = findViewById(R.id.prefCountSpin);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typeCount, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spin.setAdapter(adapter);

        spin = null;
    }

    // add items into spinner dynamically
    public void addFetchItemToSpin()
    {
        spin = findViewById(R.id.prefFetchSpin);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.FetchTime, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spin.setAdapter(adapter);

        spin = null;
    }

    @Override
    public void onBackPressed()
    {
        Intent startList = new Intent(Preferences.this, MainActivity.class);
        startList.putExtra("url",getUrl());
        startList.putExtra("amount",getMaxItem());
        startList.putExtra("updateTime",getRate());
        startActivity(startList);
    }

    public String getUrl()
    {
        return textField.getText().toString();
    }

    public int getMaxItem()
    {
        int item;
        spin = findViewById(R.id.prefCountSpin);
        item = spin.getSelectedItemPosition();

        switch (item)
        {
            case (1): item = 20; break;
            case (2): item = 50; break;
            case (3): item = 100; break;
            default: item = 10; break;
        }
        return item;
    }

    public int getRate()
    {
        int rate;
        spin = findViewById(R.id.prefFetchSpin);
        rate = spin.getSelectedItemPosition();

        switch (rate)
        {
            case (1): rate = 60000; break;
            case (2): rate = 10000000; break;
            default: rate = 10000; break;
        }

        return rate;
    }
}
