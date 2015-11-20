/*
Gère la mise à jour de la base de données de cartes grâce à une préférence partagée contenant le
numéro de version

Redirige vers MenuActivity une fois la maj faite, ou directement après un lancement non-postMAJ
*/

package com.kronologia.stonestats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import CardDB.CardDAO;
import CardDB.JSONParser;


public class LaunchActivity extends Activity {

    // NUMVERSION à changer lors de la maj du fichier .json contenant les données de cartes (lors de
    // maj Hearthstone ou changement sur les cartes
    public static final String NUMVERSION = "0.5";
    public boolean UPDATE = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //Détection du type de lancement grâce à settings
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //Premier lancement de l'appli depuis ce terminal
        if (settings.getString("app_version", "00").equals("00")) {

            Log.d("Comments", "First launch");

            //Lancement de la AsyncTask permettant la création de la DBCards
            new RetrieveData(getApplicationContext()).execute();

            //MAJ du numéro de version dans la préférence
            settings.edit().putString("app_version", NUMVERSION).commit();

            //Premier lancement de l'appli depuis une maj du numéro de version
        } else if (!settings.getString("app_version", "00").equals(NUMVERSION)) {
            Log.d("Comments", "First launch since update");
            UPDATE = true;

            new RetrieveData(getApplicationContext()).execute();

            settings.edit().putString("app_version", NUMVERSION).commit();

            //Lancement normal, juste une redirection
        } else {
            Log.d("Comments", "Normal launch");
            Intent intent = new Intent(LaunchActivity.this, MenuActivity.class);
            startActivity(intent);
        }




    }

    //Tache asyncrone pour ne pas bloquer le termnal pendant le JSON parsing
    class RetrieveData extends AsyncTask<String, String, String> {

        private Context context;

        //On récupère uste le context pour initialiser la DB
        public RetrieveData(Context c) {
            context = c;
        }

        private CardDAO cardDatabase;

        protected String doInBackground(String... arg0) {

            cardDatabase = new CardDAO(context);
            cardDatabase.open();

            //Normalement la base est vide si on arrive ici, mais utile lors des tests
            cardDatabase.deleteTout();

            //Utilisation de la class JSONParser pour faire File -> JSONObject
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromFile();

            //Recuperation des cartes "Classic"
            int nbClassic = addCards("Classic", json, cardDatabase);
            Log.w("ParametersActivity", nbClassic + " cards added from Classic");

            int nbBRM = addCards("Blackrock Mountain", json, cardDatabase);
            Log.w("ParametersActivity", nbBRM + " cards added from BRM");

            int nbBasic = addCards("Basic", json, cardDatabase);
            Log.w("ParametersActivity", nbBasic + " cards added from Basic");

            int nbNaxx = addCards("Curse of Naxxramas", json, cardDatabase);
            Log.w("ParametersActivity", nbNaxx + " cards added from Naxx");

            int nbGvG = addCards("Goblins vs Gnomes", json, cardDatabase);
            Log.w("ParametersActivity", nbGvG + " cards added from GvG");

            int nbReward = addCards("Reward", json, cardDatabase);
            Log.w("ParametersActivity", nbReward + " cards added from Reward");

            int nbPromote = addCards("Promotion", json, cardDatabase);
            Log.w("ParametersActivity", nbPromote + " cards added from Promote");

            int nbTGT = addCards("The Grand Tournament", json, cardDatabase);
            Log.w("ParametersActivity", nbTGT + " cards added from TGT");

            return null;
        }

        //Une fois le JSONObject lu, on redirige vers le menu et ferme la DB
        protected void onPostExecute(String ab) {

            Intent intent = new Intent(LaunchActivity.this, MenuActivity.class);
            startActivity(intent);
            cardDatabase.close();
        }

    }

    // Permet la conversion du nom de classe en string pour faire le lien avec la DB
    public int classToInt(String classString) {

        int nbClass = 0;

        if(classString.equals("")) { nbClass = 0; }
        if(classString.equals("Paladin")) { nbClass = 1; }
        if(classString.equals("Warrior")) { nbClass = 2; }
        if(classString.equals("Hunter")) { nbClass = 3; }
        if(classString.equals("Shaman")) { nbClass = 4; }
        if(classString.equals("Druid")) { nbClass = 5; }
        if(classString.equals("Rogue")) { nbClass = 6; }
        if(classString.equals("Priest")) { nbClass = 7; }
        if(classString.equals("Warlock")) { nbClass = 8; }
        if(classString.equals("Mage" )) { nbClass = 9; }

        return nbClass;
    }

    //Ajout des cartes de la catégorie category du json dans cardDatabase, permet de fragmenter
    //la reconnaissance des cartes
    //Renvoie le nombre de cartes ajoutées en Logcat pour le débg
    public int addCards(String category, JSONObject json, CardDAO cardDatabase) {

        int nbCardsAdded = 0;

        String type, name;
        int cost, attack, health, cardClass;

        try {
            //Récupération de l'Array souhaité
            JSONArray dataJsonArr = json.getJSONArray(category);

            //Pour chaque élément = carte de l'array
            for (int i = 0; i < dataJsonArr.length(); i++) {

                JSONObject c = dataJsonArr.getJSONObject(i);

                //On récupère le type pour savoir comment traiter la carte
                type = c.getString("type");

                //Les types testés correspondent aux seuls types utilisés pour des cartes "réelles",
                //permet d'éliminer toutes les cartes de test
                if ((type.equals("Minion") || type.equals("Spell") || type.equals("Weapon"))
                        && c.has("cost") && c.has("collectible")) {

                    name = c.getString("name");
                    cost = Integer.parseInt(c.getString("cost"));

                    //Si minion, on récupère attack/health
                    if (type.equals("Minion")) {
                        attack = Integer.parseInt(c.getString("attack"));
                        health = Integer.parseInt(c.getString("health"));
                        //Si arme, on récupère attack/durability
                    } else if (type.equals("Weapon")) {
                        attack = Integer.parseInt(c.getString("attack"));
                        health = Integer.parseInt(c.getString("durability"));
                        //Sinon, c'est un spell, on règle les valeurs à 0 par défaut
                        // TODO Ajouter un type pour les cartes pour différencier les spells (pas d'affichage "0/0")
                        // permetterat aussi de gérer les légendaires à un exemplaire par deck
                    } else {
                        attack = 0;
                        health = 0;
                    }

                    //Réglage de la classe de la carte, mis à 0 si accessible par toutes les classes
                    if (c.has("playerClass")) {
                        cardClass = classToInt(c.getString("playerClass"));
                    } else {
                        cardClass = 0;
                    }

                    //Ajout de la carte à la db
                    cardDatabase.addCard(name, cost, attack, health, cardClass);
                    //Log.d("ParametersActivity", "New card : " + newCard.getName() + " (" + classToString(cardClass) + ")");
                    nbCardsAdded++;
                }
                else { //Log.d("Test activity", "Card is a " + type + ", not added");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nbCardsAdded;
    }
}