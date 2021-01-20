package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class PopupLangue extends Dialog{
    private boolean playSon;
    private SharedPreferences.Editor editeur ;
    private TextView btFrancais;
    private TextView btAnglais;
    private MediaPlayer son1;
    private MediaPlayer son2;
    public Activity activity;
    public PopupLangue(final Activity activity) {
        super(activity, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.popup_langue);
        this.activity=activity;
        SharedPreferences parametres = PreferenceManager.getDefaultSharedPreferences(activity);
        editeur= parametres.edit();
        this.son1 = MediaPlayer.create(activity, R.raw.clic_btn_jr);
        son1.setVolume(0.1f,0.5f);
        this.son2 = MediaPlayer.create(activity, R.raw.clic_btn_jn);
        son2.setVolume(0.1f,0.5f);
        playSon = parametres.getBoolean("son", true);
        this.btFrancais=findViewById(R.id.btLangueFrancais);
        this.btAnglais=findViewById(R.id.btLangueAnglais);

        String langue = parametres.getString("langue","fr");
        if (langue.equals("fr")) {
            btFrancais.setBackgroundResource(R.color.marron_foncee);
            btAnglais.setBackgroundResource(R.color.marron_claire);
        } else if (langue.equals("en")) {
            btFrancais.setBackgroundResource(R.color.marron_claire);
            btAnglais.setBackgroundResource(R.color.marron_foncee);
        }

        btFrancais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(son2.isPlaying()) son2.stop();
                if(playSon) son2.start();
                btFrancais.setBackgroundResource(R.color.marron_foncee);
                btAnglais.setBackgroundResource(R.color.marron_claire);
                editeur.putString("langue","fr");
                editeur.commit();
                Toast.makeText(activity,activity.getString(R.string.appliquerLangue),Toast.LENGTH_LONG).show();
                cancel();
            }
        });
        btAnglais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(son1.isPlaying()) son1.stop();
                if(playSon) son1.start();
                btFrancais.setBackgroundResource(R.color.marron_claire);
                btAnglais.setBackgroundResource(R.color.marron_foncee);
                editeur.putString("langue","en");
                editeur.commit();
                Toast.makeText(activity,activity.getString(R.string.appliquerLangue),Toast.LENGTH_LONG).show();
                cancel();
            }
        });

    }
}
