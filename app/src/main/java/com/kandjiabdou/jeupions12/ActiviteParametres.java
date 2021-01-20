package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class ActiviteParametres extends Activity implements View.OnClickListener {
    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur ;
    private MediaPlayer sonClick;
    private MediaPlayer son1;
    private MediaPlayer son2;
    private MediaPlayer son3;
    private MediaPlayer son4;
    private MediaPlayer son5;
    private MediaPlayer son6;

    private boolean playMusic=true;
    private boolean playSon=true;

    private ImageButton btCroixMusique;
    private ImageButton btCroixSon;

    private LinearLayout[] lesLinearLayoutsPlt=new LinearLayout[3];
    private LinearLayout[] lesLinearLayoutsPion=new LinearLayout[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_parametres);

        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        editeur= parametres.edit();

        ImageButton btMusique = findViewById(R.id.btMusique);
        ImageButton btSon = findViewById(R.id.btBruitage);
        btCroixMusique = findViewById(R.id.croixMusic);
        btCroixSon = findViewById(R.id.croixSon);
        ImageButton btRetour = findViewById(R.id.btRetourP);
        TextView btLanguage = findViewById(R.id.btLangue);
        ImageView btPltClassique = findViewById(R.id.btPlateuClassique);
        ImageView btPltBois = findViewById(R.id.btPlateuBois);
        ImageView btPltBlanc = findViewById(R.id.btPlateuBlanc);
        ImageView btPions1 = findViewById(R.id.btThemePion1);
        ImageView btPions2 = findViewById(R.id.btThemePion2);
        ImageView btPions3 = findViewById(R.id.btThemePion3);

        btMusique.setOnClickListener(this);
        btSon.setOnClickListener(this);
        btCroixMusique.setOnClickListener(this);
        btCroixSon.setOnClickListener(this);
        btRetour.setOnClickListener(this);
        btLanguage.setOnClickListener(this);
        btPltClassique.setOnClickListener(this);
        btPltBlanc.setOnClickListener(this);
        btPltBois.setOnClickListener(this);
        btPions1.setOnClickListener(this);
        btPions2.setOnClickListener(this);
        btPions3.setOnClickListener(this);

        initLayout();
    }
    private void initMedia(){
        this.sonClick = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn);
        sonClick.setVolume(0.1f,0.5f);
        this.son1 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jr);
        son1.setVolume(0.1f,0.5f);
        this.son2 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jn);
        son2.setVolume(0.1f,0.5f);
        this.son3 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_on);
        son3.setVolume(0.1f,0.5f);
        this.son4 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_or);
        son4.setVolume(0.1f,0.5f);
        this.son5 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jouer);
        son5.setVolume(0.1f,0.5f);
        this.son6 = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn);
        son6.setVolume(0.1f,0.5f);
    }
    @Override
    protected void onResume(){
        super.onResume();
        initMedia();
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        PrincipaleActivite.changerLangue(this,parametres.getString("langue","fr"));
        editeur = parametres.edit();
        playMusic = parametres.getBoolean("musique",true);
        if(playMusic)
            btCroixMusique.setVisibility(ImageButton.GONE);
        else
            btCroixMusique.setVisibility(ImageButton.VISIBLE);
        playSon = parametres.getBoolean("son",true);
        if(playSon)
            btCroixSon.setVisibility(ImageButton.GONE);
        else
            btCroixSon.setVisibility(ImageButton.VISIBLE);
        int plateau = parametres.getInt("plateau",1);
        setCouleurLayout(plateau-1);
        int pion = parametres.getInt("pion",1);
        setCouleurLayout(pion+3);
        if(!PrincipaleActivite.musiqueFond.isPlaying() && playMusic) PrincipaleActivite.musiqueFond.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.pause();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btMusique:
                if(sonClick.isPlaying()) sonClick.stop();
                if(playSon) sonClick.start();
                if(playMusic){
                    if(PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.pause();
                    btCroixMusique.setVisibility(ImageButton.VISIBLE);
                    playMusic=false;
                } else{
                    playMusic=true;
                }
                editeur.putBoolean("musique",playMusic);
                break;
            case R.id.btBruitage:
                if(playSon){
                    btCroixSon.setVisibility(ImageButton.VISIBLE);
                    playSon=false;
                } else{
                    if(sonClick.isPlaying()) sonClick.stop();
                    sonClick.start();
                    btCroixSon.setVisibility(ImageButton.GONE);
                    playSon=true;
                }
                editeur.putBoolean("son",playSon);
                break;
            case R.id.croixMusic:
                if(playMusic){
                    btCroixMusique.setVisibility(ImageButton.VISIBLE);
                    playMusic=false;
                } else{
                    if(!PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.start();
                    if(sonClick.isPlaying()) sonClick.stop();
                    if(playSon) sonClick.start();
                    btCroixMusique.setVisibility(ImageButton.GONE);
                    playMusic=true;
                }
                editeur.putBoolean("musique",playMusic);
                break;
            case R.id.croixSon:
                if(playSon){
                    btCroixSon.setVisibility(ImageButton.VISIBLE);
                    playSon=false;
                } else{
                    if(sonClick.isPlaying()) sonClick.stop();
                    sonClick.start();
                    btCroixSon.setVisibility(ImageButton.GONE);
                    playSon=true;
                }
                editeur.putBoolean("son",playSon);
                break;
            case R.id.btLangue:
                if(son4.isPlaying()) son4.stop();
                if(playSon) son4.start();
                new PopupLangue(this).show();
                break;
            case R.id.btRetourP:
                if(son6.isPlaying()) son6.stop();
                if(playSon) son6.start();
                finish();
                break;
            case R.id.btPlateuClassique:
                if(son1.isPlaying()) son1.stop();
                if(playSon) son1.start();
                editeur.putInt("plateau",1);
                setCouleurLayout(0);
                break;
            case R.id.btPlateuBlanc:
                if(son2.isPlaying()) son2.stop();
                if(playSon) son2.start();
                editeur.putInt("plateau",2);
                setCouleurLayout(1);
                break;
            case R.id.btPlateuBois:
                if(son3.isPlaying()) son3.stop();
                if(playSon) son3.start();
                editeur.putInt("plateau",3);
                setCouleurLayout(2);
                break;
            case R.id.btThemePion1:
                if(son4.isPlaying()) son4.stop();
                if(playSon) son4.start();
                editeur.putInt("pion",1);
                setCouleurLayout(4);
                break;
            case R.id.btThemePion2:
                if(son5.isPlaying()) son5.stop();
                if(playSon) son5.start();
                editeur.putInt("pion",2);
                setCouleurLayout(5);
                break;
            case R.id.btThemePion3:
                if(son6.isPlaying()) son6.stop();
                if(playSon) son6.start();
                editeur.putInt("pion",3);
                setCouleurLayout(6);
                break;
        }
        editeur.commit();
    }
    private void initLayout(){
        lesLinearLayoutsPlt[0]= findViewById(R.id.linearLayout1);
        lesLinearLayoutsPlt[1]= findViewById(R.id.linearLayout2);
        lesLinearLayoutsPlt[2]= findViewById(R.id.linearLayout3);

        lesLinearLayoutsPion[0] = findViewById(R.id.linearLayout4);
        lesLinearLayoutsPion[1] = findViewById(R.id.linearLayout5);
        lesLinearLayoutsPion[2] = findViewById(R.id.linearLayout6);
    }
    private  void setCouleurLayout(int nb){
        if(nb<3){
            for (LinearLayout linearLayout: lesLinearLayoutsPlt){
                linearLayout.setBackgroundResource(R.color.marron_claire);
            }
            lesLinearLayoutsPlt[nb].setBackgroundResource(R.color.marron_foncee);
        }else{
            for (LinearLayout linearLayout: lesLinearLayoutsPion){
                linearLayout.setBackgroundResource(R.color.marron_claire);
            }
            lesLinearLayoutsPion[nb%4].setBackgroundResource(R.color.marron_foncee);
        }
    }
    // -----------------------------------------------------------------------------------------------------------
}
