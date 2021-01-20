package com.kandjiabdou.jeupions12;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;


public class PopupFinPartie extends Dialog {

    private TextView texteQuiAgagne;
    public PopupFinPartie(Activity activity) {
        super(activity, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.popup_fin_partie);
        this.texteQuiAgagne=findViewById(R.id.textQuiAgagne);
    }

    public void setTexte(String nouveauTexte){
        this.texteQuiAgagne.setText(nouveauTexte);
    }
    public void build(String texte){
        setTexte(texte);
        show();
    }
}
