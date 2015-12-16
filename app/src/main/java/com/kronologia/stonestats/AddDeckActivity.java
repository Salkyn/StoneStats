/*
Gestion des CardDeck : ajout et retrait des cartes présentes dans un nouveau deck où modif d'un deck existant
 */

package com.kronologia.stonestats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import CardDB.Card;
import CardDB.CardDAO;
import CardDeckDB.CardDeck;
import CardDeckDB.CardDeckDAO;

public class AddDeckActivity extends Activity {

    private ListView allCardsList;
    private ListView deckCardsList;
    private Button validateButton;
    private EditText research_card;

    private CardDAO cardDbsource;
    private CardDeckDAO carddeckDbsource;

    private int deckId;
    private int classId;

    private List<Card> cards;
    private List<Card> deckCards;
    private List<String> deckCardsValues;
    private ArrayAdapter<String> deckCardsAdapter;
    private ArrayAdapter<String> cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck);


        AdView adView = (AdView) this.findViewById(R.id.adMob);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                .addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
                .build();
        adView.loadAd(adRequest);

        //On arrive forcément dans cette act depuis ListDecksAct, on récupère l'id du deck étudié dans l'intent
        Bundle extra = getIntent().getExtras();
        deckId = extra.getInt("deckId");
        classId = extra.getInt("classId");

        cardDbsource = new CardDAO(getApplicationContext());
        cardDbsource.open();

        carddeckDbsource = new CardDeckDAO(getApplicationContext());
        carddeckDbsource.open();

        //Toutes les cartes de la DB
        deckCards = new ArrayList<Card>();
        deckCardsValues = new ArrayList<String>();

        //Cartes présentes dans la decklist
        deckCardsList = (ListView) findViewById(R.id.list_deck_cards);
        allCardsList = (ListView) findViewById(R.id.list_all_cards);
        research_card = (EditText) findViewById(R.id.research_edittext);
        validateButton = (Button) findViewById(R.id.validate_button);

        //On récupère les cartes dejà présentes dans le deck(si nouveau deck, values = null
        List<CardDeck> intermediateValues = carddeckDbsource.getCDFromDeck(deckId);

            for (CardDeck cd : intermediateValues) {
                Card card = cardDbsource.getCardFromId(cd.getIdCard());
                deckCards.add(card);
                deckCardsValues.add(card.getName());
            }

        //Par défaut, on affiche les cartes qui coûtent 0 et on les met dans la liste
        cards = cardDbsource.getAllCardNames(0, classId);
        List<String> values = new ArrayList<String>();

        for(Card card : cards) {
            values.add(card.getName());
        }

        //Radiogroup de coût des cartes
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.cost_group);
        radioGroup.setOnCheckedChangeListener(costGroupListener);

        //Listeners et adapteurs sur chaque liste de cartes
        allCardsList.setOnItemClickListener(listItemClickListener);
        cardAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, values);
        allCardsList.setAdapter(cardAdapter);

        deckCardsList.setOnItemClickListener(deckListItemClickListener);
        deckCardsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                deckCardsValues);
        deckCardsList.setAdapter(deckCardsAdapter);

        research_card.addTextChangedListener(searchWatcher);

        validateButton.setOnClickListener(validateButtonListener);

    }

    protected void onStop() {
        super.onStop();
        carddeckDbsource.close();
        cardDbsource.close();
    }

    protected void onRestart() {
        super.onRestart();
        carddeckDbsource.open();
        cardDbsource.open();
    }

    private View.OnClickListener validateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(AddDeckActivity.this, MenuActivity.class);
            startActivity(intent);
        }
    };

    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            deckCardsAdapter.getFilter().filter(charSequence.toString());
            cardAdapter.getFilter().filter(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //Cost radio group listener
    private RadioGroup.OnCheckedChangeListener costGroupListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            int cost;

            //On associe chaque radiobutton à l'int qui correspond au coût demandé

            if(group.getCheckedRadioButtonId() == R.id.cost0) {
                cost = 0;
            } else if(group.getCheckedRadioButtonId() == R.id.cost1) {
                cost = 1;
            } else if(group.getCheckedRadioButtonId() == R.id.cost2) {
                cost = 2;
            } else if(group.getCheckedRadioButtonId() == R.id.cost3) {
                cost = 3;
            } else if(group.getCheckedRadioButtonId() == R.id.cost4) {
                cost = 4;
            } else if(group.getCheckedRadioButtonId() == R.id.cost5) {
                cost = 5;
            } else if(group.getCheckedRadioButtonId() == R.id.cost6) {
                cost = 6;
            } else if(group.getCheckedRadioButtonId() == R.id.cost7) {
                cost = 7;
            } else  {
                cost = 8;
            }

            //Récupération et affichage des cartes au bon coût
            cards = cardDbsource.getAllCardNames(cost, classId);
            List<String> values = new ArrayList<String>();

            for(Card card : cards) {
                values.add(card.getName());
            }

            ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, values);
            allCardsList.setAdapter(cardAdapter);

           cardAdapter.getFilter().filter(research_card.getText());

        }
    };

    //Click listener sur item de cardsList (ajout de carte dans le deck)
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            //On va ajouter la carte sur laquelle on a cliqué dans la CardDeck DB
            String cardName = (String) adapterView.getItemAtPosition(position);
            Card newCardAdded = cardDbsource.getCardFromName(cardName);

            Log.i("AddCardToDeck", newCardAdded.getName());

            carddeckDbsource.createCD(newCardAdded.getId(), deckId);

            //On ajoute la nouvelle carte à l'adapteur et on met à jour l'affichage
            deckCards.add(newCardAdded);
            deckCardsValues.add(newCardAdded.getName());

            deckCardsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, deckCardsValues);
            deckCardsAdapter.getFilter().filter(research_card.getText());

            deckCardsList.setAdapter(deckCardsAdapter);

            deckCardsAdapter.notifyDataSetChanged();

        }

    };

    //Click listener sur item de cardsList (enlevage de carte dans le deck)
    private AdapterView.OnItemClickListener deckListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            //On va enlever la carte sur laquelle on a cliqué de la CardDeck DB
            String cardName = (String) adapterView.getItemAtPosition(position);
            Card cardRemoved = cardDbsource.getCardFromName(cardName);

            Log.i("RemoveCardFromDeck", cardRemoved.getName());

            carddeckDbsource.removeCD(cardRemoved.getId(), deckId);

            //On enlève la nouvelle carte de l'adapteur et on met à jour l'affichage
            deckCards.remove(position);
            deckCardsValues.remove(position);

            deckCardsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, deckCardsValues);

            deckCardsList.setAdapter(deckCardsAdapter);

            deckCardsAdapter.notifyDataSetChanged();
            deckCardsAdapter.getFilter().filter(research_card.getText());
        }


    };
}
