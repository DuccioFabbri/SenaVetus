package it.duccius.musicplayer;



import java.util.ArrayList;

import it.duccius.download.DownloadMode;
import it.duccius.musicplayer.DownloadDialogMode.OnDownloadModeSelectionListner;
import it.duccius.visitin.catania.R;
import android.os.Bundle;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


// http://javatechig.com/android/android-dialog-fragment-example

public class PlaylistFragment extends ListFragment implements 
																OnItemSelectedListener
															  {	

	OnPlayListSelectionListner callback;

    public interface OnPlayListSelectionListner {
        public void onPlayListSelection(int selectedAudio);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
    }
    
    private String _language = "ITA";
    private SongsManager songManager;
    ListView _listView;
    
    ArrayAdapter<String> _adapter;
    ArrayList<AudioGuide> _guides;
    ArrayList<AudioGuide> _sdAudios;
    ArrayList<AudioGuide> _playList=  new ArrayList<AudioGuide>();

	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      View view = inflater.inflate(R.layout.playlist_audio, container, false);
	      return view;
	   }
	   
	   @Override
	   public void onActivityCreated(Bundle savedInstanceState) {
	      super.onActivityCreated(savedInstanceState);
	      
	      try {
	             callback = (OnPlayListSelectionListner) getActivity();
	         } catch (Exception e) {
	             throw new ClassCastException("Calling Fragment must implement OnPlayListSelectionListner"); 
	         }
	       _guides =  ((MapNavigation)getActivity())._guides;
	       _sdAudios=  ((MapNavigation)getActivity())._localAudioGuideListLang;
	       // Per i ListFragment questo elemento deve per forza chiamarsi list ?!
	      // http://stackoverflow.com/questions/26116732/android-r-id-list-that-is-not-a-listview-class-in-listfragment
	      _listView = (ListView) ((MapNavigation)getActivity()).findViewById(android.R.id.list);
	      ArrayList<AudioGuide> sdAudioguides = getAdapterSource2();
	      PlaylistAdapter adapter = new PlaylistAdapter((MapNavigation)getActivity(), sdAudioguides);
		   
//	        _listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        _listView.setAdapter(adapter);
	        	      	    
	      //getListView().setOnItemClickListener(this);
	      getListView().setOnItemClickListener(new OnItemClickListener(){	
	    	  @Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {				
					
	    		  		callback.onPlayListSelection(arg2);	}
	       
	      });
	      
	   }
	   
	  
	   private ArrayList<AudioGuide> getAdapterSource2() {
			//_sdAudios = getSdAudios();	
			//SongsManager sm = new SongsManager(_language);
			//_sdAudios = sm.getSdAudioList();
			
			//loadGuideList();
			//ArrayList<AudioGuide> audioDisponibiliServer= guideList(_language);
			
			//_audioToDownload = getAudioToDownload(sdAudios, audioDisponibiliServer);
			//songManager.loadGuideList(_guides);
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

	
		


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		callback.onPlayListSelection(arg2);	
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}