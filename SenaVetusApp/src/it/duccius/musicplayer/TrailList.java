package it.duccius.musicplayer;


import it.duccius.maps.Trail;

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


public class TrailList extends Activity implements
OnClickListener{

    ListView _listView;
    int _selectedTrail;
     
    ArrayList<Trail> _trails=  new ArrayList<Trail>();
    	
	//boolean _checkConn;
 
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();		
	    try
	    {	    		    	
	    	_trails = (ArrayList<Trail>) intent.getExtras().getSerializable("_trails");
	    	_selectedTrail= (int) intent.getIntExtra("_selectedTrail",0);
	    	
	    }
	    catch(Exception e)
	    {}
	    
        setContentView(R.layout.trail_list);                
        setupListView();               
    }
    
	public void onBackPressed( ) {
		finish();
	}

	private void setupListView() {
		_listView = (ListView) findViewById(R.id.list);
		
			TrailListAdapter adapter = new TrailListAdapter(this, _trails);
	        _listView.setAdapter(adapter);
	        _listView.setOnItemClickListener(new OnItemClickListener(){	        	        		

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				
		        Bundle b = new Bundle();		        
		        b.putInt("_selectedTrail", arg2);
		 		        
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
//	public void onClick(View v) {
//        SparseBooleanArray checked = _listView.getCheckedItemPositions();
//        ArrayList<String> selectedItems = new ArrayList<String>();
//        ArrayList<AudioGuide> selectedAGs = new ArrayList<AudioGuide>();
//        
//        for (int i = 0; i < checked.size(); i++) {
//            // Item position in adapter
//            int position = checked.keyAt(i);
//            // Add sport if it is checked i.e.) == TRUE!
//            if (checked.valueAt(i))
//            {
//                //selectedItems.add(_adapter.getItem(position).toString());
////            	selectedItems.add(_sdAudios.get(position).getPath());
////            	selectedAGs.add(_sdAudios.get(position));
//            }
//        }
// 
//        String[] outputStrArr = new String[selectedItems.size()];
// 
//        for (int i = 0; i < selectedItems.size(); i++) {
//            outputStrArr[i] = selectedItems.get(i);
//           
//        }
// //--------------------------------------------------------------------------------------
// //       Intent intent = new Intent(getApplicationContext(),
// //       		_AudioPlayerActivity.class);
// //--------------------------------------------------------------------------------------
//        // Create a bundle object
//        Bundle b = new Bundle();
//        b.putSerializable("selectedItems", selectedAGs);
////        b.putString("id_audioSD", "0");
//        b.putInt("currentSongIndex", 0);
// 
//        // Add the bundle to the intent.
////--------------------------------------------------------------------------------------
////        intent.putExtras(b);
////--------------------------------------------------------------------------------------
//        
//        // start the ResultActivity
//        //startActivity(intent);
// //--------------------------------------------------------------------------------------
// //       setResult(Activity.RESULT_OK, intent);
// //--------------------------------------------------------------------------------------
//        Intent in = new Intent();
//		in.putExtras(b);
//		if (getParent() == null) {			
//		setResult(Activity.RESULT_OK, in);}
//		else
//		{			
//		    getParent().setResult(Activity.RESULT_OK, in);
//		}
//        finish();
//    }
//    public void  update (View view)
//    {
//    	update();
//    }
//    public void  update ()
//    {
//    }
//	@SuppressWarnings("unchecked")
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//	   if (requestCode == 100)
//	   {		  	
//		    try
//		    {		    	
////		    	_language = intent.getExtras().getString("language");
////		    	// intent.getExtras().getString("id_audioSD");
////		    	_playList = (ArrayList<AudioGuide>) intent.getExtras().getSerializable("selectedItems");
//		    	setupListView();
//		    }
//		    catch(Exception e)
//		    {
//		    	Log.d("yyyy", e.toString());
//		    }
//	   }
//	   if (requestCode == 200)
//	   {	
//		   
//	   }
//	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
