package net.soplab.android.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {

    private Context context;
	private SharedPreferences pref;
	private Resources res;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = getApplicationContext();
		pref = getSharedPreferences("preference", Activity.MODE_PRIVATE);
		res = getResources();

        Button auth = (Button)findViewById(R.id.auth);
        auth.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		startActivity(new Intent(Main.this, Auth.class));
        	}
        });

        Button tweet = (Button)findViewById(R.id.tweet);
        tweet.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Log.d("TwitterAccessToken", "value");

        		String accessToken = pref.getString("TwitterAccessToken", "");
        		String accessSecret = pref.getString("TwitterAccessSecret", "");


        		AccessToken token = new AccessToken(accessToken, accessSecret);

        		// Consumer Key を取得
        		String CONSUMER_KEY = res.getString(R.string.TwitterConsumerKey);
        		String CONSUMER_SECRET = res.getString(R.string.TwitterConsumerSecret);

        	    Twitter twitter = new TwitterFactory().getInstance();
				try {
					twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					twitter.setOAuthAccessToken(token);
				} catch (Exception c) {
					// TODO 自動生成された catch ブロック
	        	    Toast.makeText(context, "Successfully updated the status to [" + c.getMessage() + "].", Toast.LENGTH_LONG).show();
				}

        	    Status status;
        	    if(twitter != null){
    				try {
    					status = twitter.updateStatus("test");
    	        	    Toast.makeText(context, "Successfully updated the status to [" + status.getText() + "].", Toast.LENGTH_LONG).show();
    				} catch (TwitterException c) {
    					// TODO 自動生成された catch ブロック
    	        	    Toast.makeText(context, "Successfully updated the status to [" + c.getMessage() + "].", Toast.LENGTH_LONG).show();
    				}
        	    }
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
