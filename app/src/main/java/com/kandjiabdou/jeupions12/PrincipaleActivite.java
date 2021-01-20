package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.Locale;

public class PrincipaleActivite extends Activity{

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerApp;
    public static MediaPlayer musiqueFond;
    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur ;
    private boolean versAcitvite = false;
    private boolean playSon=true;
    protected static int nbSonCapture;
    protected static int nbSonPerdu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principale_activite);
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        editeur= parametres.edit();
        PrincipaleActivite.changerLangue(this ,parametres.getString("langue","fr"));
        Button btCommencer = findViewById(R.id.btCommencer);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn);
        mediaPlayer.setVolume(0.1f,0.5f);

        musiqueFond = MediaPlayer.create(getApplicationContext(), R.raw.musique_de_fond);
        musiqueFond.setVolume(0.1f,0.5f);
        musiqueFond.setLooping(true);
        musiqueFond.start();

        editeur.putBoolean("musique",true);
        editeur.putBoolean("son",true);
        editeur.commit();

        mediaPlayerApp = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jr);
        mediaPlayerApp.setVolume(0.1f,0.5f);
        btCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versAcitvite =true;
                if(playSon) mediaPlayer.start();
                Intent activiteNouvellePartie= new Intent(PrincipaleActivite.this, ActiviteNouvellePartie.class);
                startActivity(activiteNouvellePartie);
            }
        });
        // set up button listeners
        Button btAbout = findViewById(R.id.btAbout);
        btAbout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                versAcitvite =true;
                if(playSon) mediaPlayerApp.start();
                Intent i = new Intent(PrincipaleActivite.this, Apropos.class);
                startActivity(i);
            }
        });
        nbSonCapture=0;
        nbSonPerdu=0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        versAcitvite =false;
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        PrincipaleActivite.changerLangue(this ,parametres.getString("langue","fr"));
        playSon = parametres.getBoolean("son",true);
        boolean playMusique = parametres.getBoolean("musique", true);
        editeur = parametres.edit();

        if(musiqueFond!=null){
            if(!musiqueFond.isPlaying()){
               if(playMusique) musiqueFond.start();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }


    @Override
    protected void onStop(){
        super.onStop();
        if(!versAcitvite){
            if(musiqueFond!=null){
                if(musiqueFond.isPlaying()){
                    musiqueFond.pause();
                }
            }
        }
    }
    protected static void changerLangue(Activity activity, String langue){
        Locale locale = new Locale(langue);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
    }
}