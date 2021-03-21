package net.atlne.dos.scenes.windows.settings.tabs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

import net.atlne.dos.Core;
import net.atlne.dos.utils.maths.HAMaths.RoundingUtils;

public class AudioSettingsTab extends Tab {
	
	/**Stores the table for arranging the elements.*/
	private VisTable table;
	/**Stores the label for displaying the master volume.*/
	private VisLabel masterVolumeLabel;
	/**Stores the label for displaying the music volume.*/
	private VisLabel musicVolumeLabel;
	/**Stores the label for displaying the SFX volume.*/
	private VisLabel sfxVolumeLabel;
	/**Stores the slider for the master volume.*/
	private VisSlider masterVolumeSlider;
	/**Stores the slider for the music volume.*/
	private VisSlider musicVolumeSlider;
	/**Stores the slider for the SFX volume.*/
	private VisSlider sfxVolumeSlider;
	/**Stores the master volume.*/
	private float masterVolume;
	/**Stores the music volume.*/
	private float musicVolume;
	/**Stores the SFX volume.*/
	private float sfxVolume;

	public AudioSettingsTab() {
		super(false, false);
		table = new VisTable();
		masterVolume = Core.audio.getMasterVolume();
		musicVolume = Core.audio.getMusicVolume();
		sfxVolume = Core.audio.getSFXVolume();
		masterVolumeLabel = new VisLabel("Master Volume: " + (int) (100 * RoundingUtils.roundTo(masterVolume, 2)) + "%");
		musicVolumeLabel = new VisLabel("Music Volume: " + (int) (100 * RoundingUtils.roundTo(musicVolume, 2)) + "%");
		sfxVolumeLabel = new VisLabel("SFX Volume: " + (int) (100 * RoundingUtils.roundTo(sfxVolume, 2)) + "%");
		masterVolumeSlider = new VisSlider(0, 1, 0.01f, false);
		masterVolumeSlider.setValue(masterVolume);
		musicVolumeSlider = new VisSlider(0, 1, 0.01f, false);
		musicVolumeSlider.setValue(musicVolume);
		sfxVolumeSlider = new VisSlider(0, 1, 0.01f, false);
		sfxVolumeSlider.setValue(sfxVolume);
		
		masterVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				masterVolume = masterVolumeSlider.getValue();
				masterVolumeLabel.setText("Master Volume: " + (int) (100 * RoundingUtils.roundTo(masterVolume, 2)) + "%");
				Core.audio.setMasterVolume(masterVolume);
			}
		});
		
		musicVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				musicVolume = musicVolumeSlider.getValue();
				musicVolumeLabel.setText("Music Volume: " + (int) (100 * RoundingUtils.roundTo(musicVolume, 2)) + "%");
				Core.audio.setMusicVolume(musicVolume);
			}
		});
		
		sfxVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				sfxVolume = sfxVolumeSlider.getValue();
				sfxVolumeLabel.setText("SFX Volume: " + (int) (100 * RoundingUtils.roundTo(sfxVolume, 2)) + "%");
				Core.audio.setSFXVolume(sfxVolume);
			}
		});
		
		table.add(masterVolumeLabel).center().padLeft(8).padRight(8).row();
		table.add(masterVolumeSlider).center().padLeft(8).padRight(8).row();
		table.add(musicVolumeLabel).center().padLeft(8).padRight(8).row();
		table.add(musicVolumeSlider).center().padLeft(8).padRight(8).row();
		table.add(sfxVolumeLabel).center().padLeft(8).padRight(8).row();
		table.add(sfxVolumeSlider).center().padLeft(8).padRight(8).row();
		table.pack();
	}

	@Override
	public Table getContentTable() {
		return table;
	}

	@Override
	public String getTabTitle() {
		return "Audio";
	}
}