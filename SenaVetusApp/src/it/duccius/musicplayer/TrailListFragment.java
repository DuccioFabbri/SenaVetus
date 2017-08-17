package it.duccius.musicplayer;



import it.duccius.maps.Trail;
import it.duccius.visitin.catania.R;

import java.util.ArrayList;

import android.os.Bundle;

import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// http://javatechig.com/android/android-dialog-fragment-example

public class TrailListFragment extends ListFragment implements 
																OnItemSelectedListener
															  {	
	OnTrailListSelectionListner callback;

    public interface OnTrailListSelectionListner {
        public void onTrailListSelection(int selectedAudio);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
    }
    
    ListView _listView;
    
    ArrayAdapter<String> _adapter;
    ArrayList<Trail> _trails;
    int _selectedTrail;
    ArrayList<AudioGuide> _playList=  new ArrayList<AudioGuide>();

	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      View view = inflater.inflate(R.layout.trail_list, container, false);
	      return view;
	   }
	   
	   @Override
	   public void onActivityCreated(Bundle savedInstanceState) {
	      super.onActivityCreated(savedInstanceState);
	      
	      try {
	             callback = (OnTrailListSelectionListner) getActivity();
	         } catch (Exception e) {
	             throw new ClassCastException("Calling Fragment must implement OnTrailListSelectionListner"); 
	         }
	      _trails =  ((MapNavigation)getActivity())._trails;
	      _selectedTrail=  ((MapNavigation)getActivity())._selectedTrail;
	       // Per i ListFragment questo elemento deve per forza chiamarsi list ?!
	      // http://stackoverflow.com/questions/26116732/android-r-id-list-that-is-not-a-listview-class-in-listfragment
	      _listView = (ListView) ((MapNavigation)getActivity()).findViewById(android.R.id.list);
	      
	      TrailListAdapter adapter = new TrailListAdapter((MapNavigation)getActivity(), _trails);
		   
//	        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        _listView.setAdapter(adapter);
	        	      	    
	      //getListView().setOnItemClickListener(this);
	      getListView().setOnItemClickListener(new OnItemClickListener(){	
	    	  @Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {				
					
	    		  		callback.onTrailListSelection(arg2);	}	       
	      });	      
	   }

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		callback.onTrailListSelection(arg2);			
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub		
	}
}