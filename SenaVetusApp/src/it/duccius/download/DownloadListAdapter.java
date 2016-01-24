package it.duccius.download;

import it.duccius.musicplayer.AudioGuide;
import it.duccius.musicplayer.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class DownloadListAdapter extends ArrayAdapter<AudioGuide> {
  private final Context context;
  private final ArrayList<AudioGuide> _sdAudiosStrings;
  // http://android-er.blogspot.it/2012/11/implement-custom-multi-select-listview.html
  public SparseBooleanArray checkBoxState = new SparseBooleanArray();

  public DownloadListAdapter(Context context, ArrayList<AudioGuide> sdAudiosStrings) {
    super(context, R.layout.download_item, sdAudiosStrings);
    this.context = context;
    this._sdAudiosStrings = sdAudiosStrings;
    
    for(int i = 0; i < sdAudiosStrings.size(); i++){
    	checkBoxState.put(i, false);
       }
  }

  @SuppressLint("ViewHolder")
@Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
    View rowView = inflater.inflate(R.layout.download_item, parent, false);
    
    TextView title = (TextView) rowView.findViewById(R.id.title);
   // TextView dimension = (TextView) rowView.findViewById(R.id.dimension);
    
    String titolo = _sdAudiosStrings.get(position).getName() ;
    title.setText(titolo);
    //dimension.setText(_sdAudiosStrings.get(position).getDescription());
    
    // http://stackoverflow.com/questions/12957553/android-listview-with-custom-layout-getcheckeditempositions
    final CheckBox multi_checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
    multi_checkBox.setChecked(false);
    multi_checkBox.setOnClickListener(new View.OnClickListener() {

           public void onClick(View v) {
            if(((CheckBox)v).isChecked())
            {
                checkBoxState.put(position, true);                   
                v.setSelected(true);   
                //listView.setItemChecked(position,true);
            }
            else
            	checkBoxState.put(position, false);
                v.setSelected(false);

            }
           });

    
    return rowView;
  }
  
  // http://www.shubhayu.com/android/listview-with-arrayadapter-and-customized-items
  public int getCount(){	  
	    return  _sdAudiosStrings.size();
	}
  
} 

