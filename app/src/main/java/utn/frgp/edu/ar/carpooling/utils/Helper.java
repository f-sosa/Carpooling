package utn.frgp.edu.ar.carpooling.utils;

import android.view.View;
import android.widget.ImageView;

import utn.frgp.edu.ar.carpooling.R;

public class Helper {

    public static String RemoverCaracteresSQLInjection (String text) {
        return text.replace("\'","").replace("\"","");
    }

    public static void MostrarEstrellas (ImageView st1, ImageView st2, ImageView st3, ImageView st4, ImageView st5, Float calificacion) {

        if(calificacion > 0 && calificacion < 1) st1.setBackgroundResource(R.mipmap.halfstar_foreground);
        if(calificacion >= 1) st1.setBackgroundResource(R.mipmap.fullstar_foreground);

        if(calificacion > 1 && calificacion < 2) st2.setBackgroundResource(R.mipmap.halfstar_foreground);
        if(calificacion >= 2) st2.setBackgroundResource(R.mipmap.fullstar_foreground);

        if(calificacion > 2 && calificacion < 3) st3.setBackgroundResource(R.mipmap.halfstar_foreground);
        if(calificacion >= 3) st3.setBackgroundResource(R.mipmap.fullstar_foreground);

        if(calificacion > 3 && calificacion < 4) st4.setBackgroundResource(R.mipmap.halfstar_foreground);
        if(calificacion >= 4) st4.setBackgroundResource(R.mipmap.fullstar_foreground);

        if(calificacion > 4 && calificacion < 5) st5.setBackgroundResource(R.mipmap.halfstar_foreground);
        if(calificacion >= 5) st5.setBackgroundResource(R.mipmap.fullstar_foreground);


    }
}
