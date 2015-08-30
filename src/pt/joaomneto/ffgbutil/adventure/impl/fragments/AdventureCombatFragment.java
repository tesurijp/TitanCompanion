package pt.joaomneto.ffgbutil.adventure.impl.fragments;

import java.util.ArrayList;
import java.util.List;

import pt.joaomneto.ffgbutil.R;
import pt.joaomneto.ffgbutil.adventure.Adventure;
import pt.joaomneto.ffgbutil.adventure.AdventureFragment;
import pt.joaomneto.ffgbutil.adventure.impl.fragments.adapter.CombatantListAdapter;
import pt.joaomneto.ffgbutil.adventure.impl.util.DiceRoll;
import pt.joaomneto.ffgbutil.util.DiceRoller;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class AdventureCombatFragment extends AdventureFragment {

	protected TextView combatResult = null;
	protected Button startCombatButton = null;
	protected Button combatTurnButton = null;
	protected Button addCombatButton = null;
	protected Button testLuckButton = null;
	protected Button resetButton = null;
	protected Button resetButton2 = null;
	protected Switch combatTypeSwitch = null;
	protected View rootView = null;

	protected List<Combatant> combatPositions = new ArrayList<AdventureCombatFragment.Combatant>();
	protected CombatantListAdapter combatantListAdapter = null;
	protected ListView combatantsListView = null;

	public static final String NORMAL = "NORMAL";
	public static final String SEQUENCE = "SEQUENCE";

	public String offText = "Normal";
	public String onText = "Sequence";

	protected String combatMode = NORMAL;
	protected int handicap = 0;

	protected boolean draw = false;
	protected boolean luckTest = false;
	protected boolean hit = false;

	protected boolean combatStarted = false;

	protected int staminaLoss = 0;

	public AdventureCombatFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_adventure_combat, container, false);

		init();

		return rootView;
	}

	protected void combatTurn() {
		if (combatPositions.size() == 0)
			return;

		if (combatStarted == false) {
			combatStarted = true;
			combatTypeSwitch.setClickable(false);
		}

		if (combatMode.equals(SEQUENCE)) {
			sequenceCombatTurn();
		} else {
			standardCombatTurn();
		}
	}

	protected void switchLayoutCombatStarted() {
		addCombatButton.setVisibility(View.GONE);
		combatTypeSwitch.setVisibility(View.GONE);
		startCombatButton.setVisibility(View.GONE);
		resetButton.setVisibility(View.GONE);
		resetButton2.setVisibility(View.VISIBLE);
		testLuckButton.setVisibility(View.VISIBLE);
		combatTurnButton.setVisibility(View.VISIBLE);

	}

	public String getOfftext() {
		return "Normal";
	}

	public String getOntext() {
		return "Sequence";
	}

	protected void init() {
		
		combatResult = (TextView) rootView.findViewById(R.id.combatResult);
		combatTurnButton = (Button) rootView.findViewById(R.id.attackButton);
		startCombatButton = (Button) rootView.findViewById(R.id.startCombat);
		addCombatButton = (Button) rootView.findViewById(R.id.addCombatButton);
		combatTypeSwitch = (Switch) rootView.findViewById(R.id.combatType);
		resetButton = (Button) rootView.findViewById(R.id.resetCombat);
		resetButton2 = (Button) rootView.findViewById(R.id.resetCombat2);
		testLuckButton = (Button) rootView.findViewById(R.id.testLuckButton);

		combatTypeSwitch.setTextOff(getOfftext());
		combatTypeSwitch.setTextOn(getOntext());
		combatTypeSwitch.setOnCheckedChangeListener(new CombatTypeSwitchChangeListener());

		combatantsListView = (ListView) rootView.findViewById(R.id.combatants);
		combatantListAdapter = new CombatantListAdapter(this.getActivity(), combatPositions);
		combatantsListView.setAdapter(combatantListAdapter);

		addCombatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addCombatButtonOnClick();
			}

		});

		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetCombat();
				refreshScreensFromResume();
			}
		});
		
		resetButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetCombat();
				refreshScreensFromResume();
			}
		});

		combatTurnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				combatTurn();
			}

		});

		startCombatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startCombat();

			}

		});

		testLuckButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Adventure adv = (Adventure) getActivity();

				if (draw || luckTest)
					return;
				luckTest = true;
				boolean result = adv.testLuckInternal();
				if (result) {
					combatResult.setText("You're lucky!");
					if (hit) {
						Combatant combatant = getCurrentEnemy();
						combatant.setCurrentStamina(combatant.getCurrentStamina() - 1);
						combatant.setStaminaLoss(combatant.getStaminaLoss() + 1);
						int enemyStamina = combatant.getCurrentStamina();
						if (enemyStamina <= 0 || (getKnockoutStamina() != null && staminaLoss >= getKnockoutStamina())) {
							enemyStamina = 0;
							Adventure.showAlert("You've defeated your opponent!", adv);
							removeAndAdvanceCombat(combatant);
						}
					} else {
						adv.setCurrentStamina(adv.getCurrentStamina() + 1);
						staminaLoss--;
					}
				} else {
					combatResult.setText("You're unlucky...");
					if (hit) {
						Combatant combatant = getCurrentEnemy();
						combatant.setCurrentStamina(combatant.getCurrentStamina() + 1);
						combatant.setStaminaLoss(combatant.getStaminaLoss() + 1);

					} else {
						adv.setCurrentStamina(adv.getCurrentStamina() - 1);
						staminaLoss++;
					}

					if (getKnockoutStamina() != null && adv.getCurrentStamina() <= getKnockoutStamina()) {
						Adventure.showAlert("You've been knocked out...", adv);
					}

					if (adv.getCurrentStamina() == 0) {
						Adventure.showAlert("You're dead...", adv);
					}
				}
				refreshScreensFromResume();
			}
		});

		refreshScreensFromResume();
	}

	protected Integer getKnockoutStamina() {
		return null;
	}

	protected void sequenceCombatTurn() {

		Combatant position = getCurrentEnemy();

		draw = false;
		luckTest = false;
		hit = false;
		Adventure adv = (Adventure) getActivity();
		DiceRoll diceRoll = DiceRoller.roll2D6();
		int skill = adv.getCombatSkillValue();
		int attackStrength = diceRoll.getSum() +  skill + position.getHandicap();
		DiceRoll enemyDiceRoll = DiceRoller.roll2D6();
		int enemyAttackStrength = enemyDiceRoll.getSum() +  position.getCurrentSkill();
		if (attackStrength > enemyAttackStrength) {
			if (!position.isDefenseOnly()) {
				Boolean suddenDeath = suddenDeath(diceRoll, enemyDiceRoll);
				if (suddenDeath == null) {
					int damage = getDamage();
					position.setCurrentStamina(Math.max(0, position.getCurrentStamina() - damage));
					hit = true;
					combatResult.setText("You have hit the enemy! (" + diceRoll.getSum() +  " + " + skill
							+ (position.getHandicap() >= 0 ? (" + " + position.getHandicap()) : "") + ") vs (" + enemyDiceRoll.getSum() +  " + "
							+ position.getCurrentSkill() + "). (-" + damage + "ST)");
				} else {
					position.setCurrentStamina(0);
					Adventure.showAlert("You've defeated an enemy by sudden death!", adv);
				}
			} else {
				draw = true;
				combatResult.setText("You have blocked the enemy attack! (" + diceRoll.getSum() +  " + " + skill
						+ (position.getHandicap() >= 0 ? (" + " + position.getHandicap()) : "") + ") vs (" + enemyDiceRoll.getSum() +  " + " + position.getCurrentSkill()
						+ ")");
			}
		} else if (attackStrength < enemyAttackStrength) {
			int damage = convertDamageStringToInteger(position.getDamage());
			adv.setCurrentStamina((Math.max(0, adv.getCurrentStamina() - damage)));
			combatResult.setText("You've been hit... (" + diceRoll.getSum() +  " + " + skill + (position.getHandicap() >= 0 ? (" + " + position.getHandicap()) : "")
					+ ") vs (" + enemyDiceRoll.getSum() +  " + " + position.getCurrentSkill() + "). (-" + damage + "ST)");
		} else {

			combatResult.setText("Both you and the enemy have missed");
			draw = true;
		}

		if (position.getCurrentStamina() == 0) {
			removeAndAdvanceCombat(position);
			combatResult.setText("You have defeated an enemy!");
		} else {
			advanceCombat(position);
		}

		if (adv.getCurrentStamina() == 0) {
			combatResult.setText("You have died...");
		}

		refreshScreensFromResume();

	}

	protected void advanceCombat(Combatant combatant) {
		int index = combatPositions.indexOf(combatant);
		Combatant currentEnemy = null;

		if (!combatPositions.isEmpty()) {
			if (index <= combatPositions.size() - 2) {
				currentEnemy = combatPositions.get(index + 1);
			} else {
				currentEnemy = combatPositions.get(0);
			}
			combatantListAdapter.setCurrentEnemy(currentEnemy);
		} else {
			resetCombat();
		}
	}

	protected void removeAndAdvanceCombat(Combatant combatant) {
		int index = combatPositions.indexOf(combatant);
		combatPositions.remove(index);
		Combatant currentEnemy = null;

		if (!combatPositions.isEmpty()) {
			if (index <= combatPositions.size() - 1) {
				currentEnemy = combatPositions.get(index);
			} else {
				currentEnemy = combatPositions.get(0);
			}
			combatantListAdapter.setCurrentEnemy(currentEnemy);
			currentEnemy.setDefenseOnly(false);
		} else {
			resetCombat();
		}
	}

	

	protected void standardCombatTurn() {
		Combatant position = getCurrentEnemy();

		// if (!finishedCombats.contains(currentCombat)) {
		draw = false;
		luckTest = false;
		hit = false;
		Adventure adv = (Adventure) getActivity();
		DiceRoll diceRoll = DiceRoller.roll2D6();
		int skill = adv.getCombatSkillValue();
		int attackStrength = diceRoll.getSum() +  skill + position.getHandicap();
		DiceRoll enemyDiceRoll = DiceRoller.roll2D6();
		int enemyAttackStrength = enemyDiceRoll.getSum() +  position.getCurrentSkill();
		if (attackStrength > enemyAttackStrength) {
			Boolean suddenDeath = suddenDeath(diceRoll, enemyDiceRoll);
			if (suddenDeath == null) {
				int damage = getDamage();

				position.setCurrentStamina(Math.max(0, position.getCurrentStamina() - getDamage()));
				position.setStaminaLoss(position.getStaminaLoss() + damage);
				hit = true;
				combatResult.setText("You have hit the enemy! (" + diceRoll.getSum() +  " + " + skill
						+ (position.getHandicap() >= 0 ? (" + " + position.getHandicap()) : "") + ") vs (" + enemyDiceRoll.getSum() +  " + " + position.getCurrentSkill()
						+ ")");
			} else {
				position.setCurrentStamina(0);
				Adventure.showAlert("You've defeated an enemy by sudden death!", adv);
			}

		} else if (attackStrength < enemyAttackStrength) {
			int damage = convertDamageStringToInteger(position.getDamage());
			staminaLoss += damage;
			adv.setCurrentStamina((Math.max(0, adv.getCurrentStamina() - damage)));
			combatResult.setText("Youve have been hit... (" + diceRoll.getSum() +  " + " + skill + (position.getHandicap() >= 0 ? (" + " + position.getHandicap()) : "")
					+ ") vs (" + enemyDiceRoll.getSum() +  " + " + position.getCurrentSkill() + ")");
		} else {

			combatResult.setText("Both you and the enemy have missed");
			draw = true;
		}

		if (position.getCurrentStamina() == 0 || (getKnockoutStamina() != null && position.getStaminaLoss() >= getKnockoutStamina())) {
			combatResult.setText("You have defeated an enemy!");
			removeAndAdvanceCombat(position);

		}

		if (getKnockoutStamina() != null && staminaLoss >= getKnockoutStamina()) {
			Adventure.showAlert("You've been knocked out...", adv);
		}

		if (adv.getCurrentStamina() == 0) {
			Adventure.showAlert("You're dead...", adv);
		}
		// }

		refreshScreensFromResume();

	}

	protected void addCombatButtonOnClick() {
		addCombatButtonOnClick(R.layout.component_add_combatant);
	}

	protected void addCombatButtonOnClick(int layoutId) {

		Adventure adv = (Adventure) getActivity();

		final View addCombatantView = adv.getLayoutInflater().inflate(R.layout.component_add_combatant, null);

		final InputMethodManager mgr = (InputMethodManager) adv.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (combatStarted)
			return;


		AlertDialog.Builder builder = new AlertDialog.Builder(adv);

		builder.setTitle("Add Enemy").setCancelable(false).setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mgr.hideSoftInputFromWindow(addCombatantView.getWindowToken(), 0);
				dialog.cancel();
			}
		});

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				mgr.hideSoftInputFromWindow(addCombatantView.getWindowToken(), 0);

				EditText enemySkillValue = (EditText) addCombatantView.findViewById(R.id.enemySkillValue);
				EditText enemyStaminaValue = (EditText) addCombatantView.findViewById(R.id.enemyStaminaValue);
				EditText handicapValue = (EditText) addCombatantView.findViewById(R.id.handicapValue);

				String skillS = enemySkillValue.getText().toString();
				String staminaS = enemyStaminaValue.getText().toString();
				Integer skill = null;
				Integer stamina = null;
				try {
					skill = Integer.valueOf(skillS);
					stamina = Integer.valueOf(staminaS);
				} catch (NumberFormatException e) {
					Adventure.showAlert("You must fill the skill and stamina values!", AdventureCombatFragment.this.getActivity());
					return;
				}
				Integer handicap = Integer.valueOf(handicapValue.getText().toString());

				addCombatant(rootView, skill, stamina, handicap, getDefaultEnemyDamage());

			}

		});

		AlertDialog alert = builder.create();

		EditText skillValue = (EditText) addCombatantView.findViewById(R.id.enemySkillValue);

		alert.setView(addCombatantView);

		mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		skillValue.requestFocus();

		alert.show();
	}

	protected void addCombatant(final View rootView, Integer skill, Integer stamina, Integer handicap, String damage) {

		Combatant combatPosition = new Combatant(stamina, skill, handicap, combatPositions.size() > 0, damage);
		if(!combatPositions.isEmpty())
			combatPosition.setDefenseOnly(true);
		combatPositions.add(combatPosition);
		combatantListAdapter.setCurrentEnemy(combatPosition);
		refreshScreensFromResume();

	}

	@Override
	public void refreshScreensFromResume() {
		combatantListAdapter.notifyDataSetChanged();
	}


	protected int getDamage() {
		return 2;
	}

	public class Combatant {

		Integer currentStamina;
		Integer currentSkill;
		Integer handicap;
		String damage;
		boolean defenseOnly;
		Integer staminaLoss = 0;

		public Combatant(Integer stamina, Integer skill, Integer handicap, boolean defenseOnly, String damage) {
			this.currentStamina = stamina;
			this.currentSkill = skill;
			this.handicap = handicap;
			this.defenseOnly = defenseOnly;
			this.damage = damage;
		}

		public Combatant(Integer stamina, Integer skill, Integer handicap, boolean defenseOnly) {
			this(stamina, skill, handicap, defenseOnly, "2");
		}

		public CharSequence toGridString() {
			return ("Skill:" + currentSkill + " Stamina:" + currentStamina);
		}

		public Integer getCurrentStamina() {
			return currentStamina;
		}

		public Integer getCurrentSkill() {
			return currentSkill;
		}

		public boolean isDefenseOnly() {
			return defenseOnly;
		}

		public void setCurrentStamina(Integer currentStamina) {
			this.currentStamina = currentStamina;
		}

		public void setDefenseOnly(boolean defenseOnly) {
			this.defenseOnly = defenseOnly;
		}

		public Integer getHandicap() {
			return handicap;
		}

		public String getDamage() {
			return damage;
		}

		public Integer getStaminaLoss() {
			return staminaLoss;
		}

		public void setStaminaLoss(Integer staminaLoss) {
			this.staminaLoss = staminaLoss;
		}

		public void setDamage(String damage) {
			this.damage = damage;
		}

		public void setCurrentSkill(Integer currentSkill) {
			this.currentSkill = currentSkill;
		}

		public void setHandicap(Integer handicap) {
			this.handicap = handicap;
		}

	}

	protected void resetCombat() {

		staminaLoss = 0;

		combatPositions.clear();
		combatMode = NORMAL;
		combatStarted = false;
		combatantListAdapter.setCurrentEnemy(null);

		combatTypeSwitch.setClickable(true);
		combatMode = combatTypeSwitch.isChecked() ? SEQUENCE : NORMAL;

		switchLayoutReset();

		refreshScreensFromResume();
	}

	protected void switchLayoutReset() {
		addCombatButton.setVisibility(View.VISIBLE);
		combatTypeSwitch.setVisibility(View.VISIBLE);
		startCombatButton.setVisibility(View.VISIBLE);
		resetButton.setVisibility(View.VISIBLE);
		testLuckButton.setVisibility(View.GONE);
		combatTurnButton.setVisibility(View.GONE);
		resetButton2.setVisibility(View.GONE);
	}

	protected String combatTypeSwitchBehaviour(boolean isChecked) {
		return combatMode = isChecked ? SEQUENCE : NORMAL;
	}

	private class CombatTypeSwitchChangeListener implements OnCheckedChangeListener {

		// @Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			combatTypeSwitchBehaviour(isChecked);

		}

	}

	protected static int convertDamageStringToInteger(String damage) {
		if (damage.equals("1D6")) {
			return DiceRoller.rollD6();
		} else {
			return Integer.parseInt(damage);
		}
	}

	protected void startCombat() {
		combatTurn();

		switchLayoutCombatStarted();
	}

	

	protected String getDefaultEnemyDamage() {
		return "2";
	}

	protected Boolean suddenDeath(DiceRoll diceRoll, DiceRoll enemyDiceRoll) {
		return null;
	}
	
	protected Combatant getCurrentEnemy(){
		return combatantListAdapter.getCurrentEnemy();
	}

}
