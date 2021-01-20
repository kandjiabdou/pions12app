package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kandjiabdou.jeupions12.Ecouteur.EcouteurDeplacement;

import java.util.ArrayList;


public class ActiviteJeu extends Activity implements EcouteurDeplacement, OnClickListener {

    private boolean jeuFini = false;
    private boolean joueurNoir;
    private boolean joueurBlanc;
    private int difficulte;
    private boolean ordiPense=false;
    private int quiCommencePartie;

    private Plateau plateau;
    private MoteurIA moteurIA;

    private IAduJeu iAduJeu ;
    private ProgressBar chargementOrdi;
    private TextView tvInfoTourDeJeu;

    private MediaPlayer sonClick;

    private MediaPlayer sonPionClicke;
    private MediaPlayer sonPionPose;

    private MediaPlayer[] sonCaptures = new MediaPlayer[3];

    private MediaPlayer sonPromotionDame;

    private MediaPlayer[] sonPerdus= new MediaPlayer[2];

    private MediaPlayer sonGagne;
    private MediaPlayer sonEgale;

    private SharedPreferences parametres;
    private SharedPreferences.Editor editeur ;
    private boolean son=true;
    private boolean playMusique=true;
    private boolean premierJeu;
    private int iAjoue = 1;
    private ArrayList<Deplacement> historiqueDeplacement = new ArrayList<>();
    private int themePion;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activite_jeu);
        parametres = PreferenceManager.getDefaultSharedPreferences(this);
        editeur = parametres.edit();
        PrincipaleActivite.changerLangue(this,parametres.getString("langue","fr"));

        this.sonClick = MediaPlayer.create(getApplicationContext(), R.raw.clic_btn);
        this.sonClick.setVolume(0.1f,0.5f);

        this.sonPionClicke = MediaPlayer.create(getApplicationContext(), R.raw.pion_clicke);
        this.sonPionPose = MediaPlayer.create(getApplicationContext(), R.raw.pion_pose);

        this.sonCaptures[0] = MediaPlayer.create(getApplicationContext(), R.raw.capture_pion1);
        this.sonCaptures[1] = MediaPlayer.create(getApplicationContext(), R.raw.capture_pion2);
        this.sonCaptures[2] = MediaPlayer.create(getApplicationContext(), R.raw.capture_pion2);
        this.sonPromotionDame = MediaPlayer.create(getApplicationContext(), R.raw.promotion_dame);
        this.sonPromotionDame.setVolume(0.1f,0.3f);

        this.sonPerdus[0] = MediaPlayer.create(getApplicationContext(), R.raw.perdu_meme_dance);
        this.sonPerdus[0].setVolume(0.1f,0.5f);
        this.sonPerdus[1] = MediaPlayer.create(getApplicationContext(), R.raw.perdu_lion_mort);

        this.sonGagne = MediaPlayer.create(getApplicationContext(), R.raw.applaudissements);
        this.sonGagne.setVolume(0.1f,1f);

        this.sonEgale = MediaPlayer.create(getApplicationContext(), R.raw.oohh);
        this.sonEgale.setVolume(0.1f,1f);

        this.chargementOrdi = findViewById(R.id.pbThinking);
        this.tvInfoTourDeJeu = findViewById(R.id.tvInfoTourDeJeu);

        // set up buttons
        findViewById(R.id.btNouvelleParie).setOnClickListener(this);
        findViewById(R.id.btAnnuler).setOnClickListener(this);
        findViewById(R.id.btParametresJeu).setOnClickListener(this);
        findViewById(R.id.btRetourJeu).setOnClickListener(this);
        findViewById(R.id.btAide).setOnClickListener(this);
        findViewById(R.id.btRetourner).setOnClickListener(this);


        this.plateau = findViewById(R.id.offlineChessBoard);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int tailePlateau = (int) (metrics.widthPixels*0.9);

        ViewGroup.LayoutParams paramsP = this.plateau.getLayoutParams();
        paramsP.height = tailePlateau;
        paramsP.width = tailePlateau;

        this.plateau.setLayoutParams(paramsP);

        this.plateau.setEcouteurDeplacement(this);
        this.plateau.setNoirDoitJouer(false);
        this.plateau.setBlancDoitJouer(false);
        this.joueurBlanc = this.parametres.getBoolean("joueurBlanc", true);
        this.joueurNoir = this.parametres.getBoolean("joueurNoir", true);
        this.difficulte = this.parametres.getInt("difficulte", 1);
        String FEN = parametres.getString("FEN", Tableau.TABLEAU_DEMARRAGE);
        Tableau tableau = new Tableau();
        tableau.fromFEN(FEN);
        this.plateau.setTableau(tableau);
        this.quiCommencePartie = this.parametres.getInt("quiCommence", 1);
        this.plateau.tableau.quiJoue = quiCommencePartie;
        premierJeu = parametres.getBoolean("premierJeu",true);
        if(premierJeu){
            new PopupAide(this).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PrincipaleActivite.changerLangue(this, parametres.getString("langue","fr"));
        if(!PrincipaleActivite.musiqueFond.isPlaying() && playMusique) PrincipaleActivite.musiqueFond.start();
        
        String FEN = parametres.getString("FEN", Tableau.TABLEAU_DEMARRAGE);
        Tableau tableau = new Tableau();
        tableau.fromFEN(FEN);
        this.plateau.setTableau(tableau);
        int quiCommence=this.parametres.getInt("quiCommence", 1);
        this.plateau.tableau.quiJoue = quiCommence;
        if (quiCommence == -1) {
            this.plateau.setNoirDoitJouer(true);
            this.plateau.setBlancDoitJouer(false);
        } else {
            this.plateau.setNoirDoitJouer(false);
            this.plateau.setBlancDoitJouer(true);
        }

        int couleurPlateau = parametres.getInt("plateau",1);
        this.themePion = parametres.getInt("pion",1);
        this.son = parametres.getBoolean("son",true);
        this.playMusique = parametres.getBoolean("musique",true);
        this.plateau.setParametre(couleurPlateau,this.themePion,this.son);
        boolean retourner = this.parametres.getBoolean("retourner", false);
        this.plateau.setRetournner(retourner);

        if(!(this.joueurNoir && this.joueurBlanc)) {
            this.iAjoue = this.joueurBlanc? -1:1;
        }

        setInfoTourDeJeu();

        if ((this.plateau.tableau.quiJoue == 1 && !this.joueurBlanc) || (this.plateau.tableau.quiJoue == -1 && !this.joueurNoir )) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ordinateurJouer();
                }
            };
            Handler handler = new Handler();
            handler.postDelayed(runnable,1000);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        this.parametres  = PreferenceManager.getDefaultSharedPreferences(this);
        this.editeur = this.parametres.edit();
        this.editeur.putString("FEN", this.plateau.tableau.reprendrePartie());
        this.editeur.putBoolean("joueurBlanc", this.joueurBlanc);
        this.editeur.putBoolean("joueurNoir", this.joueurNoir);
        this.editeur.putInt("difficulte", this.difficulte);
        this.editeur.putInt("quiCommence",this.plateau.tableau.quiJoue);
        this.editeur.putBoolean("retourner", this.plateau.getRetourner());
        this.editeur.commit();
        if(PrincipaleActivite.musiqueFond.isPlaying()) PrincipaleActivite.musiqueFond.pause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (this.moteurIA != null && this.moteurIA.getStatus() != AsyncTask.Status.FINISHED)
            new Handler().post(new Runnable() {public void run() { moteurIA.cancel(true); }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.plateau.setLastMoveFrom(-1);
        this.plateau.setLastMoveTo(-1);
        this.plateau.setNoirDoitJouer(this.joueurNoir);
        this.plateau.setBlancDoitJouer(this.joueurBlanc);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btNouvelleParie) {
            if(son) sonClick.start();
            if(!ordiPense){
                finish();
            }
        } else if (v.getId() == R.id.btAnnuler) {

            if(son) sonClick.start();

            if (historiqueDeplacement.size() > 0 && !ordiPense && !jeuFini) {
                plateau.annulerDeplacement(historiqueDeplacement.get(historiqueDeplacement.size() - 1));
                historiqueDeplacement.remove(historiqueDeplacement.size() - 1);

                if (plateau.getBlancDoitJouer() && !joueurBlanc || plateau.getNoirDoitJouer() && !joueurNoir) {
                    if (historiqueDeplacement.size() > 0) {
                        plateau.annulerDeplacement(historiqueDeplacement.get(historiqueDeplacement.size() - 1));
                        historiqueDeplacement.remove(historiqueDeplacement.size() - 1);
                    }
                }

                if (historiqueDeplacement.size() == 0) {
                    plateau.setLastMoveFrom(-1);
                    plateau.setLastMoveTo(-1);
                    plateau.tableau.quiJoue=this.quiCommencePartie;
                    plateau.setQuiJoue(quiCommencePartie);

                    if ((quiCommencePartie == 1 && !this.joueurBlanc) || (quiCommencePartie == -1 && !this.joueurNoir )) {
                        Runnable runnable = new Runnable() {public void run() { ordinateurJouer(); }};
                        Handler handler = new Handler();
                        handler.postDelayed(runnable,1000);
                    }

                } else {
                    Deplacement dernierDeplacement = historiqueDeplacement.get(historiqueDeplacement.size() - 1);
                    plateau.setLastMoveFrom(dernierDeplacement.depart);
                    plateau.setLastMoveTo(dernierDeplacement.arrive);
                }

                setInfoTourDeJeu();

            }

        }else if (v.getId() == R.id.btParametresJeu) {
            if(son) sonClick.start();

            if(!ordiPense){
                Intent activiteParametres= new Intent(ActiviteJeu.this, ActiviteParametres.class);
                startActivity(activiteParametres);
            }

        }else if (v.getId() == R.id.btRetourJeu) {
            if(son) sonClick.start();
            if(!ordiPense){
                finish();
            }
        } else if (v.getId() == R.id.btAide) {
            if(son) sonClick.start();
            if(!ordiPense) new PopupAide(this).show();
        }else if (v.getId() == R.id.btRetourner) {
            if(son) sonClick.start();
            if(!ordiPense){
                plateau.setRetournner(!plateau.getRetourner());
                plateau.setTableau(plateau.tableau);

            }
        }
    }



    @Override
    public void notifierDeplacement(Deplacement deplacement) {
        if(notififerFinPartie()) return;
        historiqueDeplacement.add(deplacement);
        setInfoTourDeJeu();
        if (plateau.tableau.quiJoue== 1 && !joueurBlanc || plateau.tableau.quiJoue == -1 && !joueurNoir) {
            ordinateurJouer();
        }
    }

    @Override
    public void notifierInfo() {
        setInfoTourDeJeu();
        if (plateau.tableau.quiJoue== 1 && !joueurBlanc || plateau.tableau.quiJoue == -1 && !joueurNoir) {
            ordinateurJouer();
        }
    }

    @Override
    public void notifierSon(int nbSon) {
        if(!son) return;
        if(nbSon==1){
            sonPionClicke.start();
        }else if(nbSon==2){
            sonPionPose.start();
        }else if(nbSon==3){
            this.sonCaptures[PrincipaleActivite.nbSonCapture%3].start();
            PrincipaleActivite.nbSonCapture++;
        }else if(nbSon==4){
            this.sonPromotionDame.start();
        }
    }

    private boolean notififerFinPartie(){
        int jf = plateau.tableau.finJeu();
        if(Math.abs(jf)==1 || jf ==0){
            jeuFini = true;
            ordiPense=false;
            setInfoTourDeJeu();
            PopupFinPartie popupFinPartie = new PopupFinPartie(this);
            if(jf==0) {
                if(son) sonEgale.start();
                popupFinPartie.build(getString(R.string.partieEgale));
            } else if(joueurBlanc && joueurNoir){
                if(son) sonGagne.start();
                if (jf==1){
                    popupFinPartie.build(getString(R.string.blancGagne));
                    if(this.themePion==1) popupFinPartie.build(getString(R.string.blancGagne));
                    if(this.themePion==2) popupFinPartie.build(getString(R.string.rougeGagne));
                    if(this.themePion==3) popupFinPartie.build(getString(R.string.marronGagne));
                }else { popupFinPartie.build(getString(R.string.noirGagne)); }
            }else{
                if(son) {
                    if (jf*iAjoue>0) {
                        this.sonPerdus[PrincipaleActivite.nbSonPerdu%2].start();
                        PrincipaleActivite.nbSonPerdu++;
                    }
                    else { sonGagne.start(); }
                }
                if (jf*iAjoue>0){ popupFinPartie.build(getString(R.string.tuAsPerdu));
                }else { popupFinPartie.build(getString(R.string.tuAsGagne)); }
            }
            return true;
        }
        return  false;
    }

    private void ordinateurJouer() {
        ordiPense=true;
        chargementOrdi.setVisibility(View.VISIBLE);
        plateau.setNoirDoitJouer(false);
        plateau.setBlancDoitJouer(false);
        setInfoTourDeJeu();
        if(notififerFinPartie()) return;
        moteurIA = new MoteurIA();
        moteurIA.execute();
    }

    private void setInfoTourDeJeu() {
        if (jeuFini) {
            tvInfoTourDeJeu.setText(getString(R.string.finDuJeu));
            chargementOrdi.setVisibility(ProgressBar.GONE);
            return;
        }

        if (plateau.getNoirDoitJouer()) { // le noir doit jouer
            if (joueurBlanc) tvInfoTourDeJeu.setText(getString(R.string.tourDeNoir));
            else tvInfoTourDeJeu.setText(getString(R.string.aVousJouer));
        } else if (plateau.getBlancDoitJouer()) { // le blanc doit jouer
            if (joueurNoir) {
                if(this.themePion==1) tvInfoTourDeJeu.setText(getString(R.string.tourDeBlanc));
                if(this.themePion==2) tvInfoTourDeJeu.setText(getString(R.string.tourDeRouge));
                if(this.themePion==3) tvInfoTourDeJeu.setText(getString(R.string.tourDeMarron));
            } else
                tvInfoTourDeJeu.setText(getString(R.string.aVousJouer));
        } else {
            tvInfoTourDeJeu.setText(getString(R.string.tourOrdi));
        }

    }
    private void sonDans(final int son,long temps){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                plateau.notifierSon(son);
            }},temps);
    }


    private class MoteurIA extends AsyncTask<Void, Void, Deplacement> {
        @Override
        protected Deplacement doInBackground(Void... nothing) {
            try {
                iAduJeu = new IAduJeu(plateau.tableau);
                return iAduJeu.meilleurCoup(difficulte+2);
            }catch (Exception e){
                return  null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... nothing) {
        }

        @Override
        protected void onPostExecute(Deplacement meilleurCoups) {
            if(meilleurCoups!=null){
                try { Thread.sleep(1000); } catch (InterruptedException ignored) { }
                // on attents 1s pour jouer le coup
                // le debut du premier coup dans 1s pour l'ordi et sa duree de 0.5s
                long debut = System.currentTimeMillis(); // le debut est maintenent, soit 1.5 apres le thread
                long duree = 500;
                // initialement son temps de reflexion est de 1.5s
                //long tempsReflexion=500;
                historiqueDeplacement.add(meilleurCoups);
                plateau.notifierSon(1);
                sonDans(2,450);
                plateau.tableau.faitDeplacement(meilleurCoups);
                chargementOrdi.setVisibility(View.GONE);
                plateau.oridiDeplacerUnPion(meilleurCoups,debut,duree); // prend ou deplacemement normale
                if(meilleurCoups.capture!=0){sonDans(3,100);}
                for(Deplacement resteDeplacement : meilleurCoups.raffles) {
                    // reafle normale apres deplacemement normal
                    // ou prend + deplacemement normale

                    try { Thread.sleep(1300); } catch (InterruptedException ignored) {}
                    sonDans(1,10);
                    sonDans(2,200);
                    plateau.tableau.faitDeplacement(resteDeplacement);
                    if(resteDeplacement.capture!=0){sonDans(3,510);}
                    plateau.oridiDeplacerUnPion(resteDeplacement, System.currentTimeMillis(), duree);

                    for(Deplacement resteDeplacement2 : resteDeplacement.raffles) {
                        try { Thread.sleep(1300); } catch (InterruptedException ignored) { }
                        sonDans(1,10);
                        sonDans(2,500);
                        plateau.tableau.faitDeplacement(resteDeplacement2);
                        if(resteDeplacement2.capture!=0){sonDans(3,210);}
                        plateau.oridiDeplacerUnPion(resteDeplacement2,System.currentTimeMillis(),duree);
                    }
                }
                plateau.pionPerduNonCapture = plateau.tableau.casePionsPerdusParNonCapture;
                plateau.tableau.quiJoue*=-1;

                plateau.setQuiJoue(plateau.tableau.quiJoue);
                setInfoTourDeJeu();
                notififerFinPartie();
                new Handler().postDelayed(new Runnable() {public void run() { ordiPense=false; }},300);
            }
        }
    }
}