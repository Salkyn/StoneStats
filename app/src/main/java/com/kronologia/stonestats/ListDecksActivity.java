/*
Contient la liste des decks présents dans la DBDecks et permet d'accéder à leur modification, ainsi
qu'à la création de nouveaux decks
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ListDecksActivity extends Activity {

    private DeckDAO datasource;
    private Button add_button;
    private EditText deck_name;
    private ListView deckList;
    private int classDeck;
    private List<Deck> decks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks_list);

        AdView adView = (AdView) this.findViewById(R.id.adMob);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                .addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
                .build();
        adView.loadAd(adRequest);

        //Spinner du choix de classe (création de deck)
        Spinner choose_class_spinner = (Spinner) findViewById(R.id.class_spinner);
        choose_class_spinner.setOnItemSelectedListener(class_spinner_listener);

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.classes_array, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        choose_class_spinner.setAdapter(spinAdapter);


        //Liste des decks existants
        deckList = (ListView) findViewById(R.id.deck_list);

        datasource = new DeckDAO(getApplicationContext());
        datasource.open();

        decks = datasource.getAllDecks();
        List<String> values = new ArrayList<String>();

        for(Deck deck : decks) {
            values.add(deck.getName() + " (" + Deck.classToString(deck.getDeckClass()) + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        deckList.setAdapter(adapter);

        deckList.setOnItemClickListener(listItemClickListener);



        add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(add_click);

        deck_name = (EditText) findViewById(R.id.deck_name);


        datasource = new DeckDAO(this);
        datasource.open();

    }

    protected void onStop() {
        super.onStop();
        datasource.close();
    }

    protected void onRestart() {
        super.onRestart();
        datasource.open();
    }

    //Spinner click listener pour connâitre l'id de de la classe du deck créé
    private OnItemSelectedListener class_spinner_listener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
                                   long id) {
            Log.w("Id de la classe choisie", pos+"");
            classDeck = pos+1;

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    };


    //Click listener sur le bouton d'ajout de deck : ajout du deck et intent vers adddeckactivity
    private OnClickListener add_click = new OnClickListener() {

        @Override
        public void onClick(View v) {

            //Ajout du deck
            String deckName = deck_name.getText().toString();;
            Deck deck = datasource.createDeck(classDeck, deckName);
            decks.add(deck);

            //Mise à jour de la liste (si retour dans cette activité)
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) deckList.getAdapter();
            adapter.add(deck.getName() + " (" + Deck.classToString(deck.getDeckClass()) + ")");
            adapter.notifyDataSetChanged();

            //On passe dans AddDeckActivity en envoyant l'id du deck étudié
            Intent intent = new Intent(ListDecksActivity.this, AddDeckActivity.class);
            intent.putExtra("deckId", deck.getId());
            intent.putExtra("classId", deck.getDeckClass());

            startActivity(intent);
        }
    };


    //Click listener sur un deck de la liste, pour y faire des modifs
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            final Deck deckClicked = decks.get(position);

            Intent intent = new Intent(ListDecksActivity.this, AddDeckActivity.class);
            intent.putExtra("deckId", deckClicked.getId());
            intent.putExtra("deckClass", deckClicked.getDeckClass());
            intent.putExtra("deckName", deckClicked.getName());

            startActivity(intent);

            //TODO Code pour supprimer un deck, trouver où le mettre dans l'interface
        //Delete deck dialog
          /*  AlertDialog.Builder builder = new AlertDialog.Builder(ListDecksActivity.this);

            builder.setMessage(R.string.delete_deck);
            builder.setTitle(R.string.confirmation);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    datasource.deleteDeck(idClick);
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) deckList.getAdapter();

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show(); */

        }



    };
}
