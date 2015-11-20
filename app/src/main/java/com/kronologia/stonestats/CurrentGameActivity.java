package com.kronologia.stonestats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import CardDB.Card;
import CardDB.CardDAO;
import CardGameDB.CardGameDAO;
import GameDB.Game;
import GameDB.GameDAO;


public class CurrentGameActivity extends Activity {

    private Button vic_button;
    private Button def_button;
    private Toast end_game_toast;
    private TextView nb_cards_view;
    private EditText research_card;
    private RadioGroup radioGroup;

    private ListView cardsList;
    private List<Card> cards;

    private CardDAO cardDbsource;
    private CardGameDAO cardgameDbsource;
    private GameDAO gameDbsource;

    private String end_game_string = "";
    private String own_class = "";
    private String opp_class = "";
    private String initText;

    ArrayAdapter<String> cardAdapter;

    private RadioGroup coin_group;

    //On ne limite pas à 30 cartes vues dans les cas où l'adversaire utilise des cartes ajoutant des cartes dans son deck
    private int[] cardsSeen = new int[45];
    private int indexCardsSeen = 0;

    boolean coin;
    int idDeck;
    int oppClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_game);

        AdView adView = (AdView) this.findViewById(R.id.adMob);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                .addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
                .build();
        adView.loadAd(adRequest);

        Bundle extra = getIntent().getExtras();
        idDeck = extra.getInt("idDeck");
        oppClassId = extra.getInt("oppClassId");
        coin = extra.getBoolean("coin");

        cardDbsource = new CardDAO(getApplicationContext());
        cardDbsource.open();

        cardgameDbsource = new CardGameDAO(getApplicationContext());
        cardgameDbsource.open();

        gameDbsource = new GameDAO(getApplicationContext());
        gameDbsource.open();

        cardsList = (ListView) findViewById(R.id.list_cards);
        vic_button = (Button) findViewById(R.id.victory_button);
        def_button = (Button) findViewById(R.id.defeat_button);
        research_card = (EditText) findViewById(R.id.research_edittext);
        radioGroup = (RadioGroup) findViewById(R.id.cost_group);
        nb_cards_view = (TextView) findViewById(R.id.cards_seen_textview);

        //Par défaut, on affiche les cartes qui coûtent 0 et on les met dans la liste
        cards = cardDbsource.getAllCardNames(0, oppClassId);
        List<String> values = new ArrayList<String>();

        for(Card card : cards) {
            values.add(card.getName());
        }

        //Radiogroup de coût des cartes
        radioGroup.setOnCheckedChangeListener(costGroupListener);

        cardAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, values);

        cardsList.setOnItemClickListener(listItemClickListener);
        cardsList.setAdapter(cardAdapter);

        initText = nb_cards_view.getText().toString();

        nb_cards_view.setText(initText + " : 0/30");
        nb_cards_view.invalidate();


        research_card.addTextChangedListener(searchWatcher);

        vic_button.setOnClickListener(vic_click);
        def_button.setOnClickListener(def_click);

    }

    protected void onStop() {
        super.onStop();
        cardgameDbsource.close();
        cardDbsource.close();
        gameDbsource.close();
    }

    protected void onRestart() {
        super.onRestart();
        cardgameDbsource.open();
        cardDbsource.open();
        gameDbsource.open();
    }

    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            cardAdapter.getFilter().filter(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //Button Victory click listener
    private View.OnClickListener vic_click = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            Log.i("CGActivity Int Receive", idDeck+" "+oppClassId+" "+coin);
            Log.i("AddGame parameters", coin+" "+idDeck+" "+oppClassId);
            AlertDialog.Builder builder = new AlertDialog.Builder(CurrentGameActivity.this);

            //On résume la partie et on demande à l'utilisateur s'il est ok pour finir

            if (coin) {
                end_game_string = "Victory with coin";
            } else {
                end_game_string = "Victory without coin";
            }

            builder.setMessage(end_game_string + " ?");
            builder.setTitle(R.string.are_you_sure);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    //Si l'utilisateur est ok, on ajoute la game dansla DB

                    end_game_toast = Toast.makeText(getApplicationContext(), end_game_string, Toast.LENGTH_SHORT);

                    Game game = GameDAO.createGame(coin, true, idDeck, oppClassId);
                    endGame(idDeck, game.getId());
                    end_game_toast.show();

                    Intent intent = new Intent(CurrentGameActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Rien à faire
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        }
    };

    //Button Defeat click listener (idem que vic_click juste au dessus sur le principe
    private View.OnClickListener def_click = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(CurrentGameActivity.this);

            if (coin) {
                end_game_string = "Defeat with coin";
            } else {
                end_game_string = "Defeat without coin";
            }

            builder.setMessage(end_game_string + " ?");
            builder.setTitle(R.string.are_you_sure);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    end_game_toast = Toast.makeText(getApplicationContext(), end_game_string, Toast.LENGTH_SHORT);
                    Game game = GameDAO.createGame(coin, false, idDeck, oppClassId);
                    endGame(idDeck, game.getId());
                    end_game_toast.show();

                    Intent intent = new Intent(CurrentGameActivity.this, MenuActivity.class);
                    startActivity(intent);

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

    //Cost radio group listener (même fonction que dans AddDeckActivity)
    private RadioGroup.OnCheckedChangeListener costGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int cost = 0;
            if (group.getCheckedRadioButtonId() == R.id.cost0) {
                cost = 0;
            } else if (group.getCheckedRadioButtonId() == R.id.cost1) {
                cost = 1;
            } else if (group.getCheckedRadioButtonId() == R.id.cost2) {
                cost = 2;
            } else if (group.getCheckedRadioButtonId() == R.id.cost3) {
                cost = 3;
            } else if (group.getCheckedRadioButtonId() == R.id.cost4) {
                cost = 4;
            } else if (group.getCheckedRadioButtonId() == R.id.cost5) {
                cost = 5;
            } else if (group.getCheckedRadioButtonId() == R.id.cost6) {
                cost = 6;
            } else if (group.getCheckedRadioButtonId() == R.id.cost7) {
                cost = 7;
            } else {
                cost = 8;
            }

            cards = cardDbsource.getAllCardNames(cost, oppClassId);
            List<String> values = new ArrayList<String>();

            for (Card card : cards) {
                values.add(card.getName());
            }

            cardAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, values);
            cardsList.setAdapter(cardAdapter);

            cardAdapter.getFilter().filter(research_card.getText());
        }
    };

    //Click listener on items of cardsList : ajout d'une CardGame dans la DB
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


            String cardName = (String) adapterView.getItemAtPosition(position);
            Card cardClicked = cardDbsource.getCardFromName(cardName);

            int idClick = cardClicked.getId();

            cardDbsource.addView(cardClicked.getName());

            Log.i(this.getClass().getSimpleName(), "Click on " + cardClicked.getName() + " (id " + idClick + ")");

            //Ajout de l'id de la carte ajoutée dans le tableau temporaire que l'on traitera en fin de game
            cardsSeen[indexCardsSeen] = idClick;
            indexCardsSeen++;

            nb_cards_view.setText(initText + " : " + indexCardsSeen + "/30");
            nb_cards_view.invalidate();
            //On informe l'user quand 30 cartes ont été vues
            if (indexCardsSeen == 30) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentGameActivity.this);


                builder.setMessage("30 cards seen");
                builder.setTitle("title");

                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }


    };

    //Ajout des CardGame temporaires dans la DB lors de la fin de partie
    private void endGame(int idDeck, int idGame) {
        for (int i = 0; i < 45; i++) {
            if (cardsSeen[i] != 0) {
//              Log.i("AddCG parameters", cardsSeen[i]+" "+idDeck+" "+idGame);
                cardgameDbsource.createCG(cardsSeen[i], idDeck, idGame);
                cardsSeen[i] = 0;
            }
        }


    }
}