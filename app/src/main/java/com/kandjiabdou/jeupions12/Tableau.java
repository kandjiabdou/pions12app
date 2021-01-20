package com.kandjiabdou.jeupions12;

import java.util.ArrayList;

public class Tableau {
    public static final String TABLEAU_DEMARRAGE = ""
            + "rrrrr/"
            + "rrrrr/"
            + "rronn/"
            + "nnnnn/"
            + "nnnnn";

    public int[] casePion25 = new int[25];
    public int quiJoue; // whose turn it is, 1 - white, -1 - black
    public ArrayList<Integer> casePionsPerdusParNonCapture= new ArrayList<Integer>();

    public Tableau() {
        for(int i=0;i<25;i++){ casePion25[i]=0; }

        fromFEN(TABLEAU_DEMARRAGE);
        quiJoue = -1;

    }
    private int getPionDeChar(String c){
        int p=0;
        if (c.equals("n")) p=-1;
        if (c.equals("N")) p=-2;
        if (c.equals("R")) p=2;
        if (c.equals("r")) p=1;
        if (c.equals("o")) p=0;

        return p;
    }

    public void fromFEN(String FEN) {

        for(int i=0;i<25;i++){ casePion25[i]=0; }

        //"nnnnn/nnnnn/nnorr/rrrrr/rrrrr "
        String c = "";
        int pos = -1;
        for(int yp=0;yp<5;yp++){ // pour chaque ligne
            for(int xp=0;xp<5;xp++){ // chaque nb
                pos++;
                c = FEN.substring(pos, pos + 1);
                int pieceType = getPionDeChar(c);
                if (pieceType != 0) { casePion25[yp * 5 + xp] = pieceType; }
            }
            pos++;

        }
    }

    private String getCharDePion(int p){
        String c="o";
        if (p==1) c="r";
        if (p==2) c="R";
        if (p==0) c="o";
        if (p==-1) c="n";
        if (p==-2) c="N";
        return c;
    }

    public String reprendrePartie() {
        String retour ="";
        for(int yp=0;yp<5;yp++){ // pour chaque ligne
            for(int xp=0;xp<5;xp++){ // chaque nb
                retour+=this.getCharDePion(this.casePion25[yp*5+xp]);
            }
            retour+="/";
        }
        return retour;
    }

    public int finJeu(){
        int nbR=0;
        int nbN=0;
        for(int i=0;i<25;i++){
            if(casePion25[i]<0) nbN++;
            if(casePion25[i]>0) nbR++;
        }

        if(nbN>1 && nbR>1) return 2;

        if(nbN!=0 && nbR==0) return -1;

        if(nbN==0 && nbR!=0) return 1;

        if(nbN==1 && nbR==1){

            for(int i =0; i<25;i++){

                if(casePion25[i]!=0 && casePion25[i]*quiJoue>0){
                    boolean[] caseCaturable = legalDoubleCapture(i);
                    for(boolean b : caseCaturable) if(b) return 2;
                }
            }

            if(this.casePionsPerdusParNonCapture.size()!=0) return 2;
            return 0;
        }

        return 2;
    }

    public int compterBlanc() {
        int nb=0;
        for(int i=0; i<25;i++) {
            if(this.casePion25[i]>0) nb++;
        }
        return nb;
    }
    public int compterNoir() {
        int nb=0;
        for(int i=0; i<25;i++) {
            if(this.casePion25[i]<0)nb++;
        }
        return nb;
    }

    private int getXdeP(int p){
        return p%5;
    }
    private int getYdeP(int p){
        return p/5;
    }

    public void faitDeplacement(Deplacement deplacement){

        deplacement.piece=this.casePion25[deplacement.depart];
        if(deplacement.depart==deplacement.arrive) {
            deplacement.piece=0;
            deplacement.pieceCapture=this.casePion25[deplacement.depart];
            deplacement.capture = deplacement.depart;
            casePion25[deplacement.depart]=0;
        }else{
            lesPionsPerdusParNonCapture();
            int xd = getXdeP(deplacement.depart);
            int yd = getYdeP(deplacement.depart);

            int xa = getXdeP(deplacement.arrive);
            int ya = getYdeP(deplacement.arrive);

            casePion25[deplacement.arrive]=casePion25[deplacement.depart];
            casePion25[deplacement.depart]=0;
            if (ya==0 && deplacement.piece==-1) casePion25[deplacement.arrive]=-2;
            if (ya==4 && deplacement.piece==1) casePion25[deplacement.arrive]=2;

            if((Math.abs(xd-xa)>=2 && yd==ya) || (Math.abs(yd-ya)>=2 && xd==xa)) {
                if(xd<xa && yd==ya){
                    for(int i=xd+1; i<xa;i++){
                        if(casePion25[i+yd*5]!=0){
                            deplacement.capture= i+yd*5;
                            deplacement.pieceCapture=casePion25[i+yd*5];
                            casePion25[i+yd*5]=0;
                            break;
                        }
                    }
                }
                if(xd>xa && yd==ya){
                    for(int i=xd-1; i>xa;i--){
                        if(casePion25[i+yd*5]!=0){
                            deplacement.capture= i+yd*5;
                            deplacement.pieceCapture=casePion25[i+yd*5];
                            casePion25[i+yd*5]=0;
                            break;
                        }
                    }
                }
                if(yd<ya && xd==xa){
                    for(int i=yd+1; i<ya;i++){
                        if(casePion25[xa+i*5]!=0){
                            deplacement.capture= xa+i*5;
                            deplacement.pieceCapture=casePion25[xa+i*5];
                            casePion25[xa+i*5]=0;
                            break;
                        }
                    }
                }
                if(yd>ya && xd==xa){
                    for(int i=yd-1; i>ya;i--){
                        if(casePion25[xa+i*5]!=0){
                            deplacement.capture= xa+i*5;
                            deplacement.pieceCapture=casePion25[xa+i*5];
                            casePion25[xa+i*5]=0;
                            break;
                        }
                    }
                }
            }

            if(deplacement.capture!=0) {
                boolean[] lc = this.legalDoubleCapture(deplacement.arrive);
                int i=0;
                for(i=0;i<25;i++) {
                    if(lc[i]) {
                        this.casePionsPerdusParNonCapture= new ArrayList<Integer>();
                        this.casePionsPerdusParNonCapture.add(deplacement.arrive);
                        break;
                    }
                }

                if(i==25) {
                    this.casePionsPerdusParNonCapture= new ArrayList<Integer>();
                }
            }else {

                for(int k=0; k<this.casePionsPerdusParNonCapture.size();k++) {
                    if(deplacement.depart==this.casePionsPerdusParNonCapture.get(k)) {
                        this.casePionsPerdusParNonCapture.set(k, deplacement.arrive);
                    }
                }

            }
        }
    }


    private void lesPionsPerdusParNonCapture() {
        this.casePionsPerdusParNonCapture= new ArrayList<Integer>();
        for(int p=0; p<25;p++) {
            if(this.casePion25[p]*this.quiJoue>0 && this.casePion25[p]!=0) {
                boolean[] lm= this.legalDoubleCapture(p);
                for(boolean b: lm) {
                    if(b) {
                        casePionsPerdusParNonCapture.add(p);
                        break;
                    }
                }
            }
        }

    }

    public int moveCapturable(int depart, int arrive){
        int xd = getXdeP(depart);
        int yd = getYdeP(depart);

        int xa = getXdeP(arrive);
        int ya = getYdeP(arrive);

        if((Math.abs(xd-xa)>=2 && yd==ya) || (Math.abs(yd-ya)>=2 && xd==xa)) {
            if(xd<xa && yd==ya){
                for(int i=xd+1; i<xa;i++){
                    if(casePion25[i+yd*5]!=0){
                        return  i+yd*5;
                    }
                }
            }
            else if(xd>xa && yd==ya){
                for(int i=xd-1; i>xa;i--){
                    if(casePion25[i+yd*5]!=0){
                        return  i+yd*5;
                    }
                }
            }
            else if(yd<ya && xd==xa){
                for(int i=yd+1; i<ya;i++){
                    if(casePion25[xa+i*5]!=0){
                        return xa+i*5;
                    }
                }
            }
            else if(yd>ya && xd==xa){
                for(int i=yd-1; i>ya;i--){
                    if(casePion25[xa+i*5]!=0){
                        return xa+i*5;
                    }
                }
            }
        }
        return -1;
    }

    public boolean[] legalDoubleCapture(int from){
        boolean[] lm=new boolean[25];
        for(int i=0;i<25 ;i++){ lm[i]=false; }

        if(Math.abs(casePion25[from])==1){
            if(casePion25[from]==-1){
                if((from/5 - 2) >=0 && casePion25[from-10]==0 && casePion25[from]*casePion25[from-5]<0){
                    lm[from-10]=true;
                }
            }
            if(casePion25[from]==1){
                if((from/5 + 2) <5 && casePion25[from+10]==0 && casePion25[from+5]*casePion25[from]<0) {
                    lm[from + 10] = true;
                }
            }

            if((from%5 +2)<5 && casePion25[from+2]==0 && casePion25[from] * casePion25[from+1]<0){
                lm[from+2]=true;
            }if((from%5-2)>=0 && casePion25[from-2]==0 && casePion25[from] * casePion25[from-1]<0){
                lm[from-2]=true;
            }
        }

        // si le pion est une dame
        if(Math.abs(casePion25[from])==2){ // si le pion est dame
            // haut .................
            boolean aCapture = false;

            for (int y = from/5 -1 ; y>=0;y--) {
                if(casePion25[from%5+y*5]*casePion25[from]>0 || (y-1>=0 && casePion25[from%5+y*5]!=0 && casePion25[from%5+(y-1)*5]!=0) || (aCapture && casePion25[from%5+y*5]!=0)) {
                    break;
                }
                if(casePion25[from%5+y*5]==0){
                    if( (from%5+(y+1)*5) != from && casePion25[from%5+(y+1)*5]*casePion25[from]<0 && casePion25[from%5+(y+1)*5]!=0) aCapture=true;
                    if(aCapture)lm[from%5+y*5] = true;
                }
            }
            aCapture=false;
            // bas
            for (int y = from/5 +1 ; y<5;y++) {
                if(casePion25[from%5+y*5]*casePion25[from]>0 || (y+1<5 && casePion25[from%5+y*5]!=0 && casePion25[from%5+(y+1)*5]!=0) || (aCapture && casePion25[from%5+y*5]!=0)) {
                    break;
                }
                if(casePion25[from%5+y*5]==0){
                    if( (from%5+(y-1)*5) != from && casePion25[from%5+(y-1)*5]*casePion25[from]<0 && casePion25[from%5+(y-1)*5]!=0) aCapture=true;
                    if(aCapture)lm[from%5+y*5] = true;
                }
            }
            aCapture=false;
            // droite
            for (int x = from%5 +1 ; x<5;x++) {
                if(casePion25[x+(from/5)*5]*casePion25[from]>0 || (x+1<5 && casePion25[x+(from/5)*5]!=0 && casePion25[x+1+(from/5)*5]!=0) || (aCapture && casePion25[x+(from/5)*5]!=0)) {
                    break;
                }
                if(casePion25[x+(from/5)*5]==0){
                    if( (x-1+(from/5)*5) != from && casePion25[x-1+(from/5)*5]*casePion25[from]<0 && casePion25[x-1+(from/5)*5]!=0) aCapture=true;
                    if(aCapture)lm[x+(from/5)*5] = true;
                }
            }
            aCapture=false;
            // guache
            for (int x = from%5 -1 ; x>=0;x--) {
                if(casePion25[x+(from/5)*5]*casePion25[from]>0 || (x-1>=0 && casePion25[x+(from/5)*5]!=0 && casePion25[x-1+(from/5)*5]!=0) || (aCapture && casePion25[x+(from/5)*5]!=0)) {
                    break;
                }
                if(casePion25[x+(from/5)*5]==0){
                    if( (x+1+(from/5)*5) != from && casePion25[x+1+(from/5)*5]*casePion25[from]<0 && casePion25[x+1+(from/5)*5]!=0) aCapture=true;
                    if(aCapture) lm[x+(from/5)*5] = true;
                }
            }
        }

        return lm;
    }

    public boolean[] legalMovesMap(int from){

        boolean[] lm=new boolean[25];
        for(int i=0;i<25 ;i++){ lm[i]=false; }
        if(Math.abs(casePion25[from])==1){

            if(casePion25[from]==-1){
                // Haut noir
                if((from/5 - 1) >=0 && casePion25[from-5]==0){
                    lm[from-5]=true;
                }// deux cases en haut nior
                else if((from/5 - 2) >=0 && casePion25[from-10]==0 && casePion25[from]*casePion25[from-5]<0){
                    lm[from-10]=true;
                }
            }
            if(casePion25[from]==1){
                // BAS rouge
                if((from/5 + 1) <5 && casePion25[from+5]==0){
                    lm[from+5]=true;
                }// deux cases en bas
                else if((from/5 + 2) <5 && casePion25[from+10]==0 && casePion25[from+5]*casePion25[from]<0) {
                    lm[from + 10] = true;
                }
            }

            if((from%5 + 1) < 5 && casePion25[from+1]==0){// DROITE
                lm[from+1]=true;
            }else if((from%5 +2)<5 && casePion25[from+2]==0 && casePion25[from] * casePion25[from+1]<0){
                lm[from+2]=true;
            }
            if((from%5 -1 )>=0 && casePion25[from-1]==0){// GAUCHE
                lm[from-1]=true;
            }else if((from%5-2)>=0 && casePion25[from-2]==0 && casePion25[from] * casePion25[from-1]<0){
                lm[from-2]=true;
            }
        }

        // si le pion est une dame
        if(Math.abs(casePion25[from])==2){ // si le pion est dame
            // haut .................
            boolean aCapture = false;

            for (int y = from/5 -1 ; y>=0;y--) {
                if(casePion25[from%5+y*5]*casePion25[from]>0 || (y-1>=0 && casePion25[from%5+y*5]!=0 && casePion25[from%5+(y-1)*5]!=0) || (aCapture && casePion25[from%5+y*5]!=0)) {
                    break;
                }
                if(casePion25[from%5+y*5]==0){
                    if( (from%5+(y+1)*5) != from && casePion25[from%5+(y+1)*5]*casePion25[from]<0 && casePion25[from%5+(y+1)*5]!=0) aCapture=true;
                    lm[from%5+y*5] = true;
                }
            }
            aCapture=false;
            // bas
            for (int y = from/5 +1 ; y<5;y++) {
                if(casePion25[from%5+y*5]*casePion25[from]>0 || (y+1<5 && casePion25[from%5+y*5]!=0 && casePion25[from%5+(y+1)*5]!=0) || (aCapture && casePion25[from%5+y*5]!=0)) {
                    break;
                }
                if(casePion25[from%5+y*5]==0){
                    if( (from%5+(y-1)*5) != from && casePion25[from%5+(y-1)*5]*casePion25[from]<0 && casePion25[from%5+(y-1)*5]!=0) aCapture=true;
                    lm[from%5+y*5] = true;
                }
            }
            aCapture=false;
            // droite
            for (int x = from%5 +1 ; x<5;x++) {
                if(casePion25[x+(from/5)*5]*casePion25[from]>0 || (x+1<5 && casePion25[x+(from/5)*5]!=0 && casePion25[x+1+(from/5)*5]!=0) || (aCapture && casePion25[x+(from/5)*5]!=0)) {
                    break;
                }
                if(casePion25[x+(from/5)*5]==0){
                    if( (x-1+(from/5)*5) != from && casePion25[x-1+(from/5)*5]*casePion25[from]<0 && casePion25[x-1+(from/5)*5]!=0) aCapture=true;
                    lm[x+(from/5)*5] = true;
                }
            }
            aCapture=false;
            // guache
            for (int x = from%5 -1 ; x>=0;x--) {
                if(casePion25[x+(from/5)*5]*casePion25[from]>0 || (x-1>=0 && casePion25[x+(from/5)*5]!=0 && casePion25[x-1+(from/5)*5]!=0) || (aCapture && casePion25[x+(from/5)*5]!=0)) {
                    break;
                }
                if(casePion25[x+(from/5)*5]==0){
                    if( (x+1+(from/5)*5) != from && casePion25[x+1+(from/5)*5]*casePion25[from]<0 && casePion25[x+1+(from/5)*5]!=0) aCapture=true;
                    lm[x+(from/5)*5] = true;
                }
            }
        }

        return lm;
    }

    public ArrayList<Deplacement> genererToutesLesPossibilites(){

        ArrayList<Deplacement> toutesLesPossibilites= new ArrayList<Deplacement>();
        for(int position = 0; position<25; position++ ) {

            if(this.casePion25[position]!=0 && this.casePion25[position]*this.quiJoue>0) {
                boolean[] legalMove = this.legalMovesMap(position);
                for(int arrive = 0; arrive<25; arrive++ ) {
                    if(legalMove[arrive]) {
                        Tableau tableauFils = this.copierTableau();
                        Deplacement d =new Deplacement(tableauFils, position, arrive);

                        tableauFils.faitDeplacement(d);

                        toutesLesPossibilites.add(d);

                        if(d.capture!=0) {
                            ArrayList<ArrayList<Integer>> tousLesRafles = tableauFils.genererTousRafles(tableauFils, arrive);
                            for(ArrayList<Integer> linge : tousLesRafles ) {
                                Tableau tableauFilsRafle = tableauFils.copierTableau();

                                Deplacement deplacementRafle =new Deplacement(tableauFilsRafle, position, arrive);
                                int departR=arrive;
                                int arriveR=0;

                                for(int arriveRafle: linge) {
                                    arriveR=arriveRafle;

                                    deplacementRafle.raffles.add(new Deplacement(tableauFilsRafle, departR, arriveR));
                                    tableauFilsRafle.faitDeplacement(new Deplacement(tableauFilsRafle, departR, arriveR));
                                    departR=arriveRafle;
                                }

                                toutesLesPossibilites.add(deplacementRafle);
                            }
                        }

                    }
                }
            }



        }

        if(this.casePionsPerdusParNonCapture.size()!=0) {
            for(int pp : this.casePionsPerdusParNonCapture) {

                Tableau tableauFilsPerteCapture = this.copierTableau();

                tableauFilsPerteCapture.faitDeplacement(new Deplacement(tableauFilsPerteCapture, pp, pp));

                ArrayList<Deplacement> continuePossibilite = tableauFilsPerteCapture.genererLeResteDesPossibilites();

                for(Deplacement dR : continuePossibilite) {
                    Deplacement dP = new Deplacement(tableauFilsPerteCapture, pp, pp);
                    dP.raffles.add(dR);
                    toutesLesPossibilites.add(dP);
                }

            }

        }

        return toutesLesPossibilites;

    }

    public int promotionDame(){
        int nb = compterBlanc();
        int nn = compterNoir();
        int r=-1;
        if(nn ==1){
            for(int i=0; i<25;i++){
                if(casePion25[i]==-1 ){
                    casePion25[i]=-2;
                    return i*-1;
                }
            }
        }
        if(nb==1){
            for(int i=0; i<25;i++){
                if(casePion25[i]==1){
                    casePion25[i]=2;
                    return i;
                }
            }
        }
        return r;
    }

    public ArrayList<Deplacement> genererLeResteDesPossibilites(){

        ArrayList<Deplacement> toutesLesPossibilites= new ArrayList<Deplacement>();
        for(int position = 0; position<25; position++ ) {

            if(this.casePion25[position]!=0 && this.casePion25[position]*this.quiJoue>0) {
                boolean[] legalMove = this.legalMovesMap(position);
                for(int arrive = 0; arrive<25; arrive++ ) {
                    if(legalMove[arrive]) {

                        Tableau tableauFils = this.copierTableau();

                        Deplacement d =new Deplacement(tableauFils, position, arrive);

                        tableauFils.faitDeplacement(d);

                        toutesLesPossibilites.add(d);

                        if(d.capture!=0) {
                            ArrayList<ArrayList<Integer>> tousLesRafles = tableauFils.genererTousRafles(tableauFils, arrive);
                            for(ArrayList<Integer> linge : tousLesRafles ) {
                                Tableau tableauFilsRafle = tableauFils.copierTableau();

                                Deplacement deplacementRafle =new Deplacement(tableauFilsRafle, position, arrive);
                                int departR=arrive;
                                int arriveR=0;

                                for(int arriveRafle: linge) {
                                    arriveR=arriveRafle;

                                    deplacementRafle.raffles.add(new Deplacement(tableauFilsRafle, departR, arriveR));
                                    tableauFilsRafle.faitDeplacement(new Deplacement(tableauFilsRafle, departR, arriveR));
                                    departR=arriveRafle;
                                }

                                toutesLesPossibilites.add(deplacementRafle);
                            }
                        }

                    }
                }
            }

        }

        return toutesLesPossibilites;

    }



    public ArrayList<ArrayList<Integer>> genererTousRafles(Tableau tablaeu, int racine){
        ArrayList<ArrayList<Integer>> touslesRafles= new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> listeActuelle = new ArrayList<Integer>();
        if(tablaeu.quiJoue*tablaeu.casePion25[racine]>0) {
            rechercher(tablaeu, racine, 0, listeActuelle, touslesRafles);
            if(touslesRafles.size()>=1) touslesRafles.remove(touslesRafles.size()-1);

        }

        return touslesRafles;
    }


    public void rechercher(Tableau tablaeu, int depart, int indice, ArrayList<Integer> listeActuelle, ArrayList<ArrayList<Integer>> touslesRafles) {
        boolean[] lm = tablaeu.legalDoubleCapture(depart);
        int i;
        for(i=indice;i<25;i++) {
            if(lm[i]) {

                Tableau tbF= tablaeu.copierTableau();
                Deplacement d = new Deplacement(tbF, depart, i);
                tbF.faitDeplacement(d);

                // Je continue avac la liste avec la liste actuelle
                // je copie le tableau
                ArrayList<Integer> nouvelleliste = copierListe(listeActuelle);
                nouvelleliste.add(i);

                //je continue la recherche avec i
                rechercher(tbF, i, 0, nouvelleliste, touslesRafles);
            }

        }

        if(i==25) {
            // J'ai pas vu avec "+ depart;
            // et j' ajoute la liste � tous les rafles possible
            ArrayList<Integer> nouvelleliste = copierListe(listeActuelle);
            nouvelleliste.add(depart);

            if(nouvelleliste.size()>=1) nouvelleliste.remove(nouvelleliste.size()-1);
            touslesRafles.add(nouvelleliste);
        }

    }

    private ArrayList<Integer> copierListe(ArrayList<Integer> liste){
        ArrayList<Integer> listeCopie = new ArrayList<Integer>();
        for(int i=0;i< liste.size();i++) {
            listeCopie.add(liste.get(i));
        }
        return listeCopie;
    }


    public Tableau copierTableau(){
        Tableau tableauCopie = new Tableau();
        tableauCopie.quiJoue = this.quiJoue;
        for(int i=0; i<25;i++) {
            tableauCopie.casePion25[i]=this.casePion25[i];
        }
        tableauCopie.casePionsPerdusParNonCapture=this.casePionsPerdusParNonCapture;
        return tableauCopie;
    }
}