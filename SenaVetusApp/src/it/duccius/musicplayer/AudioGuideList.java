package it.duccius.musicplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.lang.Iterable;

public class AudioGuideList extends ArrayList<AudioGuide> implements Iterable<AudioGuide>{ 


private ArrayList<AudioGuide> audioGuides = new ArrayList<AudioGuide>();

@SuppressWarnings("unchecked")
public void sort()
{
	Collections.sort(audioGuides);	
}

public AudioGuide getFromPosition(int pos)
{
	return audioGuides.get(pos);
}
public AudioGuide getFromTitle(String title)
{
	for (AudioGuide audioGuide: audioGuides)
	{
		if (audioGuide.getTitle().equals(title))
		{
			return audioGuide;			
		}
	}
	return null;
}
public AudioGuide getFromName(String name)
{
	for (AudioGuide audioGuide: audioGuides)
	{
		if (audioGuide.getName().equals(name))
		{
			return audioGuide;			
		}
	}
	return null;
}
public String toString() {
    String s= "";
    for (Iterator<AudioGuide> iter=audioGuides.iterator();iter.hasNext();) {
    	AudioGuide p = (AudioGuide)iter.next();
        s += p.getTitle() + "\n" + p.getName() + "\n\n";
    }
    return s;
}
public ArrayList<String> getAudioTitles() {
	ArrayList<String> audioTitles = new ArrayList<String>();
	for (AudioGuide audio: audioGuides)
	{
		audioTitles.add(audio.getTitle());
	}
	return audioTitles;
}

public boolean add(AudioGuide newAudioGuide) {
	return audioGuides.add(newAudioGuide);
}

public ArrayList<AudioGuide> getAudioGuides() {
    return audioGuides;
}

public void setAudioGuides(ArrayList<AudioGuide> audioGuides) {
    this.audioGuides = audioGuides;
}

@Override
public Iterator iterator() {
	// TODO Auto-generated method stub
	return this.audioGuides.iterator();
}

}
