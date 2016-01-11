package it.duccius.musicplayer;


import it.duccius.maps.Trail;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
		_listView = (ListView) findViewById(R.id.trail_list);
		
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
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
