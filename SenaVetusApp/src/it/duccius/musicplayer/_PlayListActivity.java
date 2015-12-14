package it.duccius.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.DownloadManager;
import android.app.ListActivity;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class _PlayListActivity extends ListActivity {
	// Songs list
	public ArrayList<_Audio> songsList = new ArrayList<_Audio>();
	private Spinner spnLanguage;
	
	SharedPreferences preferenceManager;
	DownloadManager downloadManager;
	
	ArrayList<_Audio> guides;
	_Audio currentAudio;	
	
	//String Download_path = "http://2.227.2.94:8080/audio/";
	
	//String Like_path = "http://2.227.2.94:8080/SenaVetus/Contatti?";	
	
	String Download_ID = "DOWNLOAD_ID";
	String language = "ITA";
	_AudioAdapter adapter;
	ArrayList<_Audio> _completeAudioList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		
		// Language
		loadGuideList();
		Intent intent = getIntent();		
	    try
	    {
	    	language = intent.getExtras().getString("language");	    	
	    }
	    catch(Exception e)
	    {}
		spnLanguage = (Spinner)findViewById(R.id.spnLanguage);
		ArrayAdapter<String> adapterLang = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		ApplicationData.getLanguages()
        		);
        
        spnLanguage.setAdapter(adapterLang);
        spnLanguage.setSelection(adapterLang.getPosition(language));
       		
		fillAudioList();
		
		
	}
	private void fillAudioList() {
		preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
	     downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);			
	
		ArrayList<_Audio> songsListData = new ArrayList<_Audio>();

		SongsManager plm = new SongsManager(language);		
		songsListData = getSDAudio(language, plm);
		
		ArrayList<_Audio> audioDisponibiliServer= guideList(language);			
		_completeAudioList = addAudioToDownload(songsListData, audioDisponibiliServer);
		
		try{
			//adapter = new AudioAdapter(songsListData, this);	
			adapter = new _AudioAdapter(_completeAudioList, this);
	//		adapter.notifyDataSetChanged();
			setListAdapter(adapter);
		}
		catch(Exception e)
		{System.out.print(e);}
		
	}
	private ArrayList<_Audio> getSDAudio(String lang,
			SongsManager plm) {
		ArrayList<_Audio> songsListData = new ArrayList<_Audio>();
		// get all songs from sdcard
		this.songsList = plm.getPlayList();

		// looping through playlist
		for (int i = 0; i < songsList.size(); i++) {
			// creating new HashMap
			_Audio song = songsList.get(i);
						
			// adding HashList to ArrayList
			songsListData.add(song);
		}
		return songsListData;
	}

	private String getSelectedLang() {
		return spnLanguage.getItemAtPosition(spnLanguage.getSelectedItemPosition()).toString();
	}
	//private void addAudioToUpdate(ArrayList<Audio> songsListData,
	//ArrayList<Audio> audioDisponibiliServer) {
	//for (Audio disponibileSuServer :audioDisponibiliServer) {
	private ArrayList<_Audio> addAudioToDownload(ArrayList<_Audio> songsListData,
			ArrayList<_Audio> audioDisponibiliServer) {
		
		ArrayList<_Audio> newSongsListData = new ArrayList<_Audio>();
		
		for (_Audio disponibileSuServer :audioDisponibiliServer) {
						
			// creating new HashMap
			boolean presente = false;
			
			//for (Audio audioInSD: songsListData )
			for (_Audio audioInSD: songsListData )
			{
				if( audioInSD.get("songTitle").equals(disponibileSuServer.get("songTitle"))){
					presente=true;	
					disponibileSuServer.setToBeDownloaded(false);
					disponibileSuServer.setSongPositionInSd(audioInSD.getSongPositionInSd());
					newSongsListData.add(disponibileSuServer);
					break;}
			}
			if (!presente)
			{
				disponibileSuServer.setToBeDownloaded(true);
				songsListData.add(disponibileSuServer);
				newSongsListData.add(disponibileSuServer);
			}
		}
		return newSongsListData;
	}
	
	public void loadGuideList()
	{
		guides= new ArrayList<_Audio>();
		
		for (int i = 1; i < 5; i++) {
			_Audio song = new _Audio();
			song.put("songTitle", "siena"+i );
			song.put("songPath", "http://2.227.2.94:8080/audio/ITA/siena"+i+".mp3");
			song.setSongLang("ITA");
			song.setPoint(ApplicationData.getPoints().get(i-1));
			// Adding each song to SongList
			guides.add(song);
		}
		for (int i = 1; i < 2; i++) {
			_Audio song = new _Audio();
			song.put("songTitle", "siena"+i );
			song.put("songPath", "http://2.227.2.94:8080/audio/ENG/siena"+i+".mp3");
			song.setSongLang("ENG");
			song.setPoint(ApplicationData.getPoints().get(i-1));
			// Adding each song to SongList
			guides.add(song);
		}
		
	}
	public ArrayList<_Audio> guideList(String lang)
	{
		ArrayList<_Audio> langGuides= new ArrayList<_Audio>();

		for (_Audio audio: guides) {
		    if (audio.getSongLang().contains(lang))
		    	langGuides.add(audio);
		}
		
		return langGuides;
	}
	@Override
	public void onBackPressed( ) {
		Intent in = new Intent(getApplicationContext(),
				_AudioPlayerActivity.class);
		in.putExtra("language", getSelectedLang());
//		setResult(100, in);
		startActivity(in);
//		// Closing PlayListView
		finish();
	}
	
	public void clickOnDownloadAudio(String title)
	{
		
		this.download(title);
		
	}

	public void clickOnSDAudio(String id_audioSD, String language, String point)
	{
		// Starting new intent
		Intent in = new Intent(this,
				_AudioPlayerActivity.class);
		
		// Sending songIndex to PlayerActivity
		in.putExtra("id_audioSD", id_audioSD);
		in.putExtra("language", language);
		in.putExtra("point", point);
		startActivity(in);
		finish();
	}
	private String getIdAudioSd() {
		return ((TextView)this.findViewById(R.id.id_audioSD)).getText().toString();
	}
	 public void download(String title) {
		 
	 	like("audio",getAudioPathToDownload(title));	 		 		 		 	
	 	
	    Uri Download_Uri = Uri.parse(getAudioPathToDownload(title));
	    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
	    request.setDestinationInExternalPublicDir(getDestSDFld(), title+".mp3");
	    
	    long download_id = downloadManager.enqueue(request);
	  
		//Save the download id
	    Editor PrefEdit = preferenceManager.edit();
	    PrefEdit.putLong(Download_ID, download_id);
	    PrefEdit.commit();
	 }
	 private String getDestSDFld() {
			String sourcePath = ApplicationData.getAppName()+"/"+getSelectedLang();
			return sourcePath;
		}
	 private String getAudioPathToDownload(String title) {
		String sourcePath = ApplicationData.getDownloadRemotePath()+getSelectedLang()+"/"+title+".mp3";
		return sourcePath;
	}

	 public void like(String name, String value) {
		 
		    // TODO Auto-generated method stub
		 String android_id = Secure.getString(getBaseContext().getContentResolver(),
              Secure.ANDROID_ID); 
		 	String msg = ApplicationData.getFeedbackRemoteUrl() + "id_device="+android_id + "&"+name+"="+value;
		 	
		 	GetXMLTask task = new GetXMLTask();
	        task.execute(new String[] { msg });

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
	public void updateAudioguides (View view)
	{
//		Intent in = new Intent(this,
//				_DownloadSelection.class);
//		
//		in.putExtra("language", getSelectedLang());
//		
//		finish();		
//		startActivity(in);
		
	}
	public void refresh(View view) {
		  // TODO Auto-generated method stub
		Intent in = new Intent(this,
				_PlayListActivity.class);
		
		in.putExtra("language", getSelectedLang());
		
		finish();		
		startActivity(in);
		
//		fillAudioList();
//		
//		view.requestLayout();
//		view.invalidate();
		
		//adapter.setNotifyOnChange (true);
		 }
	private void receivedBroadcast(Intent i) {
        // Put your receive handling code here
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
		      Toast.makeText(_PlayListActivity.this,
		        "File Downloaded"  + file.toString()+ uriString +uriString,
		        Toast.LENGTH_LONG).show();	
		      
		      //http://stackoverflow.com/questions/3275042/android-best-practice-on-updating-the-ui-from-broadcastreceiver-to-a-certain-act
		      _PlayListActivity.this.receivedBroadcast(arg1);
		      
		     } catch (FileNotFoundException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		      Toast.makeText(_PlayListActivity.this,
		        e.toString(),
		        Toast.LENGTH_LONG).show();
		     }
		       
		    }else if(status == DownloadManager.STATUS_FAILED){
		     Toast.makeText(_PlayListActivity.this,
		       "FAILED!\n" + "reason of"  + reason,
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_PAUSED){
		     Toast.makeText(_PlayListActivity.this,
		       "PAUSED!\n" + "reason of"  + reason,
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_PENDING){
		     Toast.makeText(_PlayListActivity.this,
		       "PENDING!",
		       Toast.LENGTH_LONG).show();
		    }else if(status == DownloadManager.STATUS_RUNNING){
		     Toast.makeText(_PlayListActivity.this,
		       "RUNNING!",
		       Toast.LENGTH_LONG).show();
		    }
		   }
		  }		   
		 };	 
		
		 
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
