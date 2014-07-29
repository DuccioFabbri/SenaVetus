package it.duccius.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class _WebviewActivity extends Activity implements LocationListener{

	private LocationManager locationManager;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);		
		
		Intent intent = getIntent();	
		String urlPoint = intent.getExtras().getString("urlPoint");
		
		 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 
		 Criteria criteria = new Criteria();
		 criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    provider = locationManager.getBestProvider(criteria, true);
	    
	    locationManager.requestLocationUpdates(provider, 60L,
	            500.0f, this);
	    Location location = locationManager.getLastKnownLocation(provider);
	    if (location != null) {
	        double latitude = location.getLatitude();
	        double longitude = location.getLongitude();
	   
	        locationManager.requestLocationUpdates(                
	        		provider,0,0,(LocationListener) this);
	    } else {
	      
	    }
	    
	    //--------------------------------------------------------------------
		 
		 WebView mywebview = (WebView) findViewById(R.id.webView1);
		    	
		 mywebview = (WebView) findViewById(R.id.webView1);
	        // Brower niceties -- pinch / zoom, follow links in place
		 mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		 mywebview.getSettings().setBuiltInZoomControls(true);
		 mywebview.setWebViewClient(new GeoWebViewClient());
	        // Below required for geolocation
		 mywebview.getSettings().setJavaScriptEnabled(true);		
		 mywebview.getSettings().setGeolocationEnabled(true);
		 mywebview.setWebChromeClient(new GeoWebChromeClient());
	        // Load google.com
		 String url = "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ll=43.328999,11.32183&spn=0.002191,0.00408";
		 mywebview.loadUrl(urlPoint);
		 
//		    WebSettings webSettings = mywebview.getSettings();
//		    webSettings.setJavaScriptEnabled(true);
//		    mywebview.getSettings().setAppCacheEnabled(true);
//		    mywebview.getSettings().setDomStorageEnabled(true);
//		    mywebview.getSettings().setBuiltInZoomControls(true);
//		    mywebview.getSettings().setSupportZoom(true);
//		    //mywebview.setWebViewClient(new WebViewClient());
//		    mywebview.getSettings().setAppCacheEnabled(true);
//		    mywebview.getSettings().setDatabaseEnabled(true);
//		    mywebview.getSettings().setDomStorageEnabled(true);
//		    
//		    mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//		    mywebview.getSettings().setGeolocationEnabled(true);
//		   	
//		    mywebview.setWebViewClient(new GeoWebViewClient());
//		    MyChromeWebViewClient mCC = new MyChromeWebViewClient();
//		    mywebview.setWebChromeClient(mCC);
//		    
//		    String url = "https://www.google.com/maps/ms?msid=206212653941099478857.0004e6e21f640cfa6a5fa&msa=0&ll=43.328999,11.32183&spn=0.002191,0.00408";
//		    mywebview.loadUrl(url);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.webview, menu);
		return true;
	}
	 /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	  @Override
	  public void onLocationChanged(Location location) {
	    int lat = (int) (location.getLatitude());
	    int lng = (int) (location.getLongitude());
//	    latituteField.setText(String.valueOf(lat));
//	    longitudeField.setText(String.valueOf(lng));
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
	  }
	 public class GeoWebViewClient extends WebViewClient {

		    @Override
		    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
		        handler.proceed();
		    }

		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        // When user clicks a hyperlink, load in the existing WebView
		        view.loadUrl(url);
		        return true;
		    }
		}
	 //http://turbomanage.wordpress.com/2012/04/23/how-to-enable-geolocation-in-a-webview-android/
	 /**
	     * WebChromeClient subclass handles UI-related calls
	     * Note: think chrome as in decoration, not the Chrome browser
	     */
	    public class GeoWebChromeClient extends WebChromeClient {
	        @Override
	        public void onGeolocationPermissionsShowPrompt(String origin,
	                GeolocationPermissions.Callback callback) {
	            // Always grant permission since the app itself requires location
	            // permission and the user has therefore already granted it
	            callback.invoke(origin, true, false);
	        }
	    }
	 private class MyChromeWebViewClient extends WebChromeClient {
		    
		    @Override
		    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
		        callback.invoke(origin, true, false);
		    }
		}
}

