package android.behnamnasehi.chatApp.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.TextView;
import android.behnamnasehi.chatApp.R;

import androidx.core.view.ViewCompat;


import com.google.android.material.snackbar.Snackbar;

public class SnackBarHelper {

    public static void configSnackbar(Context context, Snackbar snack) {
        addMargins(snack);
        setRoundBordersBg(context, snack);
        setTypeFace(context, snack);
        setToRtl(snack);
        ViewCompat.setElevation(snack.getView(), 6f);
        changeActionTextColor(snack, context);
    }

    private static void setToRtl(Snackbar snackbar) {
        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
    }

    private static void changeActionTextColor(Snackbar snackbar, Context context) {
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorWhite));
    }

    private static void addMargins(Snackbar snack) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 50);
        snack.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.getView().setBackground(context.getDrawable(R.drawable.contianer_snackbar));
        }
    }

    private static void setTypeFace(Context context, Snackbar snackbar) {
        TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        TextView snackbarActionTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/dana_regular.ttf");
        Typeface bold = Typeface.createFromAsset(context.getAssets(), "fonts/dana_bold.ttf");
        tv.setTypeface(font);
        snackbarActionTextView.setTypeface(bold);
    }
}
