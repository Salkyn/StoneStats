/*
Activité contenant des fonctions de tests qui seront éliminées au fur et à mesure du développement
Peut-être en intégrer quelques unes dans une activité de paramètrage

 */


package com.kronologia.stonestats;

import CardDB.CardDAO;
import CardDeckDB.CardDeckDAO;
import CardGameDB.CardGameDAO;
import DeckDB.DeckDAO;
import GameDB.GameDAO;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ParametersActivity extends Activity {

    private Button resetButton;
    private GameDAO gameDbHelper;
    private DeckDAO deckDbHelper;
    private CardDeckDAO carddeckDbHelper;
    private CardGameDAO cardgameDbHelper;
    private CardDAO cardDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        AdView adView = (AdView) this.findViewById(R.id.adMob);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                .addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
                .build();
        adView.loadAd(adRequest);

        resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(resetListener);

        carddeckDbHelper = new CardDeckDAO(getApplicationContext());
        carddeckDbHelper.open();

        cardgameDbHelper = new CardGameDAO(getApplicationContext());
        cardgameDbHelper.open();

        deckDbHelper = new DeckDAO(getApplicationContext());
        deckDbHelper.open();

        gameDbHelper = new GameDAO(getApplicationContext());
        gameDbHelper.open();

        cardDbHelper = new CardDAO(getApplicationContext());
        cardDbHelper.open();

    }

    protected void onStop() {
        super.onStop();
        carddeckDbHelper.close();
        cardgameDbHelper.close();
        cardDbHelper.close();
        deckDbHelper.close();
        gameDbHelper.close();
    }

    protected void onRestart() {
        super.onRestart();
        carddeckDbHelper.open();
        cardgameDbHelper.open();
        cardDbHelper.open();
        deckDbHelper.open();
        gameDbHelper.open();
    }

    //Reset des DB
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            deckDbHelper.deleteTout();
            gameDbHelper.deleteTout();
            carddeckDbHelper.deleteTout();
            cardgameDbHelper.deleteTout();
            cardDbHelper.resetAllViews();

            carddeckDbHelper.close();
            cardgameDbHelper.close();
            cardDbHelper.close();
            deckDbHelper.close();
            gameDbHelper.close();

            Intent intent = new Intent(ParametersActivity.this, MenuActivity.class);
            startActivity(intent);

            Toast toast = Toast.makeText(getApplicationContext(),"Reset all DBs", Toast.LENGTH_SHORT);
            toast.show();
        }

    };


}