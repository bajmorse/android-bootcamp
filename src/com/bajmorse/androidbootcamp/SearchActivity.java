package com.bajmorse.androidbootcamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	Activity mActivity = this; 
	private List<HeadlineEntity> searchHeadlines = new ArrayList<HeadlineEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupActionBar();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public void searchESPN(View view) {
        ESPNSearchASYNC task = new ESPNSearchASYNC(); 
        task.execute();
	}
	
    private class ESPNSearchASYNC extends AsyncTask<String, Integer, Double> {
      	 
    	private final String ESPN_KEY = "ncxc4z72h9pda94pvcw27c3p";
    	private final String SEARCH_URL_START = "http://api.espn.com/v1/sports/";
    	private final String SEARCH_URL_END = "/news/headlines?apikey=";
    	    	
        protected Double doInBackground(String... urls) {
    		EditText searchText = (EditText) findViewById(R.id.search_text);
            String url = SEARCH_URL_START + searchText.getText() + SEARCH_URL_END + ESPN_KEY;
            HttpClient client = new DefaultHttpClient(); 
            HttpPost post = new HttpPost(url);
            
            HttpResponse response; 
            try {
            	response = client.execute(post);
            	
            	HttpEntity entity = response.getEntity(); 
            	String resultString = EntityUtils.toString(entity);
//            	Log.w("com.bajmorse.androidbootcamp", resultString);
            	JSONObject responseObject = new JSONObject(resultString);
            	
            	JSONArray headlinesArray = null; 
            	try {
            		headlinesArray = responseObject.getJSONArray("headlines");
            	} catch (JSONException e) {
            		searchHeadlines.clear(); 
                	return null;
            	}
            	
            	searchHeadlines.clear();
            	for (int i = 0; i < headlinesArray.length(); ++i) {
            		JSONObject headlineJSON = headlinesArray.getJSONObject(i);
            		HeadlineEntity headline = new HeadlineEntity(); 
            		headline.setHeadline(headlineJSON.getString("headline"));
            		headline.setLink(headlineJSON.getJSONObject("links").getJSONObject("mobile").getString("href"));            		
            		JSONArray headlinePhotoArray = headlineJSON.getJSONArray("images");
            		if (headlinePhotoArray.length() > 0) {
	            		JSONObject headlinePhoto = (JSONObject) headlineJSON.getJSONArray("images").get(0); 
	            		headline.setPictureURL(headlinePhoto.getString("url"));
            		} else {
            			headline.setPictureURL("http://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/ESPN_wordmark.svg/800px-ESPN_wordmark.svg.png");
            		}
            		searchHeadlines.add(headline);

            	}
       		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (JSONException e) {
    			e.printStackTrace(); 
    		}   
            return null; 
        }

        protected void onProgressUpdate(Integer... progress) {
        }
        
        protected void onPostExecute(Double response) {
            ListView espnListView = (ListView) findViewById(R.id.ESPNSearchList);
            HeadlineAdapter adapter = new HeadlineAdapter(mActivity, searchHeadlines);
            espnListView.setAdapter(adapter);
        }
    }

}
