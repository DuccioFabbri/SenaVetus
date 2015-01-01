package it.duccius.musicplayer;

import it.duccius.maps.MapService;
import it.duccius.maps.NavigationDataSet;
import it.duccius.maps.Placemark;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ShowMap extends Activity implements LocationListener  {

	GoogleMap mMap;
	private Spinner spinner1;
	private ImageButton btnPlay;
	private ImageButton btnDownload;	
	
//	private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
//	private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
//	static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
	
	private HashMap<String, Marker> courseMarkers = new HashMap<String, Marker>();
	NavigationDataSet nDs = new NavigationDataSet();
	
	// http://discgolfsoftware.wordpress.com/2012/12/06/hiding-and-showing-on-screen-markers-with-google-maps-android-api-v2/
	
	boolean markerClicked;
	
	private ArrayList<AudioGuide> _playList = new ArrayList<AudioGuide>();	
	private ArrayList<AudioGuide> _localAudioGuideListLang = new ArrayList<AudioGuide>();
	private ArrayList<AudioGuide> _audioToDownloadLang = new ArrayList<AudioGuide>();	
	
	private String _language = "ITA";
	private int _currentSongIndex =0;
	public String _clickedMarker ;
	private boolean _checkConn;
	private ArrayList<AudioGuide> _guides;
	private SongsManager songManager;
	
	private String getTitleFromName(ArrayList<AudioGuide> guides, String name)
	{
		String res = "";
		
		for(AudioGuide ag: guides)
		{
			if(ag.getName().equals(name))
			{
				res = ag.getTitle();
				break;
			}
		}
		return res;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		  Intent intent = getIntent();	
		  String currentAudioTitle="";
		  
		    try
		    {		    	
		    	SharedPreferences settings = getSharedPreferences("SenaVetus", 0); 
		    	_checkConn = settings.getBoolean("checkConn", false);
		    	_language = intent.getExtras().getString("language");
		    	_currentSongIndex = intent.getIntExtra("currentSongIndex",0);
		    	//_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("selectedItems");
		    	_guides = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("allGuides");
		    	
		    	 songManager = new SongsManager(_language);
		    	//_playList = songManager.getSdAudioList();		    	
		    	 _playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("playList");
		    	 _localAudioGuideListLang = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("localAudioGuideListLang");
		    	 _audioToDownloadLang = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("audioToDownloadLang");
		    	 
		    	currentAudioTitle = getTitleFromName(_guides,_playList.get(_currentSongIndex).getName() );
		    	//currentAudioTitle = _playList.get(_currentSongIndex).getTitle();
		    	
		    }
		    catch(Exception e)
		    {}
		    
		    Location currentLocation = getCurrentLocation();
		
		setContentView(R.layout.activity_show_map);
		try
		{			
			String coords = "";
			
			// Il file .kml ho provatoa a scaricarlo in AudioPlayerActivity.java
			String kmlFile = Utilities.getTempSDFld()+ File.separator+ "SenaVetus.kml";
			String UrlKmlFile = "file://"+kmlFile;
			File f = new File(kmlFile);
			if(f.exists()) {  
				nDs = MapService.getNavigationDataSet(UrlKmlFile);
				coords = nDs.getCoordFromTitle(currentAudioTitle);
			}
			// http://discgolfsoftware.wordpress.com/2012/12/06/hiding-and-showing-on-screen-markers-with-google-maps-android-api-v2/
					
			//mMap.setOnMarkerClickListener(this);
			LatLng from = null;
			LatLng to = null;
			
			to = new LatLng(Double.valueOf(coords.split(",")[1]),Double.valueOf(coords.split(",")[0]));
			if (currentLocation!=null)
			{
				from = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());			
			}else
				from = to;
			
			setUpMapIfNeeded(from, to);	
			
			mMap.setOnCameraChangeListener(getCameraChangeListener());
			mMap.setOnMarkerClickListener(getMarkerClickListener());
			
		}	
			catch (Exception e)
			{}
		}
	
	private ArrayList<String> getAdapterSource(ArrayList<AudioGuide> sourceList) {
		//_sdAudios = getSdAudios();	
		SongsManager sm = new SongsManager(_language);				
		
		ArrayList<String> sdAudiosStrings  = sm.getSdAudioStrings(sourceList);
		return sdAudiosStrings;
	}
	private Location getCurrentLocation() {
		// Getting LocationManager object from System Service LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);
		return location;
	}
	

	public OnMarkerClickListener getMarkerClickListener()
	{
	    return new OnMarkerClickListener() 
	    {	       
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				_clickedMarker = marker.getTitle();
				checkReadyToPlay();
				
				checkReadyToDownload();
								
				return false;
			}

			private void checkReadyToPlay() {
				ArrayList<String> alString = getAdapterSource(_localAudioGuideListLang); 
				int visibility =4;
				
				for (String tit: alString)
				{
					if (tit.equals(_clickedMarker))
					{
						visibility = 0;
						break;
					}
				}
				
				btnPlay = (ImageButton) findViewById(R.id.btnPlay);
				btnPlay.setVisibility(visibility);
			}
			private void checkReadyToDownload() {
				ArrayList<String> alString = getAdapterSource(_audioToDownloadLang); 
				int visibility =4;
				
				for (String tit: alString)
				{
					if (tit.equals(_clickedMarker))
					{
						visibility = 0;
						break;
					}
				}
				btnDownload = (ImageButton) findViewById(R.id.btnDownload);
				btnDownload.setVisibility(visibility);
			}
	    };
	}
	
	public OnCameraChangeListener getCameraChangeListener()
	{
	    return new OnCameraChangeListener() 
	    {
	        @Override
	        public void onCameraChange(CameraPosition position) 
	        {
	            addItemsToMap(ShowMap.this.nDs);
	        }
	    };
	}
	//Vedi anche:
	// http://stackoverflow.com/questions/14123243/google-maps-api-v2-custom-infowindow-like-in-original-android-google-maps
	
	//Note that the type "Items" will be whatever type of object you're adding markers for so you'll
	//likely want to create a List of whatever type of items you're trying to add to the map and edit this appropriately
	//Your "Item" class will need at least a unique id, latitude and longitude.
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
		return mo;
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_map, menu);
		return true;
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
	        	    .zoom(19)                   // Sets the zoom
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
	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(getApplicationContext(), "Location Changed", Toast.LENGTH_SHORT).show();
	}
	public void onBackPressed( ) {
		Intent in = new Intent(getApplicationContext(),
				_AudioPlayerActivity.class);
		in.putExtra("language", _language);
		setResult(200, in);
//		startActivity(in);
//		// Closing PlayListView
		finish();
	}
	

}
