package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActiviteNouvellePartie extends Activity implements OnClickListener {
    private boolean joueurNoir=true;
    private boolean joueurBlanc=true;
    private int quiCommence = -1;
    private int couleurVsNoir=1;

    private Button btJoueurNoir;
    private Button btOrdinateurNoir;
    private Button btOrdinateurRouge;
    private Button btJoueurBlanc;
    private Button btQuiCommence;
    private TextView textJoueur2;


    private SeekBar barDifficulte;
    private TextView textDifficulte;
    private MediaPlayer mediaPlayerBar1;
    private MediaPlayer mediaPlayerBar2;
    private MediaPlayer mediaPlayerBar3;
    private MediaPlayer mediaPlayerJn;
    private MediaPlayer mediaPlayerJr;
    private MediaPlayer mediaPlayerOn;
    private MediaPlayer mediaPlayerOr;
    private MediaPlayer mediaPlayerJouer;

    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur ;
    private boolean playSon=true;
    private boolean playMusic=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_nouvelle_partie);
        // restauration des parametres si stockes
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        editeur= parametres.edit();
        couleurVsNoir = parametres.getInt("pion",1);
        playSon=parametres.getBoolean("son",true);
        playMusic=parametres.getBoolean("musique",true);

        // initialisation des medias sons / bruitages
        initMedias();

        //  Initialissation des boutons
        initBoutons();

        // initialisation bar de dificulte et le texte
        initBarDifficulte();

        // Bouton jouer une nouvelle partie
        initBoutonJouer();
    }
    private void initMedias(){
        mediaPlayerJn = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jn);
        mediaPlayerJr = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jr);
        mediaPlayerOn = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_on);
        mediaPlayerOr = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_or);
        mediaPlayerJouer = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn_jouer);
        mediaPlayerBar1 = MediaPlayer.create(getApplicationContext(), R.raw.bar_diff_1);
        mediaPlayerBar2 = MediaPlayer.create(getApplicationContext(), R.raw.bar_diff_2);
        mediaPlayerBar3 = MediaPlayer.create(getApplicationContext(), R.raw.bar_diff_3);
        mediaPlayerJn.setVolume(0.1f,0.5f);
        mediaPlayerJr.setVolume(0.1f,0.5f);
        mediaPlayerOn.setVolume(0.1f,0.5f);
        mediaPlayerOr.setVolume(0.1f,0.5f);
        mediaPlayerJouer.setVolume(0.1f,0.5f);
        mediaPlayerBar1.setVolume(0.1f,0.5f);
        mediaPlayerBar2.setVolume(0.1f,0.5f);
        mediaPlayerBar3.setVolume(0.1f,0.5f);
    }

    private void initBoutons(){
        btJoueurNoir = findViewById(R.id.btJoueurNoir);
        btJoueurNoir.setOnClickListener(this);
        btOrdinateurNoir = findViewById(R.id.btOrdinateurNoir);
        btOrdinateurNoir.setOnClickListener(this);

        btJoueurBlanc = findViewById(R.id.btJoueurRouge);
        btJoueurBlanc.setOnClickListener(this);
        btOrdinateurRouge = findViewById(R.id.btOrdinateurRouge);
        btOrdinateurRouge.setOnClickListener(this);

        btQuiCommence = findViewById(R.id.btnQuiCommence);
        btQuiCommence.setOnClickListener(this);
        textJoueur2 = findViewById(R.id.tvJoueur2);
        ImageView btPararmetre = findViewById(R.id.btParametresNp);
        btPararmetre.setOnClickListener(this);
        ImageView btRetourAcl = findViewById(R.id.btRetourNp);
        btRetourAcl.setOnClickListener(this);
    }

    private void initBarDifficulte(){
        textDifficulte= findViewById(R.id.text_difficulte);
        barDifficulte = findViewById(R.id.barDiffculte);
        int nb = parametres.getInt("difficulte", 1);
        barDifficulte.setProgress(nb);
        String msg="";
        if(nb==0) {if(playSon) mediaPlayerBar1.start(); msg= getString(R.string.text_diffculteF);}
        if(nb==1) {if(playSon) mediaPlayerBar2.start(); msg= getString(R.string.text_diffculteM);}
        if(nb==2) {if(playSon) mediaPlayerBar3.start(); msg= getString(R.string.text_diffculteD);}
        textDifficulte.setText(msg);
        barDifficulte.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String msg="";
                if(progress==0) {if(playSon) mediaPlayerBar1.start(); msg= getString(R.string.text_diffculteF);}
                if(progress==1) {if(playSon) mediaPlayerBar2.start(); msg= getString(R.string.text_diffculteM);}
                if(progress==2) {if(playSon) mediaPlayerBar3.start(); msg= getString(R.string.text_diffculteD);}
                textDifficulte.setText(msg);
                editeur.putInt("difficulte", progress);
                editeur.commit();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
        });

    }

    private void initBoutonJouer(){
        Button btJouer = findViewById(R.id.btJouer);
        btJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playSon) mediaPlayerJouer.start();
                if (!joueurNoir && !joueurBlanc) { // Au moins un des deux joueurs doit etre un etre humain
                    Toast.makeText(ActiviteNouvellePartie.this, R.string.ordiVSordiToast, Toast.LENGTH_SHORT).show();
                    return;
                }

                editeur.putString("FEN", Tableau.TABLEAU_DEMARRAGE);
                editeur.putBoolean("joueurBlanc", joueurBlanc);
                editeur.putBoolean("joueurNoir", joueurNoir);
                editeur.putInt("difficulte", barDifficulte.getProgress());
                editeur.putInt("quiCommence", quiCommence);
                editeur.commit();

                Intent activiteDuJeu = new Intent(ActiviteNouvellePartie.this, ActiviteJeu.class);
                startActivity(activiteDuJeu);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        // restauration des parametres si stockes
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        PrincipaleActivite.changerLangue(this,parametres.getString("langue","fr"));

        couleurVsNoir = parametres.getInt("pion",1);
        playSon=parametres.getBoolean("son",true);
        playMusic=parametres.getBoolean("musique",true);

        joueurNoir = parametres.getBoolean("joueurNoir", true); // Stokage des parametres
        joueurBlanc = parametres.getBoolean("joueurBlanc", true); // Stokage des parametres
        quiCommence = parametres.getInt("quiCommence", 1);
        barDifficulte.setProgress(parametres.getInt("difficulte", 1));

        setNoir();
        setBlanc();
        setQuiCommence();

        if(!PrincipaleActivite.musiqueFond.isPlaying() && playMusic) PrincipaleActivite.musiqueFond.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        if(PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.pause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btJoueurNoir:
                if(playSon) mediaPlayerJn.start();
                joueurNoir = true;
                setNoir();
                break;
            case R.id.btOrdinateurNoir:
                if(playSon) mediaPlayerOn.start();
                joueurNoir = false;
                setNoir();
                break;
            case R.id.btJoueurRouge:
                if(playSon) mediaPlayerJr.start();
                joueurBlanc = true;
                setBlanc();
                break;
            case R.id.btOrdinateurRouge:
                if(playSon) mediaPlayerOr.start();
                joueurBlanc = false;setBlanc();
                break;
            case R.id.btnQuiCommence:
                if(playSon) mediaPlayerOn.start();
                quiCommence*=-1;
                setQuiCommence();
                break;
            case R.id.btRetourNp:
                if(playSon) mediaPlayerOr.start();
                finish();
                break;
            case R.id.btParametresNp:
                if(playSon) mediaPlayerOn.start();
                Intent activiteParametres= new Intent(ActiviteNouvellePartie.this, ActiviteParametres.class);
                startActivity(activiteParametres);
                break;
        }
    }

    private void setNoir() {
        if (joueurNoir) {
            btJoueurNoir.setBackgroundResource(R.drawable.bg_noir);
            btOrdinateurNoir.setBackgroundResource(R.drawable.bg_vide);
            if(joueurBlanc){
                textDifficulte.setVisibility(TextView.GONE);
                barDifficulte.setVisibility(SeekBar.GONE);
            }
        } else {
            btJoueurNoir.setBackgroundResource(R.drawable.bg_vide);
            btOrdinateurNoir.setBackgroundResource(R.drawable.bg_noir);
            textDifficulte.setVisibility(TextView.VISIBLE);
            barDifficulte.setVisibility(SeekBar.VISIBLE);
        }
    }

    private void setBlanc() {
        if (joueurBlanc) {
            if(couleurVsNoir==2){
                btJoueurBlanc.setBackgroundResource(R.drawable.bg_rouge);
                textJoueur2.setText(getString(R.string.joueur2r));
            }else if (couleurVsNoir==3){
                btJoueurBlanc.setBackgroundResource(R.drawable.bg_marron);
                textJoueur2.setText(getString(R.string.joueur2m));
            }else{
                btJoueurBlanc.setBackgroundResource(R.drawable.bg_blanc);
                textJoueur2.setText(getString(R.string.joueur2b));
            }
            btOrdinateurRouge.setBackgroundResource(R.drawable.bg_vide);
            if(joueurNoir){
                textDifficulte.setVisibility(TextView.GONE);
                barDifficulte.setVisibility(SeekBar.GONE);
            }
        } else {
            btJoueurBlanc.setBackgroundResource(R.drawable.bg_vide);
            textDifficulte.setVisibility(TextView.VISIBLE);
            barDifficulte.setVisibility(SeekBar.VISIBLE);
            if(couleurVsNoir==2){
                textJoueur2.setText(getString(R.string.joueur2r));
                btOrdinateurRouge.setBackgroundResource(R.drawable.bg_rouge);
            }else if(couleurVsNoir==3){
                textJoueur2.setText(getString(R.string.joueur2m));
                btOrdinateurRouge.setBackgroundResource(R.drawable.bg_marron);
            }else{
                textJoueur2.setText(getString(R.string.joueur2b));
                btOrdinateurRouge.setBackgroundResource(R.drawable.bg_blanc);
            }
        }
    }

    private void setQuiCommence() {
        if(quiCommence==1) {
            if(couleurVsNoir==2){
                btQuiCommence.setText(getString(R.string.rougeCommence));
                btQuiCommence.setBackgroundResource(R.drawable.bg_rouge);
            }else if(couleurVsNoir==3){
                btQuiCommence.setText(getString(R.string.marronCommence));
                btQuiCommence.setBackgroundResource(R.drawable.bg_marron);
            }else {
                btQuiCommence.setText(getString(R.string.blancCommence));
                btQuiCommence.setBackgroundResource(R.drawable.bg_blanc);
            }
        } else if(quiCommence==-1){
            btQuiCommence.setText(getString(R.string.noirCommence));
            btQuiCommence.setBackgroundResource(R.drawable.bg_noir);
        }
    }
}