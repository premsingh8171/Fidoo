package com.fidoo.user.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.fidoo.user.R;
import com.fidoo.user.api_request_retrofit.ApiCalls;
import com.fidoo.user.data.CheckConnectivity;
import com.fidoo.user.data.session.SessionNotNull;
import com.fidoo.user.data.session.SessionTwiclo;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public  abstract class BaseActivity extends AppCompatActivity implements Handler.Callback {

    public Activity _context = null;
    public Handler _handler = null;
    private UiHandleMethods mUihandler;
    private ProgressDialog _progressDlg;
    private Vibrator _vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this will keep the screen the awake until the app is running
        //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //  check for the permission
        //  addOverlay();

        // lock the home key

        super.onCreate(savedInstanceState);
        mUihandler = new UiHandleMethods(this);
        _context = this;
        _vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        _handler = new Handler(this);


    }
    public boolean isNetworkConnected() {
        return new CheckConnectivity(this).isNetworkAvailable();
    }
    public ApiCalls getRestfullInstance() {
        return new ApiCalls(this);
    }

    @Override
    protected void onDestroy() {

        closeProgress();
        /*  try {
            if (_vibrator != null)
                _vibrator.cancel();
        } catch (Exception e) {
        }*/
        _vibrator = null;

        super.onDestroy();
    }
    public List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void goForVerificationScreen(Class<?> mClass, String accessToken, String id, String mobile, String otp) {

        Intent i = new Intent(this, mClass);
        i.putExtra("accessToken",accessToken);
        i.putExtra("id",id);
        i.putExtra("mobile",mobile);
        i.putExtra("otp",otp);
        startActivity(i);
        this.finish();
        //    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void dismissIOSProgress() {
        /*    try {
            if (mKProgressHUD != null) {
                if (mKProgressHUD.isShowing()) {
                    mKProgressHUD.dismiss();
                }
            }
        } catch (Exception ex) {
            Log.wtf("IOS_error_dismiss", ex.getCause());
        }
*/
        if (_progressDlg == null) {
            return;
        }
        _progressDlg.dismiss();
        _progressDlg = null;
    }

    public SessionTwiclo getSessionInstance() {
        return new SessionTwiclo(this);
    }

    public SessionNotNull getSessionInstanceNotNull() {
        return new SessionNotNull(this);
    }

    public void showIOSProgress() {
        /*   try {
            mKProgressHUD = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    //  .setLabel("Please wait")
                    //  .setDetailsLabel(msg)
                    .setCancellable(true)

                    .setAnimationSpeed(1)
                    .setDimAmount(.2f)
                    .show();
        } catch (Exception ex) {
            Log.wtf("IOS_error_starting", ex.getCause());
        }*/
        /* _progressDlg = new ProgressDialog(_context,R.style.AppTheme);
        _progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDlg.setCancelable(true);
        _progressDlg.show();*/
        closeProgress();
        _progressDlg = new ProgressDialog(this, R.style.TransparentProgressDialog);
        _progressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDlg.setCancelable(false);
        _progressDlg.show();
    }
/*    public void showProgress(boolean cancelable) {
        closeProgress();
        _progressDlg = new ProgressDialog(_context, R.style.MyTheme);
        _progressDlg.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        _progressDlg.setCancelable(cancelable);
        _progressDlg.show();

    }*/

    /* //  public void showProgress() {
        showProgress(false);
    }*/

    public String getGeoAddressFromLatLong(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            //   String knownName = addresses.get(0).getFeatureName(); // Only if available else return

            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void closeProgress() {

        if (_progressDlg == null) {
            return;
        }
        _progressDlg.dismiss();
        _progressDlg = null;
    }

    public void showAlertDialog(String msg) {

        AlertDialog.Builder customBuilder = new AlertDialog.Builder(_context);

        customBuilder.setTitle(getString(R.string.app_name));
        customBuilder.setMessage(msg);
        customBuilder.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // MyActivity.this.finish();
            }
        });
        customBuilder.setIcon(R.drawable.logo);
        customBuilder.setCancelable(false);
        AlertDialog dialog = customBuilder.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (b != null) {
            b.setBackgroundColor(getResources().getColor(R.color.white));
        }
        dialog.show();
    }


    /**
     * show toast
     *
     * @param toast_string
     */
    public void showToast(String toast_string) {
        Toast.makeText(_context, toast_string, Toast.LENGTH_SHORT).show();
    }

    public void showInternetToast() {
        Toast.makeText(_context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String toast_string) {
        Toast.makeText(_context, toast_string, Toast.LENGTH_LONG).show();
    }

    /*  public void vibrate() {

        if (_vibrator != null)
            _vibrator.vibrate(500);
    }*/

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            default:
                break;
        }
        return false;
    }

    public UiHandleMethods getUiHandlerMethod() {
        return mUihandler;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

