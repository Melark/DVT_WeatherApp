package dvt.test.weatherapp;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by marka on 2018/01/29.
 */

public class ErrorException extends Exception {
    public ErrorException(String errorTitle, String errorMessage, Context currentContext) {
        AlertDialog.Builder alertBuilder = null;

        alertBuilder.setTitle(errorTitle)
                .setMessage(errorMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
