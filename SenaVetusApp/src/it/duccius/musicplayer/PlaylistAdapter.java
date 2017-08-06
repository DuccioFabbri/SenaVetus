package it.duccius.musicplayer;

import it.duccius.visitin.catania_en.R;

import java.util.ArrayList;

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

public class PlaylistAdapter extends ArrayAdapter<AudioGuide> {
  private final Context context;
  private final ArrayList<AudioGuide> values;

  public PlaylistAdapter(Context context, ArrayList<AudioGuide> values) {
    super(context, R.layout.rowlayout, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.label);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    textView.setText(values.get(position).getTitle());
    // Change the icon for Windows and iPhone
    
    String picName = values.get(position).getName()+".jpg";    
      
    String nome = Environment.getExternalStoragePublicDirectory(ApplicationData.getPicFolder()+"/"+picName).toString();
    Bitmap bitmap = BitmapFactory.decodeFile(nome);
    imageView.setImageBitmap(bitmap);          

    return rowView;
  }
} 
