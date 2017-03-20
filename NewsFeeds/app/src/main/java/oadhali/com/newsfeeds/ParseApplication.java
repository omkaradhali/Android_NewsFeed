package oadhali.com.newsfeeds;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by omkaradhali on 3/7/17.
 */

public class ParseApplication {

    private static final String TAG = "ParseApplication";
    private ArrayList<FeedEntry> application;

    public ParseApplication() {
        this.application = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplication() {
        return application;
    }

    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                //Log.d(TAG, "TAGNAME :"+tagName);
                
                switch(eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Startting tag for "+tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue= xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for "+tagName);
                        if(inEntry){
                            if("entry".equalsIgnoreCase(tagName)){
                                application.add(currentRecord);
                                inEntry = false;
                            }else if("name".equalsIgnoreCase(tagName)){
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            } else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            } else if("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            } else if("image".equalsIgnoreCase(tagName)){
                                currentRecord.setImageUrl(textValue);
                            }
                        }
                        break;

                    default:
                        //do nothing

                }
                eventType = xpp.next();

            }
            for (FeedEntry app: application){
                Log.d(TAG, "parse: *******************");
                Log.d(TAG, app.toString());
           }

        }catch(Exception e){
            status = false;
            e.printStackTrace();
        }

        return status;

    }
}
