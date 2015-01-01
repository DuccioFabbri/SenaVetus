package it.duccius.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import it.duccius.musicplayer.R;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class _Download extends Activity {
	public final static String EXTRA_MESSAGE = "com.androidhive.musicplayer.MESSAGE";

	String Download_path = "http://2.227.2.94:8080/audio/vienna01.mp3";
	String Like_path = "http://2.227.2.94:8080/SenaVetus/Contatti?";	
	
	String Download_ID = "DOWNLOAD_ID";
	
	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_old);		       
 
		 preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
	     downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
       
	}
	 public void download(View arg0) {
		 
		    // TODO Auto-generated method stub
		    Uri Download_Uri = Uri.parse(Download_path);
		    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
		    request.setDestinationInExternalPublicDir("duccius","vienna01.mp3");
		    long download_id = downloadManager.enqueue(request);
		      
		    //Save the download id
		    Editor PrefEdit = preferenceManager.edit();
		    PrefEdit.putLong(Download_ID, download_id);
		    PrefEdit.commit();
		   }
	 public void like(View arg0) {
		 
		    // TODO Auto-generated method stub
		 String android_id = Secure.getString(getBaseContext().getContentResolver(),
                 Secure.ANDROID_ID); 
		 	Like_path += "id_device="+android_id;
		 	GetXMLTask task = new GetXMLTask();
	        task.execute(new String[] { Like_path });

		   }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	 protected void onResume() {
	  // TODO Auto-generated method stub
	  super.onResume();
	    
	  IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	  registerReceiver(downloadReceiver, intentFilter);
	 }	
	@Override
	 protected void onPause() {
	  // TODO Auto-generated method stub
	  super.onPause();
	    
	  unregisterReceiver(downloadReceiver);
	 }
	//httpsunil-android.blogspot.it201301pass-data-from-service-to-activity.html
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		  
		  @Override
		  public void onReceive(Context arg0, Intent arg1) {
		   // TODO Auto-generated method stub
		   DownloadManager.Query query = new DownloadManager.Query();
		   query.setFilterById(preferenceManager.getLong(Download_ID, 0));
		   Cursor cursor = downloadManager.query(query);
		     
		   if(cursor.moveToFirst()){
		    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
		    int status = cursor.getInt(columnIndex);
		    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
		    int reason = cursor.getInt(columnReason);
		      
		    if(status == DownloadManager.STATUS_SUCCESSFUL){
		     //Retrieve the saved download id
		     long downloadID = preferenceManager.getLong(Download_ID, 0);
		       
		     ParcelFileDescriptor file;
		     try {
		      file = downloadManager.openDownloadedFile(downloadID);
		      String uriString = cursor
                      .getString(cursor
                              .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
		      Toast.makeText(_Download.this,
		        "File Downloaded"  + file.toString()+ uriString +uriString,
		        Toast.LENGTH_LONG).show();		      		      		      
		      
		     } catch (FileNotFoundException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		      Toast.makeText(_Download.this,
		        e.toString(),
		        Toast.LENGTH_LONG).show();
		     }
		       
		    }else if(status == DownloadManager.STATUS_FAILED){
		     Toast.makeText(_Download.this,
		       "FAILED!\n" + "reason of"  + reason,
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_PAUSED){
		     Toast.makeText(_Download.this,
		       "PAUSED!\n" + "reason of"  + reason,
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_PENDING){
		     Toast.makeText(_Download.this,
		       "PENDING!",
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_RUNNING){
		     Toast.makeText(_Download.this,
		       "RUNNING!",
		       Toast.LENGTH_LONG).show();
		    }
		   }
		  }		   
		 };	 
		 
		 public void play(View view) {
			    Intent intent = new Intent(this, _AudioPlayerActivity.class);			   			   			   
			    startActivity(intent);
			}

		 
		    private class GetXMLTask extends AsyncTask<String, Void, String> {
		        @Override
		        protected String doInBackground(String... urls) {
		            String output = null;
		            for (String url : urls) {
		                output = getOutputFromUrl(url);
		            }
		            return output;
		        }
		 
		        private String getOutputFromUrl(String url) {
		            String output = null;
		            try {
		                DefaultHttpClient httpClient = new DefaultHttpClient();
		                HttpGet httpGet = new HttpGet(url);
		                HttpResponse httpResponse = httpClient.execute(httpGet);
		               
		            } catch (UnsupportedEncodingException e) {
		                e.printStackTrace();
		            } catch (ClientProtocolException e) {
		                e.printStackTrace();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		            return "";
		        }
		        
		    }
}
