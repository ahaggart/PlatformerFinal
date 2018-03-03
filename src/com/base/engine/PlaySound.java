package com.base.engine;
import java.util.ArrayList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.Music;


public class PlaySound
{
	private ArrayList<Sound> sounds;
	private ArrayList<Music> music;
	private Music playing;

	public PlaySound()
	{
		sounds = new ArrayList<Sound>();
		music = new ArrayList<Music>();
	}
	public void init()
	{
		
	}
	public int loadSound(String filePath)
	{
		Sound sound;
		try {
			System.out.println("loaded sound: " + filePath);
			sound = new Sound(filePath);
			sounds.add(sound);
			return sounds.indexOf(sound);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public void playSound(int ID)
	{
		if( 0 <= ID && ID < sounds.size() )
			sounds.get(ID).play();
	}
	public int loadMusic(String filePath)
	{
		Music music;
		try {
			music = new Music(filePath);
			this.music.add(music);
			int ID = this.music.indexOf(music);
			System.out.println("loaded music :: " + filePath + " :: assigned to track: " + ID);
			return ID;
		} catch (SlickException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public void playMusic(int ID, boolean loop)
	{
		System.out.println("Track Requested: " + ID);
		if( 0 <= ID && ID < music.size() )
		{
			if(loop)
				music.get(ID).loop();
			else
				music.get(ID).play(1.0f,1.0f);
			playing = music.get(ID);
			System.out.println("Now playing: Track " + ID);
		}
		else
			System.out.println("Request for track: " + ID + " did not match any loaded tracks");
	}
	public boolean isMusicPlaying()
	{
		if(playing == null)
			return false;
		else if(playing.playing())
			return true;
		else
			return false;
	}
	public int getPlayingID()
	{
		return music.indexOf(playing);
	}
	public void stopMusic()
	{
		playing.stop();
	}
	public void startMusic()
	{
		playing.play();
	}
	public void pauseMusic()
	{
		playing.pause();
	}
	public void resumeMusic()
	{
		playing.resume();
	}
	public void transition( int milliseconds, double endVolume)
	{
		playing.fade(milliseconds, (float)endVolume, true);
	}
}