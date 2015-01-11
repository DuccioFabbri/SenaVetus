package it.duccius.download;
 
import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.AudioGuide;
import it.duccius.musicplayer.AudioGuideList;
import it.duccius.musicplayer.MapNavigation;
import it.duccius.musicplayer.PlayListAudio;
import it.duccius.musicplayer.R;
import it.duccius.musicplayer.SongsManager;
import it.duccius.musicplayer.Utilities;
import it.duccius.musicplayer.Utilities.MyCallbackInterface;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class DownloadAudio extends Activity implements
        OnClickListener {
    Button _button;
    ListView _listView;
    private Spinner _spnLanguage;
    private String _language = "ITA";
    
    ArrayAdapter<String> _adapter;
    ArrayList<AudioGuide> _guides =  new ArrayList<AudioGuide>();
    
    ArrayList<AudioGuide> _audioToDownload; 
    AudioGuideList _audioToDownloadLang = new AudioGuideList();
   
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
	    	 ArrayList<AudioGuide> ags = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("_audioToDownloadLang");
	    	_audioToDownloadLang.setAudioGuides(ags);
	    }
	    catch(Exception e)
	    {
	    	Log.d(getPackageName(), e.getMessage());
	    }
	    
        setContentView(R.layout.download_audio);        
        setupLangSpinner();
        setupListView();
        setupButton();        
        
    }
	public void onBackPressed( ) {
//		Intent in = new Intent(getApplicationContext(),
//				MapNavigation.class);
		Intent in = new Intent();
		
		//in.putExtra("language", getSelectedLang());
		if (getParent() == null) {
		setResult(Activity.RESULT_CANCELED, in);}
		else
		{
		    getParent().setResult(Activity.RESULT_CANCELED, in);
		}
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
		
		ArrayList<String> sdAudiosStrings  = _audioToDownloadLang.getAudioTitles();
		
		_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, sdAudiosStrings);                	
        
        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        _listView.setAdapter(_adapter);
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
            {
                //selectedItems.add(_adapter.getItem(position).toString());
            	AudioGuide ag = _audioToDownloadLang.getFromPosition(position);
            	//selectedItems.add(ag.getPath());
            	selectedItems.add(Utilities.getMp3UrlFromName(ag.getName()));
            }
        }
 
//        Intent intent = new Intent(getApplicationContext(),
//        		Download.class);
//      
//        
//        // Create a bundle object
//        Bundle b = new Bundle();
//       // b.putSerializable("audioToDownload", _audioToDownloadLang.getAudioGuides());
//        //b.putStringArray("selectedItems", outputStrArr);
//        b.putSerializable("selectedItems", selectedItems);
//        b.putString("language", _language);
// 
//        // Add the bundle to the intent.
//        intent.putExtras(b);
// 
//        // start the ResultActivity
//        //startActivity(intent);
//        startActivityForResult(intent, 1);
        
        //--------
        starDownload(selectedItems,new MyCallbackInterface() {

            @Override
            public void onDownloadFinished(List<RowItem> rowItems) {
            	Intent intent = new Intent();
            	try{		       		    	
     	        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity            	
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }
               }
            	catch(Exception e)
            	{getParent().setResult(Activity.RESULT_CANCELED, intent);}
            	finally{
            		finish();
            	}
            }
        });
 		//---------------
                
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
    public void ______onActivityResult(int requestCode, int resultCode, Intent intent) {
    	// Per chiudere l'attività, aspetto la risposta di Download e poi la passo indietro a chi mi ha chiamato
 	   if (requestCode == 1)
 	   {		  	
 		    try
 		    {		  
 		    	Intent intent1 = new Intent();
 	     		
 	     		// Sending songIndex to PlayerActivity        
 	        intent1.putExtra("language", _language);       
 	     		//startActivity(intent);
 	        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
 	        if (getParent() == null) {
 	            setResult(resultCode, intent1);
 	        } else {
 	            getParent().setResult(resultCode, intent1);
 	        } 	        	        
 		    			
 		    }
 		    catch(Exception e)
 		    {
 		    	Log.d("zzzz", e.toString());
 		    }
 		    finally
 		    {
 		    	 finish();
 		    }
 	   }
 	}
}