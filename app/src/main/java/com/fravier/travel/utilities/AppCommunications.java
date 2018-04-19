package com.fravier.travel.utilities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;


/**
 * Created by francis on 02/06/2016.
 */
public class AppCommunications {
    public Context context;

    public AppCommunications(Context context) {
        this.context = context;
    }

    public void makePhoneCall(String phoneNumber) {
        String number = "tel:" + phoneNumber.trim();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(callIntent);
    }

    public void sendEmail(String emailAddress, String subject, String body) {
        String[] addresses = new String[1];
        addresses[0] = emailAddress;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void openSocialMedia(String socialMedia) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(socialMedia));
        context.startActivity(intent);
    }

}
