package pt.joaomneto.titancompanion.adventure.impl.fragments.strider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pt.joaomneto.titancompanion.R;
import pt.joaomneto.titancompanion.adventure.impl.HOHAdventure;
import pt.joaomneto.titancompanion.adventure.impl.fragments.AdventureVitalStatsFragment;

public class STRIDERAdventureVitalStatsFragment extends AdventureVitalStatsFragment {
//TODO
	TextView fearValue = null;

	Button increaseFearButton = null;

	Button decreaseFearButton = null;

	public STRIDERAdventureVitalStatsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(
				R.layout.fragment_10hoh_adventure_vitalstats, container, false);

		initialize(rootView);

		decreaseFearButton = (Button) rootView
				.findViewById(R.id.minusFearButton);
		increaseFearButton = (Button) rootView
				.findViewById(R.id.plusFearButton);
		fearValue = (TextView) rootView.findViewById(R.id.statsFearValue);
		final HOHAdventure adv = (HOHAdventure) getActivity();

		decreaseFearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (adv.getCurrentFear() > 0)
					adv.setCurrentFear(adv.getCurrentFear() - 1);
				refreshScreensFromResume();

			}
		});

		increaseFearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (adv.getCurrentFear() < adv.getMaximumFear())
					adv.setCurrentFear(adv.getCurrentFear() + 1);
				refreshScreensFromResume();

			}
		});

		refreshScreensFromResume();

		return rootView;
	}

	@Override
	public void refreshScreensFromResume() {
		super.refreshScreensFromResume();
		HOHAdventure adv = (HOHAdventure) getActivity();
		fearValue.setText("" + adv.getCurrentFear());

	}

}
