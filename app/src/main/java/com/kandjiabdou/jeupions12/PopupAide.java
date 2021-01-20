package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

public class PopupAide extends Dialog {
    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur;
    private boolean premierJeu;
    public PopupAide(Activity activity) {

        super(activity, R.style.Theme_AppCompat_Dialog);
        parametres= PreferenceManager.getDefaultSharedPreferences(activity);
        editeur = parametres.edit();

        premierJeu = parametres.getBoolean("premierJue",true);

        setContentView(R.layout.popup_aide);
        ImageView btRetour = findViewById(R.id.btRetourPpAide);
        btRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(premierJeu){
                    editeur.putBoolean("premierJeu",false);
                    editeur.apply();
                }
                cancel();
            }
        });
    }

}
