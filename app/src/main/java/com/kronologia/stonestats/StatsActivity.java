/*
Affichage des stats de victoire avec/sans pièce contre chaque classe, pour chaque deck

Consiste en un spinner + un tableau de scores récupérés depuis la DB
 */

package com.kronologia.stonestats;

import java.util.ArrayList;
import java.util.List;

import DeckDB.Deck;
import DeckDB.DeckDAO;
import GameDB.Game;
import GameDB.GameDAO;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class StatsActivity extends Activity {

	private Spinner deck_spinner;
	private TextView nbGamesTV;
	private TextView winrateTotalTV;
	private TextView winrateCoinTV;
	private TextView winrateNoCoinTV;

	private DeckDAO deckDbsource;
	private GameDAO gameDbsource;

	private int deckId = 0;
	private List<Game> games = new ArrayList<Game>();
	private List<Deck> decks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		AdView adView = (AdView) this.findViewById(R.id.adMob);

		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
				.addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
				.build();
		adView.loadAd(adRequest);

		deck_spinner = (Spinner) findViewById(R.id.deck_spinner);

		deckDbsource = new DeckDAO(getApplicationContext());
		deckDbsource.open();

		gameDbsource = new GameDAO(getApplicationContext());
		gameDbsource.open();

        //On récupère tous les decks pour les mettres dans le spinner
		decks = deckDbsource.getAllDecks();
		List<String> deckNames = new ArrayList<String>();

        //L'adapteur est fait grâce à "decks.getName()" pour travailler sur des Deck et pas des String
		for(Deck deck : decks) {
			deckNames.add(deck.getName());
		}

		ArrayAdapter<String> deckAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, deckNames);

		deckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		deck_spinner.setAdapter(deckAdapter);
		deck_spinner.setOnItemSelectedListener(deckSpinnerListener);


	}

	protected void onStop() {
		super.onStop();
		deckDbsource.close();
		gameDbsource.close();
	}

	protected void onRestart() {
		super.onRestart();
		deckDbsource.open();
		gameDbsource.open();
	}

    //Lors de la sélection d'un Deck dans la liste du spinner
	private OnItemSelectedListener deckSpinnerListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            //On travaille par ligne, une ligne = une classe (ou total)
			for(int classId = 0 ; classId < 10 ; classId++) {

                //Les identifiants des textView sont sous la forme texte+numero_classe
				String winrateTVResource = "total_winrate"+classId;
				String winrateCoinTVResource = "total_winrate_coin"+classId;
				String winrateNoCoinTVResource = "total_winrate_nocoin"+classId;
				String nbGamesTVResource = "total_nbgames"+classId;

                //On récupère les id des textView grâce aux noms des ressources recréés juste au dessus
				winrateTotalTV = (TextView) findViewById(getResources().getIdentifier(winrateTVResource, "id", getPackageName()));
				winrateCoinTV = (TextView) findViewById(getResources().getIdentifier(winrateCoinTVResource, "id", getPackageName()));
				winrateNoCoinTV = (TextView) findViewById(getResources().getIdentifier(winrateNoCoinTVResource, "id", getPackageName()));
				nbGamesTV = (TextView) findViewById(getResources().getIdentifier(nbGamesTVResource, "id", getPackageName()));

				//Récupération de l'd du deck sélectionné
                deckId = decks.get(pos).getId();

				//Log.d("StatsActivity", "deckId="+deckId);

                //Récupération de toutes les games avec l'idDeck sélectionné
				games = gameDbsource.getGamesWithDeck(deckId, classId);

                //Récupération des statistiques sur les game sélectionnées
				int[] winrates = gameDbsource.getWinrates(games);
                int nbGames = gameDbsource.getNbGames(games);

				int winrate = winrates[0];
				int winrateC = winrates[1];
				int winrateNC = winrates[2];

                //SetText direct du nombre de games
				nbGamesTV.setText(nbGames+"");

				//Log.d("StatsActivity/spinnerListener", winrate+" "+winrateC+" "+winrateNC);

                //Si getWinrates à renvoyé -1 sur une valeur, c'est que le nombre de games sur lequel
                //on travaille est de 0, on adapte le TextView en conséquence
				if(winrate != -1) {
					winrateTotalTV.setText(winrate+"%");
				} else {
					winrateTotalTV.setText(R.string.nan);
				}
				if(winrateC != -1) {
					winrateCoinTV.setText(winrateC+"%");
				} else {
					winrateCoinTV.setText(R.string.nan);
				}
				if(winrateNC != -1) {
					winrateNoCoinTV.setText(winrateNC+"%");
				} else {
					winrateNoCoinTV.setText(R.string.nan);
				}

			}
		}

		public void onNothingSelected(AdapterView<?> parent) { }

	};

}


