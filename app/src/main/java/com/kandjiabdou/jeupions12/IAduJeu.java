package com.kandjiabdou.jeupions12;

import java.util.ArrayList;

public class IAduJeu {
	
	/*
     * La Classe Situation definie une situation dans laquelle un joueur a
     * plusieurs possilites pour faire un coup:
     * cette classe est caracterisee par :
     *  -- un tableau (les pions presents dans dans le plateau),
     *  -- les possibiltes de deplacement (ensemble de coups possibles),
     *  -- les situations filles que chaque possiblite deplacement genere,
     *  -- le maximiant un boolean verifiant si le joueur maximise ses gains (true) sinon minimise ses gains (false)
     *  -- la prondeur de la situation(si c'est 0 elle est evaluee sion elle cree d'autres situations filles)
     */


    public class Situation{
        private int evaluation;
        private Tableau tableauSituation;
        ArrayList<Deplacement> possibiltesDeplacement;
        ArrayList<Situation> situationFilles= new ArrayList<>();
        private boolean maximisant;
        private int profondeur;
        
        // Constructeur
        // Pour creer une situation on besion du tableau du jeu acctuelle,
        // la profondeur limite, et le maximisant.
       
        public Situation(Tableau tableauSituation,int profondeur, boolean maximisant) {
            this.tableauSituation=tableauSituation;
            this.profondeur=profondeur;
            this.maximisant=maximisant;
            this.possibiltesDeplacement = this.tableauSituation.genererToutesLesPossibilites();
        }
        /*
         * Cette classe pocede une methode ou fonction "evaluer(alpha, beta)"
         * pour evaluer une situation on a besion des bornes Alpha et Beta pour elaguer certaines
         * de ses situations filles, pour optimisation
         * Au depart alpha prend la valeur de -00 pour alpha et beta +00, donc alpha doit etre plus petit que beta
         */
        public int evaluer(int alpha0, int beta0){
        	// avant de l'evaluer ou de parcourrir ses branches
        	// on verifie si le jeu est termine ou si la profondeur = 0 ou le temps limite de calcule est atteinte.
            // le tableau de la situation pocede un fonction "finJeu()"
        	// qui retourne :
        	// ->  1 si les blanc ont gagne
        	// -> -1 si les noirs ont gagne
        	// ->  O s'il y a match nulle
        	// ->  2 si le jeu n'est pas fini


        	int finJeu = this.tableauSituation.finJeu();
            if(finJeu!=2) {
            	// si le jeu est fini la valeur retourne par la fonction finJeu() determine le gagnant
            	// l'evaluation est forte, on la multiplie par 1234567
                int e = finJeu*jeJoueCouleur*1234567;
                // l'evaluation devient plus forte si on trouve un gagnant avec une pronfondeur faible
                if(profondeur!=0) e*=this.profondeur;
                this.evaluation=e;
                return e;
            }else if(this.profondeur==0 || (System.currentTimeMillis()-debutTempsDeRecherche)>tempsLimitDeRecherche){
            	// si la profondeur est atteinte ou le temps de calcule depasse on evalue la situation
                int e=new Evaluation(this.tableauSituation).evaluer(jeJoueCouleur);
                this.evaluation=e;
                return e;
            } else {
            	// Sinon on initialise ses situations fille pour chaque possiblite de deplacement
                this.situationFilles = new ArrayList<>();
                for(Deplacement d : this.possibiltesDeplacement) {
                    Tableau tableauFils= this.tableauSituation.copierTableau();
                    tableauFils.faitDeplacement(d);
                    tableauFils.quiJoue*=-1;
                    Situation situationFille = new Situation(tableauFils,this.profondeur-1, !this.maximisant);
                    this.situationFilles.add(situationFille);
                }
                
                // on commence l'agorithme minmax + l'elagage alphaBeta
                
                int valeur;       // valeur de l'evaluation
                int alpha=alpha0; // alpha
                int beta=beta0;   // beta
                if(this.maximisant) { // Si la situation est maximisante
                    valeur = -1000000; // la valeur prend initialement alpha
                    
                    //Pour toute situation fille :
                    //	on l'evalue puis le compare avec le max trouve entre elles
                    for(Situation situationFille : this.situationFilles) {
                        valeur = max(valeur, situationFille.evaluer(alpha,beta));
                        // si la valeur de la sitation est superieur a beta
                        // on coupe les branches suivantes (les situations suivantes ne seront pas etudiees) et on retourne la valeur(coupure beta)
                        if(valeur>=beta) {
                            this.evaluation = valeur;
                            return valeur;
                        }
                        
                        alpha = max(alpha,valeur);
                    }

                }else {// Sinon la situation est minimisante
                    valeur = 1000000;

                    //Pour toute situation fille :
                    //	on l'evalue puis le compare avec le min trouve entre elles
                    for(Situation situationFille : this.situationFilles) {
                    	// si la valeur de la sitation est inferieur a alpha
                        // on coupe les branches suivantes(les situations suivantes ne seront pas atudiees) et on retourne la valeur(coupure alpha)
                        valeur = min(valeur,situationFille.evaluer(alpha,beta));
                        if(alpha>=valeur) {
                            this.evaluation=valeur;
                            return valeur;
                        }
                        beta=min(beta,valeur);
                    }
                }

                this.evaluation=valeur;
                return valeur;
            }

        }

    }
    
    /*
     * La classe IAduJeu permet de retrouver un meilleur coup pour une situation donnee
     * avec le joueur qui doit jouer !!
     * 
     * Cette classe est caracterisee par:
     * -- le tableau actuelle
     * -- la couleur que l'ordi doit jouer
     * -- et le temps limite de recherche
     */

    public Tableau tableau;
    private int jeJoueCouleur;
    private int tempsLimitDeRecherche ;
    private long debutTempsDeRecherche ;

    /************
     Costructeur de la class IAduJeu
     Il prend en parametre un tableau
     La classe Tableau materialise les cases du plateau
     avec les pions  representes par un ArrayListe de nombre
     1 et 2 pour les blancs et -1 -2 pour les noirs (les |+-2| sont dame et |+-1| des pions simples)
    
     *************/
    //
    public IAduJeu(Tableau t) {
        this.tableau= t.copierTableau();
        this.jeJoueCouleur=t.quiJoue;
    }

    // methode appele pour donner le meileur coups durant le parcours de l'arbre
    public Deplacement meilleurCoup(int profondeur) {

        // on initilialise les valeurs avant recherche
    	// alhpa et beta
        int alphaInitiale=-10000001;
        int betaInitiale = 10000001;
        int max=-5474547; // valeur de l'evalution initiale
        
        // chrono temps de recherche
        this.debutTempsDeRecherche = System.currentTimeMillis();
        this.tempsLimitDeRecherche = 10000;
        // la siation de depart(S a laquelle l'odi doit choisir un coup)
        // elle prend en parametre le tableau actuel, la profondeur(depend du niveau de difficulte), le maximisant qui est vrai(true)
        Situation situationDepart = new Situation(tableau, profondeur, true);
        
        //S'il y'a une seule possiblite on retourne cette le coup possible et on fait pas de recherche pour ne pas perdre du temps
        if(situationDepart.possibiltesDeplacement.size()==1) {
            return situationDepart.possibiltesDeplacement.get(0);
        }
        
        //S'il ya plusieur possibilte : on cree ses situations filles
        
        situationDepart.situationFilles= new ArrayList<>();
        int nb = jeJoueCouleur==-1 ? this.tableau.compterBlanc():this.tableau.compterNoir();
        if(nb<=4){
        	// cette partie permet d'arreter la recherche lorsqu'on qu'on trouve un coup gagnant
        	// vers la fin du jeu (environ 4 pions restants pour l'adversaire)
            int k;
            for(k=0; k<situationDepart.possibiltesDeplacement.size();k++) {
                Tableau tableauFils= situationDepart.tableauSituation.copierTableau();
                tableauFils.faitDeplacement(situationDepart.possibiltesDeplacement.get(k));
                Situation situationFille = new Situation(tableauFils,0, false);

                if(situationFille.evaluer(alphaInitiale,betaInitiale)==1234567){
                    return situationDepart.possibiltesDeplacement.get(k);
                }
            }
        }

        Deplacement d = situationDepart.possibiltesDeplacement.get(0);
        int evaluation;
        
        // c'est cet instruuction qui permet de determiner le meilleur coups
        // car c'est la fonction "evaluer(alphaInitiale,betaInitiale)" qui fait appelle la recursion
        // afin de parcourrir toutes les branches a un profondeur et temps de calcule donne
        situationDepart.evaluer(alphaInitiale,betaInitiale);
        
        // une fois execute : on a que les valeurs des situations filles(issu des deplacements possibles)
        // on prend le deplacement correspondant a la situation qui a le max d'evaluation
        int i;
        for(i=0; i<situationDepart.situationFilles.size();i++) {
            
            evaluation=situationDepart.situationFilles.get(i).evaluation;
            // il arrive que des deplacements aient la meme  valeur d'evaluation
            if(evaluation==max) {
            	// cette partie permet de filtrer les coups qui ont la meme valeur d'evaluation
                if(d.capture==0 && situationDepart.possibiltesDeplacement.get(i).capture!=0) {
                    // un deplacement qui a une capture vaut plus qu'un autre qui n'en a pas
                	d=situationDepart.possibiltesDeplacement.get(i);
                }
                if(d.capture!=0 && situationDepart.possibiltesDeplacement.get(i).capture!=0 ) {
                    if(d.compterRafle() < situationDepart.possibiltesDeplacement.get(i).compterRafle()) {
                    	// un coup qui admet plus de rafles vaut plus qu'un autre qui a en moins
                        d=situationDepart.possibiltesDeplacement.get(i);
                    }
                }
            }

            if(evaluation>max) {
            	// on prend le max entre les coups possibles de la situation de depart
                max=evaluation;
                d=situationDepart.possibiltesDeplacement.get(i);
            }
        }
        // on retoune le coup qui sera joue par l'ordi
        return d;

    }

    public int max(int a, int b) { return Math.max(a, b); }
    public int min(int a, int b) { return Math.min(a, b); }
    
    
    
}
