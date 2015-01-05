package it.duccius.musicplayer;

import it.duccius.download._DownloadSelection;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;


public class PlayListAudio extends Activity implements
OnClickListener{
	// Songs list
	    Button _button;
    ListView _listView;
    private Spinner _spnLanguage;
    private String _language = "ITA";
    private Button _btnDownload;
    
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
	    	songManager = new SongsManager(_language);
	    }
	    catch(Exception e)
	    {}
	    
        setContentView(R.layout.playlist_audio);        
        setupLangSpinner();
        //setupListView();
        setupButton();        
        
    }
	public void onBackPressed( ) {
		finish();
	}

	private void setupButton() {
		_button = (Button) findViewById(R.id.testbutton);
        _button.setOnClickListener(this);
	}

	private void setupListView() {
		_listView = (ListView) findViewById(R.id.list);
		//_btnDownload = (Button) findViewById(R.id.button1);
		//_btnDownload.setClickable(_checkConn);
		
		ArrayList<String> sdAudiosStrings = getAdapterSource();
		if(sdAudiosStrings.size()<1) 
		{
			showMsg();	
		}
		else
		{
			_adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_multiple_choice, sdAudiosStrings);                	
	        
	        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        _listView.setAdapter(_adapter);
	        for (AudioGuide sdAg: _sdAudios)
	        {
	        	for(AudioGuide ag: _playList )
	        	{
	        		if(sdAg.getName().equalsIgnoreCase(ag.getName()))
	        			_listView.setItemChecked(sdAg.getSdPosition(), true);
	        	}
	        }
		}
        
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
	
	private void setupLangSpinner() {
		_spnLanguage = (Spinner)findViewById(R.id.spnLanguage);
		ArrayAdapter<String> adapterLang = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		ApplicationData.getLanguages()
        		);
        
		_spnLanguage.setAdapter(adapterLang);
		_spnLanguage.setSelection(adapterLang.getPosition(_language));
		_spnLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	_language = getSelectedLang();
            	setupListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
	}
	private String getSelectedLang() {
		return _spnLanguage.getItemAtPosition(_spnLanguage.getSelectedItemPosition()).toString();
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
 
        Intent intent = new Intent(getApplicationContext(),
        		_AudioPlayerActivity.class);
 
        // Create a bundle object
        Bundle b = new Bundle();
        b.putSerializable("selectedItems", selectedAGs);
//        b.putString("id_audioSD", "0");
        b.putSerializable("audioToDownload", _sdAudios);
        
        b.putString("language", _language);
 
        // Add the bundle to the intent.
        intent.putExtras(b);
 
        // start the ResultActivity
        //startActivity(intent);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    public void  update (View view)
    {
    	update();
    }
    public void  update ()
    {
    	 Intent intent = new Intent(getApplicationContext(),
         		_DownloadSelection.class);
  
         // Create a bundle object
         Bundle b = new Bundle();
        
         b.putString("language", _language);
  
         // Add the bundle to the intent.
         intent.putExtras(b);
  
         // start the ResultActivity
        // startActivity(intent);       
         startActivityForResult(intent, 100);	
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
