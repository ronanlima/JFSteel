package br.com.home.maildeliveryjfsteel.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronan.lima on 19/07/17.
 */

public class PermissionUtils {

    /**
     * Verifica se o usuário já concedeu permissão para a funcionalidade solicitada.
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @return
     */
    public static boolean validate(AppCompatActivity activity, int requestCode, String... permissions) {
        List<String> list = new ArrayList<>();
        for (String perm : permissions) {
            boolean ok = ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED;
            if (!ok) {
                list.add(perm);
            }
        }
        if (list.isEmpty()) {
            return true;
        }
        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);
        ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        return false;
    }

}
