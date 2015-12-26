package it.duccius.maps;

import it.duccius.musicplayer.Audio;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrailColor {

	static private List<String> colors = Arrays.asList("183,219,171", "238,156,150","214,152,173","17,145,139","249,247,166");
	static public String GetColor(int i)
	{
		return colors.get(i%5);
		
	}
	public TrailColor()
	{
		
	}
}
