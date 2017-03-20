package oadhali.com.newsfeeds;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        Log.d(TAG, "onCreate: Starting ASYNCTask");
        DownloadData downLoadData = new DownloadData();
        downLoadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG,"onCreate:Done");

    }


    //<String1,void,String2> String1 = URL; void = progress bar; String2 = result
    private class DownloadData extends AsyncTask<String , Void, String>{

        private static final String TAG = "DownloadData";


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is "+s);
            ParseApplication parseApplication = new ParseApplication();
            parseApplication.parse(s);

            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this,R.layout.list_item,parseApplication.getApplication());
            listApps.setAdapter(arrayAdapter);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground: Starts with "+params[0]);
            String rssFeed = downloadXML(params[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error Downloading" );
            }
            //Log.d(TAG, "doInBackground: RSS FEED"+rssFeed);
            return rssFeed;
        }

        private String downloadXML(String param) {

            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(param);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int res = connection.getResponseCode();
                Log.d(TAG, "downloadXML: response code is "+res);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];

                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0){
                        break;
                    }
                   if(charsRead > 0){
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                   }
                }
                reader.close();
                return xmlResult.toString();
            } catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL"+ e.getMessage() );
            } catch (IOException e){
                Log.e(TAG, "downloadXML: IO connection"+ e.getMessage() );
            }
            return null;//String.valueOf(xmlResult);
        }
    }
}
