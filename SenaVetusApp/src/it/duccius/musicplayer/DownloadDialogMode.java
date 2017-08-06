package it.duccius.musicplayer;



import it.duccius.download.DownloadMode;
import it.duccius.visitin.catania_en.R;
import android.os.Bundle;

import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


// http://javatechig.com/android/android-dialog-fragment-example

public class DownloadDialogMode extends DialogFragment  {	

	private OnDownloadModeSelectionListner callback;

    public interface OnDownloadModeSelectionListner {
        public void onDownloadModeSelection(int downloadMode);
    }
    
    public DownloadDialogMode() {
        // Empty constructor required for DialogFragment
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
             callback = (OnDownloadModeSelectionListner) getActivity();
         } catch (Exception e) {
             throw new ClassCastException("Calling Fragment must implement OnDownloadModeSelectionListner"); 
         }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
            View view = inflater.inflate(R.layout.fragment_download_mode, container,false);
       
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().setTitle(R.string.mapnavigation_download_title);

        Button button1 = (Button) view.findViewById(R.id.frg_download_button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	callback.onDownloadModeSelection(DownloadMode.ALL);   
            	((MapNavigation)getActivity()).linlaHeaderProgress.setVisibility(View.GONE);
            	((MapNavigation)getActivity()).toolbar.setVisibility(View.VISIBLE);
                  dismiss();
            }
        });
        Button button2 = (Button) view.findViewById(R.id.frg_download_button2);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	
                	callback.onDownloadModeSelection(DownloadMode.SINGLE);
                	((MapNavigation)getActivity()).linlaHeaderProgress.setVisibility(View.GONE);
                	((MapNavigation)getActivity()).toolbar.setVisibility(View.VISIBLE);
                      dismiss();               
            }
        });
        
        return view;
    }

}
