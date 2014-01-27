package com.bajmorse.androidbootcamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils; 
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	Activity mActivity = this; 
	List<HeadlineEntity> headlines = new ArrayList<HeadlineEntity>(); 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView espnListView = (ListView) findViewById(R.id.ESPNList);
        espnListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(headlines.get(position).getLink()));
            	startActivity(browserIntent);
            }
        });
        
        ESPNFeedASYNC task = new ESPNFeedASYNC(false); 
        task.execute();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Refresh Page");
        menu.add(0, 1, 0, "Search ESPN");
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	if (item.getItemId() == 0) {
    		ESPNFeedASYNC task = new ESPNFeedASYNC(true);
    		task.execute();
    	} else if (item.getItemId() == 1) {
    		Intent searchIntent = new Intent(this, SearchActivity.class);  
    		startActivity(searchIntent);      	
    	}
    	    	
    	return true; 
    }
    
    private class ESPNFeedASYNC extends AsyncTask<String, Integer, Double> {
   	 
    	private final String ESPN_KEY = "ncxc4z72h9pda94pvcw27c3p";
    	private final String HEADLINES_URL = "http://api.espn.com/v1/sports/news/headlines?apikey=";
    	private final boolean mRefresh; 
    	
    	public ESPNFeedASYNC(boolean refresh){
    		mRefresh = refresh; 
    	}
    	
        protected Double doInBackground(String... urls) {
            String url = HEADLINES_URL + ESPN_KEY;
            HttpClient client = new DefaultHttpClient(); 
            HttpPost post = new HttpPost(url);
            
            HttpResponse response; 
            try {
            	response = client.execute(post);
            	
            	HttpEntity entity = response.getEntity(); 
            	String resultString = EntityUtils.toString(entity);
            	JSONObject responseObject = new JSONObject(resultString);
            	JSONArray headlinesArray = responseObject.getJSONArray("headlines");
      
            	headlines.clear();
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
            		headlines.add(headline);

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
            ListView espnListView = (ListView) findViewById(R.id.ESPNList);
            HeadlineAdapter adapter = new HeadlineAdapter(mActivity, headlines);
            espnListView.setAdapter(adapter);
            
            if (!mRefresh) {
	            final Handler refreshHandler = new Handler(); 
	            final int delay = 30000; 
	            refreshHandler.postDelayed(new Runnable(){
	            	public void run(){
	                    ESPNFeedASYNC task = new ESPNFeedASYNC(mRefresh); 
	                    task.execute();
	            	}
	            }, delay); 
            }
        }
    }
      
}
