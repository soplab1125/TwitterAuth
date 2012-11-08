package net.soplab.android.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;

import android.util.Log;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class Auth extends Activity {


	private TextView txtTitle;
	private WebView webView;

	private int numReturnCode;

	private SharedPreferences pref;

	private Context context;

	private String CALLBACK_URL;
	private String CONSUMER_KEY;
	private String CONSUMER_SECRET;
	private Twitter twitter;
	private RequestToken requestToken;

	private String txtClassName = getClass().getSimpleName();
	private Editor e;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		// 画面初期設定
		webView = (WebView)findViewById(R.id.webView1);
		txtTitle = (TextView)findViewById(R.id.textView1);

		numReturnCode = Activity.RESULT_CANCELED;

		// タイトル設定
		txtTitle.setText("Twitter認証");

		/// ======================================
		///  設定値呼出
		/// ======================================
		context = getApplicationContext();
		pref = getSharedPreferences("preference", Activity.MODE_PRIVATE);  ;
		Resources res = getResources();

		// Consumer Key を取得
		CONSUMER_KEY = res.getString(R.string.TwitterConsumerKey);

		// Consumer Secret を取得
		CONSUMER_SECRET = res.getString(R.string.TwitterConsumerSecret);

		// コールバックURLを取得
		CALLBACK_URL = res.getString(R.string.TwitterCallbackUrl);

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

		/// ======================================
		///  トークン削除
		/// ======================================
		e = pref.edit();

		e.putString("TwitterAccessToken", "");
		e.putString("TwitterAccessSecret", "");
		e.putString("TwitterUserName", "");
		e.commit();

		Toast.makeText(context, getString(R.string.txtDeletedAuthData), Toast.LENGTH_LONG).show();

		try {
			// requestTokenを取得
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
			// 画面遷移時にWebView内で画面遷移するようにする。
			webView.loadUrl(requestToken.getAuthorizationURL());

		} catch (Exception exc) {
			Log.d(txtClassName, exc.getMessage());
		}

		// 画面遷移時にWebView内で画面遷移するようにする。
		webView.setWebViewClient(new WebViewClient(){

			// ページ描画完了時に呼ばれる。
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				// Twitter側でやってくれる処理として認証完了したら、指定したCALLBACK URLにリダイレクト
				// 遷移先のURLがCALLBACK URLから始まっていたら、認証成功とみなす。
				if(url != null && url.startsWith(CALLBACK_URL)){
					try {
						// URLパラメータを分解する。
						String[] urlParameters = url.split("\\?")[1].split("&");
						String oauthVerifier = getUrlParam(urlParameters, "oauth_verifier");

						// oauth_verifierを取り出して、access_tokenとaccess_token_secretを取得する。
						// 【この取得処理はTwitter4jが勝手にやってくれるので "oauth_token" は不要？ 】
						AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
						e.putString("TwitterAccessToken", accessToken.getToken());
						e.putString("TwitterAccessSecret", accessToken.getTokenSecret());
						e.putString("TwitterUserName", accessToken.getScreenName());
						e.commit();


						numReturnCode = Activity.RESULT_OK;

					} catch (Exception exc) {
						Log.d(txtClassName, exc.getMessage());
					}

					// 元のActivityに戻す
					Intent intent = getIntent();
					setResult(numReturnCode, intent);
					finish();

				}
			}
		});
	}

	private String getUrlParam(String[] parameter, String value){
		String retValue = "";
		try{
			// 指定したキーをURLパラメータから切り出す。
			if(parameter[0].startsWith(value)){
				retValue = parameter[0].split("=")[1];
			}else if(parameter[1].startsWith(value)){
				retValue = parameter[1].split("=")[1];
			}

		} catch (Exception exc) {
			Log.d(txtClassName, exc.getMessage());
		}

		return retValue;
	}
}
