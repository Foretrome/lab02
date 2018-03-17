package com.example.foretrome.lab02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList title = new ArrayList();
    ArrayList link = new ArrayList();

    URL url;

    String urlString;
    int updateTime;
    int amountItem;
    int type;

    SharedPreferences sharedPref;
    private static final String MyPREFERENCES = "MyPrefs" ;

    Button temp;
    Button preferences;
    Button fetchButton;

    private Handler fetch = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPref();

        listView = findViewById(R.id.rssList);

        makeButtonsDoStuff();

        Intent receive = getIntent();
        urlString =receive.getStringExtra("url");
        Toast.makeText(getApplicationContext(),urlString,Toast.LENGTH_LONG).show();
        amountItem = receive.getIntExtra("itemN",10);
        updateTime = receive.getIntExtra("rate",10000);

        if (urlString == null) {
            urlString = load();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String linkOut = (String) link.get(i);
                String titleOut = (String) title.get(i);
                Intent intent = new Intent(getBaseContext(), ContentDisplay.class);
                intent.putExtra("linkOutput", linkOut);
                intent.putExtra("titleOutput", titleOut);
                startActivity(intent);
            }
        });


        // Run
        Feed.run();
    }


    private void makeButtonsDoStuff() {
        temp = findViewById(R.id.openPreferences);

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Preferences.class));
            }
        });

        temp = findViewById(R.id.fetchUrl);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceFetch();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }


    private String load() {
        SharedPreferences loadPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String loadedUrl = loadPref.getString("url", "https://www.vg.no/rss/feed");
        return loadedUrl;
    }


    private void getPref() {
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //String urlString = sharedPref.getString("url", "http://rss.cnn.com/rss/edition_football.rss");
        urlString = sharedPref.getString("url", "https://www.vg.no/rss/feed/");

        type = sharedPref.getInt("type",0 );
        updateTime = sharedPref.getInt("updateTime", 0);
        amountItem = sharedPref.getInt("amount", 0);

        switch (amountItem)
        {
            case (1): amountItem = 20; break;
            case (2): amountItem = 50; break;
            case (3): amountItem = 100; break;
            default: amountItem = 10; break;
        }

        switch (updateTime)
        {
            case (1): updateTime = 60000; break;
            case (2): updateTime = 10000000; break;
            default: updateTime = 10000; break;
        }
    }


    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }


    private Runnable Feed = new Runnable()
    {
        @Override
        public void run()
        {
            new ProcessInBackground().execute();
            fetch.postDelayed(this, updateTime);
        }
    };


    /**
     * Force an update
     */
    public void forceFetch()
    {
        Feed.run();
    }


    // Integer1 = Input. F.eks url. Integer2 = Increment progressdialog eller w/e.
    // String = Hva doInBackground returnerer(I dette tilfellet en exception, men det kan endres til å returnere url som string?.
    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try {
                //URL url = new URL(urlDisplay); // http://feed.androidauthority.com/
                if (urlString == null) {
                    url = new URL("https://www.vg.no/rss/feed/");
                } else {
                    url = new URL(urlString);
                }

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                title.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                link.add((xpp.nextText()));
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next();

                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, title);
            listView.setAdapter(adapter);
        }
    }
}
