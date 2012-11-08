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

		// ��ʏ����ݒ�
		webView = (WebView)findViewById(R.id.webView1);
		txtTitle = (TextView)findViewById(R.id.textView1);

		numReturnCode = Activity.RESULT_CANCELED;

		// �^�C�g���ݒ�
		txtTitle.setText("Twitter�F��");

		/// ======================================
		///  �ݒ�l�ďo
		/// ======================================
		context = getApplicationContext();
		pref = getSharedPreferences("preference", Activity.MODE_PRIVATE);  ;
		Resources res = getResources();

		// Consumer Key ���擾
		CONSUMER_KEY = res.getString(R.string.TwitterConsumerKey);

		// Consumer Secret ���擾
		CONSUMER_SECRET = res.getString(R.string.TwitterConsumerSecret);

		// �R�[���o�b�NURL���擾
		CALLBACK_URL = res.getString(R.string.TwitterCallbackUrl);

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

		/// ======================================
		///  �g�[�N���폜
		/// ======================================
		e = pref.edit();

		e.putString("TwitterAccessToken", "");
		e.putString("TwitterAccessSecret", "");
		e.putString("TwitterUserName", "");
		e.commit();

		Toast.makeText(context, getString(R.string.txtDeletedAuthData), Toast.LENGTH_LONG).show();

		try {
			// requestToken���擾
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
			// ��ʑJ�ڎ���WebView���ŉ�ʑJ�ڂ���悤�ɂ���B
			webView.loadUrl(requestToken.getAuthorizationURL());

		} catch (Exception exc) {
			Log.d(txtClassName, exc.getMessage());
		}

		// ��ʑJ�ڎ���WebView���ŉ�ʑJ�ڂ���悤�ɂ���B
		webView.setWebViewClient(new WebViewClient(){

			// �y�[�W�`�抮�����ɌĂ΂��B
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				// Twitter���ł���Ă���鏈���Ƃ��ĔF�؊���������A�w�肵��CALLBACK URL�Ƀ��_�C���N�g
				// �J�ڐ��URL��CALLBACK URL����n�܂��Ă�����A�F�ؐ����Ƃ݂Ȃ��B
				if(url != null && url.startsWith(CALLBACK_URL)){
					try {
						// URL�p�����[�^�𕪉�����B
						String[] urlParameters = url.split("\\?")[1].split("&");
						String oauthVerifier = getUrlParam(urlParameters, "oauth_verifier");

						// oauth_verifier�����o���āAaccess_token��access_token_secret���擾����B
						// �y���̎擾������Twitter4j������ɂ���Ă����̂� "oauth_token" �͕s�v�H �z
						AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
						e.putString("TwitterAccessToken", accessToken.getToken());
						e.putString("TwitterAccessSecret", accessToken.getTokenSecret());
						e.putString("TwitterUserName", accessToken.getScreenName());
						e.commit();


						numReturnCode = Activity.RESULT_OK;

					} catch (Exception exc) {
						Log.d(txtClassName, exc.getMessage());
					}

					// ����Activity�ɖ߂�
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
			// �w�肵���L�[��URL�p�����[�^����؂�o���B
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
