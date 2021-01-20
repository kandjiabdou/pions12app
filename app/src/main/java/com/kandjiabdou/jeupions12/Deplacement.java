package com.kandjiabdou.jeupions12;

import java.util.ArrayList;

public class Deplacement {

    public int depart;
    public int arrive;
    public int piece;

    public int capture;
    public int pieceCapture=0;
    public int quiJouais;
    public Tableau tableau ;
    public ArrayList<Deplacement> raffles= new ArrayList<>();

    public Deplacement(Tableau tableau, int depart, int arrive) {
        this.tableau = tableau.copierTableau();
        this.quiJouais = tableau.quiJoue;
        this.piece = tableau.casePion25[depart];
        this.depart = depart;
        this.arrive = arrive;
        this.capture=0;
    }

    public int compterRafle() {
        int nb=1;
        if(this.raffles.size()!=0) {
            for(Deplacement d: this.raffles) {
                nb++;
                for(Deplacement ignored : d.raffles) {
                    nb++;
                }
            }
        }
        return nb;
    }
}