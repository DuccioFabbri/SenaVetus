package it.duccius.musicplayer;

//import it.duccius.musicplayer.RetriveAsyncFile;
import it.duccius.download.Download;

import it.duccius.download.DownloadAudio;
import it.duccius.download.DownloadFile;
import it.duccius.download.RowItem;
import it.duccius.download._DownloadSelection;
//import it.duccius.download.Download.GetXMLTask;
import it.duccius.musicplayer.R;
import it.duccius.musicplayer.R.drawable;
import it.duccius.musicplayer.R.id;
import it.duccius.musicplayer.R.layout;
import it.duccius.musicplayer.Utilities.MyCallbackInterface;

import it.duccius.maps.MapService;
import it.duccius.maps.NavigationDataSet;
import it.duccius.maps.Placemark;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MapNavigation extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener, LocationListener  {

	ProgressDialog progressDialog;
	
	GoogleMap mMap;
	
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private ImageButton btnMap;	
	private ImageButton btnPOIplay;
	private ImageButton btnPOIinfo;
	private ImageButton btnPOIdownload;
	
	private ImageView songThumbnail;
		
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<AudioGuide> _playList = new ArrayList<AudioGuide>();
	// _guides: elenco delle audioguide dispoibili sul server pronte dda scaricare
	private ArrayList<AudioGuide> _guides = new ArrayList<AudioGuide>();
	
	private String _language = "ITA";
	private TextView textLanguage;			
	public static boolean checkConn = false;		
	
	public String _urlDownloads = Utilities.getUrlDownloads();
	public String _filePath = Utilities.getTempSDFld();
	public String _downloadsFileName = "downloads.xml";
	public String _downloadsSDPath = Utilities.getDownloadsSDPath();

	public String _clickedMarker ;
	public int _clickedMarkerIndex;
	
	public int _timeoutSec = 5;
	
	ArrayList<AudioGuide> _localAudioGuideListLang = new ArrayList<AudioGuide>();
	ArrayList<AudioGuide> _audioGuideListLang = new ArrayList<AudioGuide>();
//	ArrayList<AudioGuide> _audioToDownloadLang = new ArrayList<AudioGuide>();
	AudioGuideList _audioToDownloadLang = new AudioGuideList();
	
	NavigationDataSet _nDs = new NavigationDataSet();
	String _currentPOIcoords = "";
	
	LocationManager _locationManager;
	Location _location;
	private String provider;
	
	public boolean downloadAudioGuideList ()
	{			
		try {
			ArrayList<String> arL = new ArrayList<String>();
			
			arL.add(_urlDownloads);
			
			starDownload(arL,_filePath,new MyCallbackInterface() {

	            @Override
	            public void onDownloadFinished(List<RowItem> rowItems) {
	                // Do something when download finished
	            	checkNewAudio();
	            }
	        });
			return true;
			
		} catch (Exception e) {
			Log.d("downloadMapItemes()", e.getMessage());
			return false;
		} 
	}
	public void checkNewAudio()
	{
		File f = new File(_downloadsSDPath);
		if(f.exists()) {  			
			_nDs = MapService.getNavigationDataSet("file://"+_downloadsSDPath);
			_nDs.sort();
		}
		// Aggiorna:
		// - _localAudioGuideListLang:	elenco di audioguide presenti nella scheda SD per una determinata lingua
		// - _audioGuideListLang:		elenco di audioguide disponibili sul server per una determinata lingua
		// - _audioToDownloadLang:		elenco di audioguide presenti sul server ma non presenti su SD per una determinata lingua
		checkForUpdates();	
		initializeMap();
		setupMediaPlayer();
	}
	public void setupMediaPlayer()
	{
		_playList = _localAudioGuideListLang;
		checkEmptyAGList();
			
		textLanguage.setText(_language);
				
		// Mediaplayer
		mp = new MediaPlayer();		
		utils = new Utilities();
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important				
		
		try
		{
			//int i=Integer.parseInt(id_audioSD);
			playSong(currentSongIndex);
		}
		catch(Exception e)
		{Log.d("playSong",e.getMessage());}
					
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.player);
		setContentView(R.layout.sena);
		
		getViewElwments();		
						
		SharedPreferences settings = getSharedPreferences("SenaVetus", 0);  		
		
	    songManager = new SongsManager(_language);		
	  
		//###############################
	    getCurrentLocation();
	    
		// Recupero downloads.xml
		if (!getAudioGuideList())
		{			
			return;
		}
		
//		// Aggiorna:
//		// - _localAudioGuideListLang:	elenco di audioguide presenti nella scheda SD per una determinata lingua
//		// - _audioGuideListLang:		elenco di audioguide disponibili sul server per una determinata lingua
//		// - _audioToDownloadLang:		elenco di audioguide presenti sul server ma non presenti su SD per una determinata lingua
//		checkForUpdates();
			
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hidePOIbtns();
				// check for already playing
				if(_playList != null && _playList.size()>0){
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				}				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hidePOIbtns();
				if(_playList != null && _playList.size()>0){
					// get current song position				
					int currentPosition = mp.getCurrentPosition();
					// check if seekForward time is lesser than song duration
					if(currentPosition + seekForwardTime <= mp.getDuration()){
						// forward song
						mp.seekTo(currentPosition + seekForwardTime);
					}else{
						// forward to end position
						mp.seekTo(mp.getDuration());
					}
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hidePOIbtns();
				if(_playList != null && _playList.size()>0){
					// get current song position				
					int currentPosition = mp.getCurrentPosition();
					// check if seekBackward time is greater than 0 sec
					if(currentPosition - seekBackwardTime >= 0){
						// forward song
						mp.seekTo(currentPosition - seekBackwardTime);
					}else{
						// backward to starting position
						mp.seekTo(0);
					}
				}
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hidePOIbtns();
				if(_playList != null && _playList.size()>0){	
					// check if next song is there or not
					if(currentSongIndex < (_playList.size() - 1)){
						playSong(currentSongIndex + 1);
						currentSongIndex = currentSongIndex + 1;
					}else{
						// play first song
						playSong(0);
						currentSongIndex = 0;
					}
				}
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hidePOIbtns();
				if(_playList != null && _playList.size()>0)
				{
					if(currentSongIndex > 0){
						playSong(currentSongIndex - 1);
						currentSongIndex = currentSongIndex - 1;
					}else{
						// play last song
						playSong(_playList.size() - 1);
						currentSongIndex = _playList.size() - 1;
					}
				}
				
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * Per semplificare l'interfaccia ho tolto la possibilit� di scegliere i brani dalla playlist
		 * */
		btnPOIinfo.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), DownloadAudio.class);
				
				//startActivity(i);
//				Bundle b = new Bundle();
//		        //b.putSerializable("_audioToDownloadLang", _audioToDownloadLang);		       		        
//		        b.putString("language", _language);		 
		        // Add the bundle to the intent.
		        //i.putExtra("language", _language);
		        //i.putExtra("_audioToDownloadLang", _audioToDownloadLang);
		        Bundle b = new Bundle();
		        b.putSerializable("_playList", _playList);
		        b.putSerializable("_audioToDownloadLang", _audioToDownloadLang.getAudioGuides());		        
		        b.putString("language", _language);		 
		        // Add the bundle to the intent.
		        i.putExtras(b);
				startActivityForResult(i, 1);	
				//finish();
			}
		});
		
	}
	

	
	// Provo a scaricare una nuova versione del file,
	// se non ci riesco allora cerco di usare una versione gi� presente in locale
	// se non ho neanche questa opzione restituisco false.
	private boolean getAudioGuideList() {
		boolean downloadOk = downloadAudioGuideList();
		if (!downloadOk)
		{			
			File picFolder = new File(_downloadsSDPath);
			if (!picFolder.exists())
			{
				Toast.makeText(getApplicationContext(), "Impossibile connettersi al server. Verificare che si abbia accesso alla rete, chiudere l'applicazione e riprovare pi� tardi.", Toast.LENGTH_LONG).show();
				
				return false;
			}
		}		
		return true;
	}
//######################################################################
	
	private void initializeMap() {
		//Location currentLocation = getCurrentLocation();
		Location currentLocation = _location;
		//LatLng from = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());		 
		LatLng from = new LatLng(11.325371,43.327671);
		LatLng to = from;
		if(!_nDs.getPlacemarks().isEmpty())
		{
			if (currentLocation != null)
				from = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
			else			
				from = new LatLng(_nDs.getPlacemarks().get(0).getLongitude(),_nDs.getPlacemarks().get(0).getLatitude());
			
			to = new LatLng(_nDs.getPlacemarks().get(0).getLongitude(),_nDs.getPlacemarks().get(0).getLatitude());
		}
		
		setUpMapIfNeeded(from, to);
		
		mMap.setOnCameraChangeListener(getCameraChangeListener());
		mMap.setOnMarkerClickListener(getMarkerClickListener());		
	}
	
	public OnCameraChangeListener getCameraChangeListener()
	{
	    return new OnCameraChangeListener() 
	    {
	        @Override
	        public void onCameraChange(CameraPosition position) 
	        {
	            addItemsToMap(MapNavigation.this._nDs);
	        }
	    };
	}
	//Vedi anche:
		// http://stackoverflow.com/questions/14123243/google-maps-api-v2-custom-infowindow-like-in-original-android-google-maps
		
		//Note that the type "Items" will be whatever type of object you're adding markers for so you'll
		//likely want to create a List of whatever type of items you're trying to add to the map and edit this appropriately
		//Your "Item" class will need at least a unique id, latitude and longitude.
	private HashMap<String, Marker> courseMarkers = new HashMap<String, Marker>();
	
	private Marker _activeMarker;
	private Marker _previousMarker;
	
		private void addItemsToMap(NavigationDataSet items)
		{
		    if(this.mMap != null)
		    {
		        //This is the current user-viewable region of the map
		        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
		 
		        //Loop through all the items that are available to be placed on the map
		        for(Placemark item : items.getPlacemarks()) 
		        {
		 
		            //If the item is within the the bounds of the screen
		            if(bounds.contains(new LatLng(item.getLongitude(),item.getLatitude())))
		            {
		                //If the item isn't already being displayed
		                if(!courseMarkers.containsKey(item.getTitle()))
		                {
		                    //Add the Marker to the Map and keep track of it with the HashMap
		                    //getMarkerForItem just returns a MarkerOptions object
		                	Marker m = this.mMap.addMarker(getMarkerForItem(item));
		                    this.courseMarkers.put(item.getTitle(), m);
		                }
		            }
		 
		            //If the marker is off screen
		            else
		            {
		                //If the course was previously on screen
		                if(courseMarkers.containsKey(item.getTitle()))
		                {
		                    //1. Remove the Marker from the GoogleMap
		                    courseMarkers.get(item.getTitle()).remove();
		                 
		                    //2. Remove the reference to the Marker from the HashMap
		                    courseMarkers.remove(item.getTitle());
		                }
		            }
		        }
		    }
		}
		private MarkerOptions getMarkerForItem(Placemark item) {
			  LatLng MarkerPos = new LatLng ( item.getLongitude(),item.getLatitude());

			  MarkerOptions mo = new MarkerOptions()

		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
		        .position(MarkerPos)
		        .title(item.getTitle());
	         // .snippet("Population: 4,137,400");
			  	//mo.snippet("XXXXXX");
			return mo;
			
		}
		private void checkReadyToPlay() {
			ArrayList<String> alString = getAdapterSource(_localAudioGuideListLang); 
//			int visibility =4;
			_clickedMarkerIndex = alString.indexOf(_clickedMarker);
			if (_clickedMarkerIndex>-1)
			{
				if (_previousMarker != null && _activeMarker != null &&_previousMarker.getTitle().equals(_activeMarker.getTitle()))
				{
					if(_playList != null && _playList.size()>0 )
					{					
						_activeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						_activeMarker.setSnippet("" );
						playSong(_clickedMarkerIndex);
					}							
				}
				else
				{
					_activeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					_activeMarker.setSnippet("Click and Play" );
				}					
			}				
		}
		private void checkReadyToDownload() {
			ArrayList<String> alString = getAdapterSource(_audioToDownloadLang); 
			int clickedMarkerIndex = alString.indexOf(_clickedMarker);
			if (clickedMarkerIndex>-1)
			{
				if (_previousMarker != null && _activeMarker != null &&_previousMarker.getTitle().equals(_activeMarker.getTitle()))
				{
					if(_audioToDownloadLang != null && _audioToDownloadLang.getAudioGuides().size()>0 )
					{					
						_activeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						//playSong(_clickedMarkerIndex);
						
						ArrayList<String> arL = new ArrayList<String>();
						AudioGuide ag = _audioToDownloadLang.getFromPosition(clickedMarkerIndex);
						String str = ag.getPath();
						arL.add(str);
						//downloadAudioGuide (arL);
						
						starDownload(arL,new MyCallbackInterface() {

				            @Override
				            public void onDownloadFinished(List<RowItem> rowItems) {
				                // Do something when download finished
				            	if (mp != null)
						    		mp.release();
				            	checkNewAudio();
				            }
				        });
					}							
				}
				else
				{
					_activeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));	
					_activeMarker.setSnippet("Click and Download" );
				}					
			}				
		}
		private void starDownload(ArrayList<String> arL, MyCallbackInterface callback) {
			ProgressDialog progressDialog = new ProgressDialog((this));
			progressDialog.setTitle("In progress...");
			progressDialog.setMessage("Loading...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setIndeterminate(false);
			progressDialog.setMax(100);
			progressDialog.setIcon(R.drawable.arrow_stop_down);
			progressDialog.setCancelable(true);
			progressDialog.show();
			DownloadFile df = new DownloadFile(this,_language,progressDialog, callback);
			df.execute(arL);
		}
		private void starDownload(ArrayList<String> arL, String destPath,MyCallbackInterface callback) {
			ProgressDialog progressDialog = new ProgressDialog((this));
			progressDialog.setTitle("In progress...");
			progressDialog.setMessage("Loading...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setIndeterminate(false);
			progressDialog.setMax(100);
			progressDialog.setIcon(R.drawable.arrow_stop_down);
			progressDialog.setCancelable(true);
			progressDialog.show();
			DownloadFile df = new DownloadFile(this,_language,destPath,progressDialog,callback);
			df.execute(arL);
		}		
	public OnMarkerClickListener getMarkerClickListener()
	{
	    return new OnMarkerClickListener() 
	    {	       
			@Override
			public boolean onMarkerClick(Marker marker) {
				//
				if(_activeMarker != null)
					_activeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				//----------
				_previousMarker = _activeMarker;				
				_activeMarker = marker;
				//----------
				
				//TODO Auto-generated method stub
				_clickedMarker = marker.getTitle();
				
				checkReadyToPlay();
				
				checkReadyToDownload();
								
				return false;
			}
			
			
	    };
	    
	}

	
	private ArrayList<String> getAdapterSource(ArrayList<AudioGuide> sourceList) {
		//_sdAudios = getSdAudios();	
		SongsManager sm = new SongsManager(_language);				
		
		ArrayList<String> sdAudiosStrings  = sm.getSdAudioStrings(sourceList);
		return sdAudiosStrings;
	}
	private void setUpMapIfNeeded(LatLng from, LatLng to) {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	//https://developers.google.com/maps/documentation/android/views
	        	// Move the camera instantly to Sydney with a zoom of 15.
//	        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
//
//	        	// Zoom in, animating the camera.
//	        	mMap.animateCamera(CameraUpdateFactory.zoomIn());
//
//	        	// Zoom out to zoom level 10, animating with a duration of 2 seconds.
//	        	mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

	        	// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
	        	CameraPosition cameraPosition = new CameraPosition.Builder()
	        	    .target(from)      // Sets the center of the map to Mountain View
	        	    .zoom(8)                   // Sets the zoom
	        	    .bearing(90)                // Sets the orientation of the camera to east
	        	    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	        	    .build();                   // Creates a CameraPosition from the builder
	        	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        	
	        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(to, 19));
//	        	Marker melbourne = mMap.addMarker(new MarkerOptions()
//                .position(to)
//                .title("Melbourne")
//                .snippet("Population: 4,137,400"));
	        	
	        	mMap.setMyLocationEnabled(true);
	        	mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        }
	    }
	}

	//#################################################################################

	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	   if (requestCode == 1)
	   {		  	
		    try
		    {		   if(resultCode == RESULT_OK) 	{
			//		    	_language = intent.getExtras().getString("language");
			//		    	// intent.getExtras().getString("id_audioSD");
			//		    	_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("selectedItems");
					    	if (mp != null)
					    		mp.release();
					    	checkNewAudio();
		    			}
		    }
		    catch(Exception e)
		    {
		    	Log.d("zzzz", e.toString());
		    }
	   }
	}
	private void setupAudioThumbnail(String imgName) {
//		imgName = imgName.substring(4);
			Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(ApplicationData.getPicFolder()+"/"+imgName+".jpg").toString());		
			songThumbnail.setImageBitmap(bmp);		
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkForUpdates() {
		boolean res = false;
		// Prima recupero tutti gli elementi del file downloads.xml e li etto in _guides
		if(songManager.loadGuideList(_guides))
		{
			// Creo una copia di _guides con i soli elementi che si accordano per lingua
			_audioGuideListLang = songManager.guideListByLang(_guides);
			
			// Creo una nuova lista con gli audio presenti nella SD, ricavando il Title dal file downloads.xml
			_localAudioGuideListLang = songManager.getSdAudioList(_audioGuideListLang);
			Collections.sort(_localAudioGuideListLang);
			
			//boolean result = songManager.getAudioToDownload(_localAudioGuideListLang, _audioGuideListLang);
			//_audioToDownloadLang = _audioGuideListLang;
			AudioGuideList localAudioGuideListLang = new AudioGuideList();
			localAudioGuideListLang.setAudioGuides(_localAudioGuideListLang);
			AudioGuideList audioGuideListLang = new AudioGuideList();
			audioGuideListLang.setAudioGuides(_audioGuideListLang);
			
			_audioToDownloadLang = songManager.getAudioToDownload(localAudioGuideListLang,audioGuideListLang );
			
			
			Collections.sort(_audioToDownloadLang);
			
			if (!_audioToDownloadLang.getAudioGuides().isEmpty())
			{
				btnPOIinfo.setVisibility(View.VISIBLE);
				Toast.makeText(getApplicationContext(), "Sono disponibili nuove audiogide\n. Accedi alla sezione 'Aggiornamenti' e clicca su 'Download'.", Toast.LENGTH_SHORT).show();
			}
			else
				btnPOIinfo.setVisibility(View.INVISIBLE);
			res = true;
		}
		return res;
	}

	private ArrayList<AudioGuide> refreshPlayList() {
		ArrayList<AudioGuide> list = new ArrayList<AudioGuide>();
		if ( _playList != null && _playList.isEmpty())
		{
			list = songManager.getSdAudioList(_audioGuideListLang);
		}	
		else
		{
			list = _playList;
		}
		return list;
	}
	private void checkEmptyAGList() {
		if ( _playList != null && _playList.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Non � dosponibile nessuna guida audio.\n. Scaricane di nuove dalla sezione 'Aggiornamenti' dal pulsante in alto a destra.", Toast.LENGTH_LONG).show();
		}
	}


	private void getViewElwments() {
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		songThumbnail = (ImageView) findViewById(R.id.thumbnail);	
		btnMap = (ImageButton) findViewById(R.id.btnMap);
		btnPOIdownload  = (ImageButton) findViewById(R.id.btnPOIdownload);		
		btnPOIplay  = (ImageButton) findViewById(R.id.btnPOIplay);
		btnPOIinfo  = (ImageButton) findViewById(R.id.btnPOIinfo);
		
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		textLanguage = (TextView) findViewById(R.id.textLanguage);
	}
	private String getDestSDFld() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getAppName()+"/"+_language;
		return sourcePath;
	}
	private String getAudioName(String url)
	 {
		 String title = "";
		 String [] tokens = url.split("/");
		 title = tokens[tokens.length-1];
		 return title;
	 }
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
			hidePOIbtns();
			//String audioPath = songManager.getLangMediaPath()+File.separator+_playList.get(songIndex).getTitle()+".mp3";
			AudioGuide ag = (AudioGuide) _playList.get(songIndex);
			//String audioPath = ag.getPath();
			String audioPath =getDestSDFld() +File.separator+ getAudioName(ag.getPath());
        	mp.reset();
			mp.setDataSource(audioPath);
			mp.prepare();
			mp.start();
			// Displaying Song title
			//String songTitle = _playList.get(songIndex).getName();
        	
        	
        	// Dovrebbe mostrare delle foto del POI in ascolto
        	// Per il momento trascurao
//        	setupAudioThumbnail(songTitle);
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);//
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();	
			
			AudioGuideList aglLocalAudioGuideList = new AudioGuideList();
			aglLocalAudioGuideList.setAudioGuides(_audioGuideListLang);
			AudioGuide currentAudioGuide = aglLocalAudioGuideList.getFromPosition(songIndex);
			songTitleLabel.setText(currentAudioGuide.getTitle());
			
			LatLng poiLatLong = new LatLng(Double.parseDouble(currentAudioGuide.getLng()),Double.parseDouble(currentAudioGuide.getLat()));
			
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(poiLatLong, 19));
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((_playList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (_playList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
		
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	    // http://stackoverflow.com/questions/13854196/application-force-closed-when-exited-android
	    mHandler.removeCallbacks(mUpdateTimeTask);
	 }

	private void hidePOIbtns() {
		btnPOIplay.setVisibility(4);
		btnPOIdownload.setVisibility(4);
	}
//	public class RetriveAsyncFile extends AsyncTask<Void, Void, Boolean> {
//
//		private Activity context;
//	    public Exception exception;
//	    private int _timeoutSec=5;
//	    private String _filePath;
//	    private String _nomeFile;
//	    private String _url;
//	   	    	
//	    public RetriveAsyncFile (Activity context, String url, String filePath, String nomeFile,
//				int timeoutSec)
//	    {
//	    	 this.context = context;
//	    	 
//	    	_url = url;
//	    	_filePath = filePath;
//	    	_nomeFile = nomeFile;
//	    	_timeoutSec = timeoutSec;  	    		    		    	 
//	    }
//	    
//	    protected Boolean doInBackground(Void...params) {
//	    	 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//	              getClass().getName());
//	         wl.acquire();	         
//	    	try {
//	            URL url= new URL(_url);
//	            final URLConnection conn = url.openConnection();
//				conn.setConnectTimeout(_timeoutSec * 1000);
//				conn.setReadTimeout(_timeoutSec * 1000);
//				conn.connect();
//				
//				Utilities.StreamToFile(url.openStream(), _filePath, _nomeFile);
//				 
////	             publishProgress((int) ((i / (float) count) * 100));
//	            return true;
//	            
//	        } catch (Exception e) {
//	            this.exception = e;  
//	            Log.d("RetriveAsyncFile", e.toString());
//	            return false;
//	        }
//	    	finally {
//	            wl.release();
//	        }
//	    }
//	    protected void onProgressUpdate(Integer... progress) {
//	        progressDialog.setProgress(progress[0]);        
//	   }
//	   
//	    @Override
//	    protected void onPostExecute(Boolean result) {
//
//	    	progressDialog.dismiss();
//	    	checkNewAudio();
//	     
//	    }
//
//	}
	/**
	 * Recupera l'attuale posizione e la assegna a '_location'
	 * Uso 'PASSIVE_PROVIDER' perch� con altre soluzioni ho avuto problemi.
	 * 
	 * http://www.vogella.com/tutorials/AndroidLocationAPI/article.html
	 * http://stackoverflow.com/questions/19621882/getlastknownlocation-returning-null?rq=1
	 */
	private void getCurrentLocation() {
		_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    provider = _locationManager.getBestProvider(criteria, false);
	    _locationManager.requestLocationUpdates(provider, 400, 1, this);
	    _location = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 if(_location == null)
		    {
		    	_location = location;
		    }		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	  @Override
	  protected void onResume() {
	    super.onResume();
	    _locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }
	  
}