package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

public class Apropos extends Activity {

    private MediaPlayer sonClick;
    private boolean playSon=true;
    private boolean playMusique=true;
    private boolean premierDemarrage;

    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_propos);
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        PrincipaleActivite.changerLangue(this,parametres.getString("langue","fr"));
        playSon = parametres.getBoolean("son",true);
        playMusique = parametres.getBoolean("musique",true);

        premierDemarrage = parametres.getBoolean("premierDemarrage",true);
        editeur = parametres.edit();
        this.sonClick = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn);
        sonClick.setVolume(0.1f,0.5f);
        ImageButton btR = this.findViewById(R.id.btRetourAp);
        btR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playSon) sonClick.start();

                if(premierDemarrage) {
                    Intent i = new Intent(Apropos.this, PrincipaleActivite.class);
                    startActivity(i);
                    editeur.putBoolean("premierDemarrage",false);
                    editeur.apply();
                }
                finish();
            }
        });

    }
    @Override
    protected void onPause(){
        super.onPause();
        if(PrincipaleActivite.musiqueFond !=null && PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.pause();
    }
    protected void onResume(){
        super.onResume();
        if(PrincipaleActivite.musiqueFond !=null && !PrincipaleActivite.musiqueFond.isPlaying() && playMusique) PrincipaleActivite.musiqueFond.start();
        PrincipaleActivite.changerLangue(this,parametres.getString("langue","fr"));
    }

    // -----------------------------------------------------------------------------------------------------------
}