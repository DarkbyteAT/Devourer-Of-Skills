package net.atlne.dos.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.reflect.TypeToken;

import net.atlne.dos.Core;
import net.atlne.dos.Manager;

public class AudioHelper extends Manager {
	
	/**Stores all supported sound file extensions.*/
	public static final String[] SUPPORTED_EXTENSIONS = {".mp3", ".wav", ".ogg"};
	
	/**Stores all music for the sound manager.*/
	private ConcurrentHashMap<String, Music> music = new ConcurrentHashMap<String, Music>();
	/**Stores all sound effects for the sound manager.*/
	private ConcurrentHashMap<String, Sound> sfx = new ConcurrentHashMap<String, Sound>();
	/**Stores the currently playing music track's name.*/
	private String currentTrack = "";
	/**Stores the master volume, the volume for music and for sound effects.*/
	private float masterVolume = 1, musicVolume = 1, sfxVolume = 1;
	
	/**Loads in the current volume from the JSON file.*/
	public AudioHelper(Core core) {
		super(core);
		
		if(!Gdx.files.local("config/volume.json").exists()) {
			if(!Gdx.files.local("config").exists()) Gdx.files.local("config").mkdirs();
			Gdx.files.local("config/volume.json")
				.writeString(Gdx.files.internal("net/atlne/dos/audio/default-volume.json").readString(), false);
		} else {
			ArrayList<Float> volumes = core.getJson().get("config/volume.json", new TypeToken<ArrayList<Float>>(){}.getType());
			masterVolume = volumes.get(0);
			musicVolume = volumes.get(1);
			sfxVolume = volumes.get(2);
		}
	}
	
	/**Disposes of any loaded music or sound effects.*/
	@Override
	public void dispose() {
		if(!Gdx.files.local("config").exists())
			Gdx.files.local("config").mkdirs();
		core.getJson().set("config/volume.json", Arrays.asList(masterVolume, musicVolume, sfxVolume));
		
		/**Iterates over each music track.*/
		for(Music m : music.values())
			/**Disposes of the track.*/
			m.dispose();
		/**Iterates over each sound effect.*/
		for(Sound s : sfx.values())
			/**Disposes of the sound effect.*/
			s.dispose();
		/**Clears the maps.*/
		music.clear();
		sfx.clear();
	}
	
	/**Plays music, given the name. 
	 * If the music track is not already loaded, adds it to the map. 
	 * Only adds to the map if the track exists.*/
	public void playMusic(String name) {
		/**First checks if the music track exists in the map.*/
		/**If not, attempts to add it.*/
		if(!music.containsKey(name)) {
			/**Flag to check whether the track was added.*/
			boolean added = false;
			
			/**Iterates over each supported extension.*/
			for(String extension : SUPPORTED_EXTENSIONS) {
				/**Creates a file to check if the file exists under the given extension.*/
				FileHandle file = Gdx.files.local("assets/music/" + name + extension);
				
				/**Checks if the file exists.*/
				if(file.exists()) {
					/**If so, adds it to the map.*/
					music.put(name, Gdx.audio.newMusic(file));
					/**Sets the flag to true as a music track was found.*/
					added = true;
					/**Exits the loop.*/
					break;
				}
			}
			
			/**Checks if the current track name exists and new music was added.*/
			if(added) {
				/**If so, stops the track, sets the current track to the given name and plays the new track.*/
				if(music.containsKey(currentTrack))
					/**Stops the current track.*/
					music.get(currentTrack).stop();
				/**Sets the current track to the given song from the parameters.*/
				currentTrack = name;
				/**Sets the current track to loop.*/
				music.get(currentTrack).setLooping(true);
				/**Sets the music's volume to match the volume variables.*/
				music.get(currentTrack).setVolume(masterVolume * musicVolume);
				/**Starts playing the new current track.*/
				music.get(currentTrack).play();
			} else {
				/**Stops the current track.*/
				if(music.containsKey(currentTrack))
					music.get(currentTrack).stop();
				/**Sets the current track to nothing as nothing is playing.*/
				currentTrack = "";
			}
		} else { /**Else if a track is playing.*/
			/**Checks if a track is playing.*/
			if(music.get(currentTrack) != null) {
				/**Only stops the track if the current track isn't the same as the track being requested.*/
				if(!currentTrack.equals(name)) {
					/**Stops the current track.*/
					music.get(currentTrack).stop();
					/**Sets the current track to the given song from the parameters.*/
					currentTrack = name;
				}
				
				/**Sets the current track to loop.*/
				music.get(currentTrack).setLooping(true);
				/**Sets the music's volume to match the volume variables.*/
				music.get(currentTrack).setVolume(masterVolume * musicVolume);
				/**Starts playing the new current track.*/
				music.get(currentTrack).play();
			}
		}
	}
	
	/**Plays a sound effect, given the name.*/
	public void playSoundEffect(String name) {
		/**Checks if the sound effect is in the map.*/
		if(!sfx.containsKey(name)) {
			/**Flag to check whether the sound effect was added.*/
			boolean added = false;
			
			/**Iterates over each supported extension.*/
			for(String extension : SUPPORTED_EXTENSIONS) {
				/**Creates a file to check if the file exists under the given extension.*/
				FileHandle file = Gdx.files.local("assets/sfx/" + name + extension);
				
				/**Checks if the file exists.*/
				if(file.exists()) {
					/**If so, adds it to the map.*/
					sfx.put(name, Gdx.audio.newSound(file));
					/**Sets the flag to true as a sound effect was found.*/
					added = true;
					/**Exits the loop.*/
					break;
				}
			}
			
			/**Checks if the sound effect was added, if so, plays it.*/
			if(added) {
				sfx.get(name).play(masterVolume * sfxVolume);
			}
		} else {
			/**If the sound effect exists, plays it.*/
			sfx.get(name).play(masterVolume * sfxVolume);
		}
	}

	public String getCurrentTrack() {
		return currentTrack;
	}
	
	public float getMasterVolume() {
		return masterVolume;
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public float getSFXVolume() {
		return sfxVolume;
	}

	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;
		/**Sets the current track volume if the current track isn't null*/
		if(music.get(currentTrack) != null)
			music.get(currentTrack).setVolume(masterVolume * musicVolume);
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		/**Sets the current track volume if the current track isn't null*/
		if(music.get(currentTrack) != null)
			music.get(currentTrack).setVolume(masterVolume * musicVolume);
	}

	public void setSFXVolume(float sfxVolume) {
		this.sfxVolume = sfxVolume;
	}
}