package it.duccius.musicplayer;


import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


public class PlayListAudio extends Activity implements
OnClickListener{
	// Songs list
	    Button _button;
    ListView _listView;
    private Spinner _spnLanguage;
    private String _language = "ITA";
    private Button _btnDownload;
    private int currentSongIndex;
    
    private SongsManager songManager;
    
    ArrayAdapter<String> _adapter;
    ArrayList<AudioGuide> _guides =  new ArrayList<AudioGuide>();
    ArrayList<AudioGuide> _sdAudios=  new ArrayList<AudioGuide>();
    ArrayList<AudioGuide> _playList=  new ArrayList<AudioGuide>();
    	
	//boolean _checkConn;
 
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();		
	    try
	    {
	    	SharedPreferences settings = getSharedPreferences("SenaVetus", 0); 
	    	//_checkConn = settings.getBoolean("checkConn", false);
	    	_language = intent.getExtras().getString("language");
	    	_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("_playList");
	    	currentSongIndex = intent.getExtras().getInt("currentSongIndex", 0);
	    	songManager = new SongsManager(_language);
	    }
	    catch(Exception e)
	    {}
	    
        setContentView(R.layout.playlist_audio);        
        //setupLangSpinner();
        setupListView();
//        setupButton();        
        
    }
    
	public void onBackPressed( ) {
		finish();
	}

//	private void setupButton() {
//		_button = (Button) findViewById(R.id.testbutton);
//        _button.setOnClickListener(this);
//	}

	private void setupListView() {
		_listView = (ListView) findViewById(R.id.list);
		//_btnDownload = (Button) findViewById(R.id.button1);
		//_btnDownload.setClickable(_checkConn);
		
		//ArrayList<String> sdAudiosStrings = getAdapterSource();
		ArrayList<AudioGuide> sdAudioguides = getAdapterSource2();
		
		
			//_adapter = new ArrayAdapter<String>(this,
	         //       android.R.layout.simple_list_item_multiple_choice, sdAudiosStrings);                	
//			_adapter = new ArrayAdapter<String>(this,
//			        R.layout.rowlayout, R.id.label, sdAudiosStrings);
			PlaylistAdapter adapter = new PlaylistAdapter(this, sdAudioguides);
			   
//	        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        _listView.setAdapter(adapter);
	        _listView.setOnItemClickListener(new OnItemClickListener(){	        	        		

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				
		        Bundle b = new Bundle();		        
		        b.putInt("currentSongIndex", arg2);
		 		        
		 //--------------------------------------------------------------------------------------
		 //       setResult(Activity.RESULT_OK, intent);
		 //--------------------------------------------------------------------------------------
		        Intent in = new Intent();
				in.putExtras(b);
				if (getParent() == null) {			
				setResult(Activity.RESULT_OK, in);}
				else
				{			
				    getParent().setResult(Activity.RESULT_OK, in);
				}
		        finish();

			}
	        });        
	}
	private void showMsg()
	{
		ProgressDialog progressDialog;
		 progressDialog = new ProgressDialog(this);
	        progressDialog.setTitle("Elenco Audio vuoto");
	        progressDialog.setMessage("Nessuna audioguida sul dispositivo. Scarica le audio guide");	       
	        progressDialog.setIndeterminate(false);
	        progressDialog.setMax(100);	        
	        progressDialog.setCancelable(true);
	        progressDialog.show();
	        
	        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            @Override
	            public void onCancel(DialogInterface dialog) {
	               
	            }
	        });	    
	}
	private ArrayList<String> getAdapterSource() {
		//_sdAudios = getSdAudios();	
		SongsManager sm = new SongsManager(_language);
		_sdAudios = sm.getSdAudioList();
		
		//loadGuideList();
		//ArrayList<AudioGuide> audioDisponibiliServer= guideList(_language);
		
		//_audioToDownload = getAudioToDownload(sdAudios, audioDisponibiliServer);
		songManager.loadGuideList(_guides);
		for(AudioGuide au: _sdAudios)
		{
			for(AudioGuide gd: _guides)
			{
				if (au.getName().equals(gd.getName()))
				{
					au.setTitle(gd.getTitle());
					break;
				}
			}
		}
		ArrayList<String> sdAudiosStrings  = sm.getSdAudioStrings(_sdAudios);
		return sdAudiosStrings;
	}
	private ArrayList<AudioGuide> getAdapterSource2() {
		//_sdAudios = getSdAudios();	
		SongsManager sm = new SongsManager(_language);
		_sdAudios = sm.getSdAudioList();
		
		//loadGuideList();
		//ArrayList<AudioGuide> audioDisponibiliServer= guideList(_language);
		
		//_audioToDownload = getAudioToDownload(sdAudios, audioDisponibiliServer);
		songManager.loadGuideList(_guides);
		for(AudioGuide au: _sdAudios)
		{
			for(AudioGuide gd: _guides)
			{
				if (au.getName().equals(gd.getName()))
				{
					au.setTitle(gd.getTitle());
					break;
				}
			}
		}
		
		return _sdAudios;
	}
		
    public void onClick(View v) {
        SparseBooleanArray checked = _listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        ArrayList<AudioGuide> selectedAGs = new ArrayList<AudioGuide>();
        
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
            {
                //selectedItems.add(_adapter.getItem(position).toString());
            	selectedItems.add(_sdAudios.get(position).getPath());
            	selectedAGs.add(_sdAudios.get(position));
            }
        }
 
        String[] outputStrArr = new String[selectedItems.size()];
 
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
           
        }
 //--------------------------------------------------------------------------------------
 //       Intent intent = new Intent(getApplicationContext(),
 //       		_AudioPlayerActivity.class);
 //--------------------------------------------------------------------------------------
        // Create a bundle object
        Bundle b = new Bundle();
        b.putSerializable("selectedItems", selectedAGs);
//        b.putString("id_audioSD", "0");
        b.putSerializable("audioToDownload", _sdAudios);
        b.putInt("currentSongIndex", 0);
        b.putString("language", _language);
 
        // Add the bundle to the intent.
//--------------------------------------------------------------------------------------
//        intent.putExtras(b);
//--------------------------------------------------------------------------------------
        
        // start the ResultActivity
        //startActivity(intent);
 //--------------------------------------------------------------------------------------
 //       setResult(Activity.RESULT_OK, intent);
 //--------------------------------------------------------------------------------------
        Intent in = new Intent();
		in.putExtras(b);
		if (getParent() == null) {			
		setResult(Activity.RESULT_OK, in);}
		else
		{			
		    getParent().setResult(Activity.RESULT_OK, in);
		}
        finish();
    }
    public void  update (View view)
    {
    	update();
    }
    public void  update ()
    {
//    	 Intent intent = new Intent(getApplicationContext(),
//         		_DownloadSelection.class);
//  
//         // Create a bundle object
//         Bundle b = new Bundle();
//        
//         b.putString("language", _language);
//  
//         // Add the bundle to the intent.
//         intent.putExtras(b);
//  
//         // start the ResultActivity
//        // startActivity(intent);       
//         startActivityForResult(intent, 100);	
         //finish();
    }
	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	   if (requestCode == 100)
	   {		  	
		    try
		    {		    	
//		    	_language = intent.getExtras().getString("language");
//		    	// intent.getExtras().getString("id_audioSD");
//		    	_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("selectedItems");
		    	setupListView();
		    }
		    catch(Exception e)
		    {
		    	Log.d("yyyy", e.toString());
		    }
	   }
	   if (requestCode == 200)
	   {	
		   
	   }
	}
}
