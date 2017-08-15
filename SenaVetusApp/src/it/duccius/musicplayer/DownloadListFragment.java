package it.duccius.musicplayer;



import it.duccius.download.DownloadFile;
import it.duccius.download.DownloadListAdapter;
import it.duccius.download.DownloadMode;
import it.duccius.download.RowItem;
import it.duccius.maps.Trail;
import it.duccius.musicplayer.Utilities.MyCallbackInterface;
import it.duccius.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.app.ProgressDialog;

// http://javatechig.com/android/android-dialog-fragment-example

public class DownloadListFragment extends ListFragment implements 
																OnItemSelectedListener
															  {	
	//OnDownloadListSelectionListner callback;
	OnDownloadListEndedListner callback;

    public interface OnDownloadListEndedListner {
        public void onDownloadListEnded(boolean result);
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                  
    }
    
    ListView _listView;
    Button _button;
    
    ArrayAdapter<String> _adapter;
    ArrayList<Trail> _trails;
    int _selectedTrail;
    
    ArrayList<AudioGuide> _audioToDownloadLang =  new ArrayList<AudioGuide>();

	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      View view = inflater.inflate(R.layout.download_audio, container, false);
	      
	      setupButton(view);
	      
	      return view;
	   }
	   
	   @Override
	   public void onActivityCreated(Bundle savedInstanceState) {
	      super.onActivityCreated(savedInstanceState);
	      
	      try {
	             callback = (OnDownloadListEndedListner) getActivity();
	         } catch (Exception e) {
	             throw new ClassCastException("Calling Fragment must implement OnDownloadListEndedListner"); 
	         }
	      
	       // Per i ListFragment questo elemento deve per forza chiamarsi list ?!
	      // http://stackoverflow.com/questions/26116732/android-r-id-list-that-is-not-a-listview-class-in-listfragment
	      _listView = (ListView) ((MapNavigation)getActivity()).findViewById(android.R.id.list);
	      
	      _audioToDownloadLang  = ((MapNavigation)getActivity())._audioToDownloadLang.getAudioGuides();
	      
	      DownloadListAdapter adapter = new DownloadListAdapter((MapNavigation)getActivity(), _audioToDownloadLang);
		   
	        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        _listView.setAdapter(adapter);
	        	      	    
	      //getListView().setOnItemClickListener(this);
	      getListView().setOnItemClickListener(new OnItemClickListener(){	
	    	  @Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {				
					
	    		  		//..
	    		  }	       
	      });	      
	   }

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		//...		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub		
	}
	private void setupButton(View view) {
		_button = (Button) view.findViewById(R.id.testbutton);
		_button.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	onClick_1();
	            }
	        });
	}
    public void onClick_1() {
        SparseBooleanArray checked = (SparseBooleanArray) ((DownloadListAdapter)_listView.getAdapter()).checkBoxState;
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
            {
                //selectedItems.add(_adapter.getItem(position).toString());
            	AudioGuide ag = _audioToDownloadLang.get(position);
            	//selectedItems.add(ag.getPath());
            	selectedItems.add(Utilities.getMp3UrlFromName(ag.getName()));
            }
        }
        
//        //--------
        starDownload(selectedItems,new MyCallbackInterface() {
//
            @Override
            public void onDownloadFinished(List<RowItem> rowItems) {
            	callback.onDownloadListEnded(true);
            }
        });
 		//---------------
                
    }
    private void starDownload(ArrayList<String> arL, MyCallbackInterface callback) {
		ProgressDialog progressDialog = new ProgressDialog(((MapNavigation)getActivity()));
		progressDialog.setTitle("In progress...");
		progressDialog.setMessage("Loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);
		progressDialog.setMax(100);
		progressDialog.setIcon(R.drawable.arrow_stop_down);
		progressDialog.setCancelable(true);
		progressDialog.show();
		DownloadFile df = new DownloadFile((MapNavigation)getActivity(),"ITA",progressDialog, callback);
		df.execute(arL);
	}
}