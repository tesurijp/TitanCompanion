package pt.joaomneto.ffgbutil.adventure.impl;

import java.io.BufferedWriter;
import java.io.IOException;

import pt.joaomneto.ffgbutil.R;
import pt.joaomneto.ffgbutil.adventure.Adventure;
import android.os.Bundle;
import android.view.Menu;

public class TROKAdventure extends Adventure {
	
	
	private Integer currentWeapons = -1;
	private Integer currentShields = -1;
	private Integer initialWeapons = -1;
	private Integer initialShields = -1;
	private Integer missiles = -1;

	protected static final int FRAGMENT_VEHICLE_COMBAT = 2;

	public TROKAdventure() {
		super();
		fragmentConfiguration.clear();
		fragmentConfiguration.put(FRAGMENT_VITAL_STATS, new AdventureFragmentRunner(R.string.vitalStats,
				"pt.joaomneto.ffgbutil.adventure.impl.fragments.AdventureVitalStatsFragment"));
		fragmentConfiguration.put(FRAGMENT_COMBAT, new AdventureFragmentRunner(R.string.fights,
				"pt.joaomneto.ffgbutil.adventure.impl.fragments.trok.TROKAdventureCombatFragment"));
		fragmentConfiguration.put(FRAGMENT_VEHICLE_COMBAT, new AdventureFragmentRunner(R.string.vehicleCombat,
				"pt.joaomneto.ffgbutil.adventure.impl.fragments.trok.TROKStarShipCombatFragment"));
		fragmentConfiguration.put(FRAGMENT_EQUIPMENT, new AdventureFragmentRunner(R.string.goldEquipment,
				"pt.joaomneto.ffgbutil.adventure.impl.fragments.AdventureEquipmentFragment"));
		fragmentConfiguration.put(FRAGMENT_NOTES, new AdventureFragmentRunner(R.string.notes,
				"pt.joaomneto.ffgbutil.adventure.impl.fragments.AdventureNotesFragment"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.adventure, menu);
		return true;
	}

	@Override
	public void storeAdventureSpecificValuesInFile(BufferedWriter bw) throws IOException {


		bw.write("currentWeapons="+currentWeapons+"\n");
		bw.write("currentShields="+currentShields+"\n");
		bw.write("initialWeapons="+initialWeapons+"\n");
		bw.write("initialShields="+initialShields+"\n");
		bw.write("missiles="+missiles+"\n");
		bw.write("gold=" + getGold() + "\n");
		bw.write("provisions=" + getProvisions() + "\n");
		bw.write("provisionsValue=" + getProvisionsValue() + "\n");
	}

	


	public Integer getCurrentWeapons() {
		return currentWeapons;
	}

	public void setCurrentWeapons(Integer currentWeapons) {
		this.currentWeapons = currentWeapons;
	}

	public Integer getCurrentShields() {
		return currentShields;
	}

	public void setCurrentShields(Integer currentShields) {
		this.currentShields = currentShields;
	}

	public Integer getInitialWeapons() {
		return initialWeapons;
	}

	public void setInitialWeapons(Integer initialWeapons) {
		this.initialWeapons = initialWeapons;
	}

	public Integer getInitialShields() {
		return initialShields;
	}

	public void setInitialShields(Integer initialShields) {
		this.initialShields = initialShields;
	}

	@Override
	protected void loadAdventureSpecificValuesFromFile() {

		setCurrentWeapons(Integer.valueOf(getSavedGame().getProperty("currentWeapons")));
		setCurrentShields(Integer.valueOf(getSavedGame().getProperty("currentShields")));
		setInitialWeapons(Integer.valueOf(getSavedGame().getProperty("initialWeapons")));
		setInitialShields(Integer.valueOf(getSavedGame().getProperty("initialShields")));
		setMissiles(Integer.valueOf(getSavedGame().getProperty("missiles")));
		setProvisions(Integer.valueOf(getSavedGame().getProperty("provisions")));
		setProvisionsValue(Integer.valueOf(getSavedGame().getProperty("provisionsValue")));
	}

	public Integer getMissiles() {
		return missiles;
	}

	public void setMissiles(Integer missiles) {
		this.missiles = missiles;
	}

	
	
	
	

	
}