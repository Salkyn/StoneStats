/*
Actvité principale gérant lesuivi d'une partie : choix du deck et des autres paramètres et ajout
des cartes adverses au fur et à mesure de la partie (ajout d'éléments dans CardGame DB)
 */

package com.kronologia.stonestats;

import java.util.ArrayList;
import java.util.List;

import DeckDB.Deck;
import DeckDB.DeckDAO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

    private Spinner own_spinner;
    private Spinner opp_spinner;
    private RadioGroup coin_group;
    private Button add_opp_cards_button;

    private String own_class = "";
    private String opp_class = "";
    private int idDeck = 0;
    private int oppClassId = 0;

    private DeckDAO deckDbsource;
    private List<Deck> decks;

    private int nbDecks = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView adView = (AdView) this.findViewById(R.id.adMob);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                .addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
                .build();
        adView.loadAd(adRequest);

        //Spinner sur le choix de son deck
        own_spinner = (Spinner) findViewById(R.id.own_class_spinner);

        //Spinner sur la classe de l'adversaire
        opp_spinner = (Spinner) findViewById(R.id.opp_class_spinner);

        add_opp_cards_button = (Button) findViewById(R.id.next_act_button);
        add_opp_cards_button.setOnClickListener(oppcardsButtonListener);

        //Ouverture de toutes les DB utiles dans cette act
        deckDbsource = new DeckDAO(getApplicationContext());
        deckDbsource.open();




        //Gestion du spinner de decks
        decks = deckDbsource.getAllDecks();
        List<String> deckNames = new ArrayList<String>();

        for(Deck deck : decks) {
            deckNames.add(deck.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, deckNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        own_spinner.setAdapter(dataAdapter);

        //Gestion du spinner (fixe) de classes pour l'adversaire
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.classes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opp_spinner.setAdapter(adapter);

        //Fin mise en place interface

        coin_group = (RadioGroup) findViewById(R.id.coin_group);


        own_spinner.setOnItemSelectedListener(own_spinner_listener);
        opp_spinner.setOnItemSelectedListener(opp_spinner_listener);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //On renvoie l'utilisateur dans le menu s'il veut faire une game sans avoir créé de deck
        //TODO Changer la fonction du bouton retour pour pas que l'user puisse arriver dans cette act sans avoir créé de deck
        nbDecks = decks.size();
        if(nbDecks == 0) {
            Toast no_deck_toast = Toast.makeText(getApplicationContext(), R.string.no_deck_alert, Toast.LENGTH_LONG);
            no_deck_toast.show();
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }
    }

    protected void onStop() {
        super.onStop();
        deckDbsource.close();
    }

    protected void onRestart() {
        super.onRestart();
        deckDbsource.open();
    }

    private OnClickListener oppcardsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(MainActivity.this, CurrentGameActivity.class);

            boolean coin = (coin_group.getCheckedRadioButtonId() == R.id.coin);
            intent.putExtra("idDeck", idDeck);
            intent.putExtra("oppClassId", oppClassId);
            intent.putExtra("coin", coin);

            Log.i("MainActivity Int Sent", idDeck+" "+oppClassId+" "+coin);

            startActivity(intent);
        }
    };

    //Choose deck spinner listener
    private OnItemSelectedListener own_spinner_listener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            //On récupère juste l'id du deck sélectionné
            idDeck = decks.get(pos).getId();
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //Choose opponent's class listener
    private OnItemSelectedListener opp_spinner_listener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            //L'id de la classe de l'adversaire correspond à la position de l'item dans le spinner +1

            opp_class = parent.getSelectedItem().toString();
            oppClassId = pos+1;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


}

