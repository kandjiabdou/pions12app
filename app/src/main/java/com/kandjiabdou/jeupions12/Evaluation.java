package com.kandjiabdou.jeupions12;
public class Evaluation {

    private Tableau tableau;

    private int[] caseNoir= {
            10,8,6,8,10,
            8,6,5,6,8,
            6,5,6,5,6,
            6,5,4,5,6,
            4,3,1,3,4
    };

    private int[] caseBlanc= new int[25];

    public Evaluation(Tableau tableau) {
        this.tableau=tableau;
        for(int i=0; i<25;i++) {
            caseBlanc[24-i]=caseNoir[i];
        }
    }

    public int evaluer(int couleur) {
        return evaluerNbPion(couleur)+this.evaluerPosition(couleur);
    }
    public int evaluerPosition(int couleur) {

        int valeur;
        int valB=0;
        int valN=0;
        for(int i=0;i<25;i++) {
            if(Math.abs(this.tableau.casePion25[i])!=2){
                if(this.tableau.casePion25[i]>0) {
                    valB+=this.caseBlanc[i];
                }else if(this.tableau.casePion25[i]<0) {
                    valN+=this.caseNoir[i];
                }
            }
        }
        if(couleur==-1) {
            valeur= valN-valB;
        }
        else {
            valeur= valB-valN;
        }

        return valeur;
    }
    public int evaluerNbPion(int couleur) {
        int valeur;
        if(couleur==1)
            valeur = 2*(this.tableau.compterBlanc()-this.tableau.compterNoir());
        else
            valeur = 2*(this.tableau.compterNoir()-this.tableau.compterBlanc());

        for(int i : tableau.casePion25) {
            if(Math.abs(i)==2){
                if(couleur*i>0) valeur += 15;
                else valeur -= 15;
            }
        }

        return valeur;
    }
}