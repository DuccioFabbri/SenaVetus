package it.duccius.musicplayer;

import it.duccius.maps.Trail;
import it.duccius.visitin.catania_en.R;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrailListAdapter extends ArrayAdapter<Trail> {
  private final Context context;
  private final ArrayList<Trail> _trails;

  public TrailListAdapter(Context context, ArrayList<Trail> trails) {
    super(context, R.layout.trail_item, trails);
    this.context = context;
    this._trails = trails;
  }

  @SuppressLint("ViewHolder")
@Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.trail_item, parent, false);
    
    TextView name = (TextView) rowView.findViewById(R.id.trail_name);
    TextView description = (TextView) rowView.findViewById(R.id.trail_description);
    
    String titolo = _trails.get(position).getName() + " - " + _trails.get(position).getTime();
    name.setText(titolo);
    description.setText(_trails.get(position).getDescription());
    
    return rowView;
  }
} 
