package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;


public class Demarrage extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demarrage);
        SharedPreferences parametres = PreferenceManager.getDefaultSharedPreferences(this);
        boolean premierDemarrage = parametres.getBoolean("premierDemarrage",true);

        if(premierDemarrage) {
            Runnable runnable1 = new Runnable() {
                public void run() {
                    Intent i = new Intent(Demarrage.this, Apropos.class);
                    startActivity(i);
                    finish(); }
            };
            new Handler().postDelayed(runnable1,5000);
        } else {
            Runnable runnable2 = new Runnable() {
                public void run() {
                    Intent i = new Intent(Demarrage.this, PrincipaleActivite.class);
                    startActivity(i);
                    finish(); }
            };
            new Handler().postDelayed(runnable2,4000);
        }

    }
}

/*


*/