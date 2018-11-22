package com.example.diegocasas.copyprogressbar;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

public class CueMsg {

    public Context context;

    public CueMsg(Context context){
        this.context = context;
    }
    public void cueError(String msg){
        Cue.init()
                .with(context)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.LONG)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#f44336"),
                        Color.parseColor("#ffffff"),
                        Color.parseColor("#f44336"))
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
    public void cueCorrect(String msg){
        Cue.init()
                .with(context)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#00BFA5"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#00BFA5")) //contorno
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
    public void cueWarning(String msg){
        Cue.init()
                .with(context)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#FFB300"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#FFB300")) //contorno
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
}