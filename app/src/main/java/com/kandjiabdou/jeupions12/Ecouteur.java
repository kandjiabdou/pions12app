package com.kandjiabdou.jeupions12;
public class Ecouteur {
    public interface EcouteurDeplacement {
        void notifierDeplacement(Deplacement deplacement);
        void notifierInfo();
        void notifierSon(int nbSon);
    }
}
