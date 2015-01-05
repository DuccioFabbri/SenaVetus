package it.duccius.download;
 
import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.AudioGuide;
import it.duccius.musicplayer.AudioGuideList;
import it.duccius.musicplayer.PlayListAudio;
import it.duccius.musicplayer.R;
import it.duccius.musicplayer.SongsManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class _DownloadSelection extends Activity implements
        OnClickListener {
    Button _button;
    ListView _listView;
    private Spinner _spnLanguage;
    private String _language = "ITA";
    
    ArrayAdapter<String> _adapter;
    ArrayList<AudioGuide> _guides =  new ArrayList<AudioGuide>();
    
    ArrayList<AudioGuide> _audioToDownload; 
    AudioGuideList _audioToDownloadLang = new AudioGuideList();
    ArrayList<AudioGuide> ags;
    ArrayList<AudioGuide> _playList=  new ArrayList<AudioGuide>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();		
	    try
	    {
	    	//_language = intent.getExtras().getString("language");	
	    	//ArrayList audioToDownloadLang = (ArrayList)intent.getSerializableExtra("_audioToDownloadLang");
	    	_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("_playList");
	    	ags = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("_audioToDownloadLang");
	    	_audioToDownloadLang.setAudioGuides(ags);
	    }
	    catch(Exception e)
	    {
	    	Log.d(getPackageName(), e.getMessage());
	    }
	    
        setContentView(R.layout.download_selection);        
        setupLangSpinner();
        setupListView();
        setupButton();        
        
    }
	public void onBackPressed( ) {
		Intent in = new Intent(getApplicationContext(),
				PlayListAudio.class);
		in.putExtra("language", getSelectedLang());
		setResult(100, in);
//		startActivity(in);
//		// Closing PlayListView
		finish();
	}

	private void setupButton() {
		_button = (Button) findViewById(R.id.testbutton);
        _button.setOnClickListener(this);
	}

	private void setupListView() {
		_listView = (ListView) findViewById(R.id.list);
		ArrayList<String> sdAudiosStrings = getAdapterSource();
		
		_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, sdAudiosStrings);                	
        
        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        _listView.setAdapter(_adapter);
	}
	private ArrayList<String> getAdapterSource() {
		SongsManager sm = new SongsManager(_language);
		ArrayList<AudioGuide> sdAudios = sm.getSdAudioList();	    						
		
		sm.loadGuideList(_guides);
		
		ArrayList<AudioGuide> audioDisponibiliServer= sm.guideListByLang(_guides);
		sm.getAudioToDownload(sdAudios, audioDisponibiliServer);
		_audioToDownload = audioDisponibiliServer;
		
		ArrayList<String> sdAudiosStrings  = sm.getSdAudioStrings(_audioToDownload);
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
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                //selectedItems.add(_adapter.getItem(position).toString());
            	selectedItems.add(_audioToDownload.get(position).getPath());
        }
 
        String[] outputStrArr = new String[selectedItems.size()];
 
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
           
        }
 
        Intent intent = new Intent(getApplicationContext(),
        		Download.class);
 
        // Create a bundle object
        Bundle b = new Bundle();
        b.putSerializable("audioToDownload", _audioToDownload);
        b.putStringArray("selectedItems", outputStrArr);
        b.putString("language", _language);
 
        // Add the bundle to the intent.
        intent.putExtras(b);
 
        // start the ResultActivity
        //startActivity(intent);
        startActivityForResult(intent, 100);
        
        // non aspetto il ritorno perchè tanto lo invia la prossima activity
        finish();
    }
}