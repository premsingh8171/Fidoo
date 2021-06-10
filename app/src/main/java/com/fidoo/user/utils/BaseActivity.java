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
  //  public Handler _handler = null;
    private UiHandleMethods mUihandler;
    private ProgressDialog _progressDlg;
    private Vibrator _vibrator;
    private static String Address = "", City = "", State = "", Country = "", Locality = "", Pincode = "",
            mAdminArea = "", mSubAdminArea = "", mSubLocality = "", mThoroughfare = "", mSubThoroughfare = "",
            mCountryCode = "", mPhone = "", mUrl = "", mFeatureName = "";

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
      //  _handler = new Handler(this);


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
        closeProgress();
        _progressDlg = new ProgressDialog(this, R.style.TransparentProgressDialog);
        _progressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDlg.setCancelable(false);
        _progressDlg.show();
    }


//    public String getGeoAddressFromLatLong(double latitude, double longitude) {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            //   String knownName = addresses.get(0).getFeatureName(); // Only if available else return
//
//            return address;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

    public String getGeoAddressFromLatLong(double latitude, double longitude) {

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            final List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                android.location.Address address = addressList.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    Address += (address.getAddressLine(i)) + (",");
                }
                try {

                    if (Address != null) {
                        Address = Address.substring(0, Address.length() - 1);
                    } else {
                        Address = "";
                    }

                    if (address.getLocality() != null) {
                        City = address.getLocality();
                    } else {
                        City = "";
                    }

                    if (address.getAdminArea() != null) {
                        State = address.getAdminArea();
                    } else {
                        State = "";
                    }

                    if (address.getCountryName() != null) {
                        Country = address.getCountryName();
                    } else {
                        Country = "";
                    }

                    if (address.getLocality() != null) {
                        Locality = address.getLocality();
                    } else {
                        Locality = "";
                    }

                    if (address.getPostalCode() != null) {
                        Pincode = address.getPostalCode();
                    } else {
                        Pincode = "";

                    }

                    if (address.getAdminArea() != null) {
                        mAdminArea = address.getAdminArea();
                    } else {
                        mAdminArea = "";

                    }

                    if (address.getSubAdminArea() != null) {
                        mSubAdminArea = address.getSubAdminArea();
                    } else {
                        mSubAdminArea = "";

                    }
                    if (address.getSubLocality() != null) {
                        mSubLocality = address.getSubLocality();
                    } else {
                        mSubLocality = "";

                    }

                    if (address.getThoroughfare() != null) {
                        mThoroughfare = address.getPostalCode();
                    } else {
                        mThoroughfare = "";

                    }

                    if (address.getSubThoroughfare() != null) {
                        mSubThoroughfare = address.getSubThoroughfare();
                    } else {
                        mSubThoroughfare = "";

                    }

                    if (address.getPhone() != null) {
                        mPhone = address.getPhone();
                    } else {
                        mPhone = "";
                    }

                    if (address.getUrl() != null) {
                        mUrl = address.getUrl();
                    } else {
                        mUrl = "";

                    }
                    if (address.getFeatureName() != null) {
                        mFeatureName = address.getFeatureName();
                    } else {
                        mFeatureName = "";

                    }

                } catch (Exception ex) {
                    if (addressList.get(0).getAddressLine(0) != null) {
                        Address = addressList.get(0).getAddressLine(0);//// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } else {
                        Address = "";
                    }

                    if (Address != null) {
                        Address = Address.substring(0, Address.length() - 1);
                    } else {
                        Address = "";
                    }

                    if (address.getLocality() != null) {
                        City = address.getLocality();
                    } else {
                        City = "";
                    }

                    if (address.getAdminArea() != null) {
                        State = address.getAdminArea();
                    } else {
                        State = "";
                    }

                    if (address.getCountryName() != null) {
                        Country = address.getCountryName();
                    } else {
                        Country = "";
                    }

                    if (address.getLocality() != null) {
                        Locality = address.getLocality();
                    } else {
                        Locality = "";
                    }

                    if (address.getPostalCode() != null) {
                        Pincode = address.getPostalCode();
                    } else {
                        Pincode = "";

                    }

                    if (address.getAdminArea() != null) {
                        mAdminArea = address.getAdminArea();
                    } else {
                        mAdminArea = "";

                    }

                    if (address.getSubAdminArea() != null) {
                        mSubAdminArea = address.getSubAdminArea();
                    } else {
                        mSubAdminArea = "";

                    }
                    if (address.getSubLocality() != null) {
                        mSubLocality = address.getSubLocality();
                    } else {
                        mSubLocality = "";

                    }

                    if (address.getThoroughfare() != null) {
                        mThoroughfare = address.getPostalCode();
                    } else {
                        mThoroughfare = "";

                    }

                    if (address.getSubThoroughfare() != null) {
                        mSubThoroughfare = address.getSubThoroughfare();
                    } else {
                        mSubThoroughfare = "";

                    }

                    if (address.getPhone() != null) {
                        mPhone = address.getPhone();
                    } else {
                        mPhone = "";
                    }

                    if (address.getUrl() != null) {
                        mUrl = address.getUrl();
                    } else {
                        mUrl = "";

                    }
                    if (address.getFeatureName() != null) {
                        mFeatureName = address.getFeatureName();
                    } else {
                        mFeatureName = "";

                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {

        }
        return Address;
    }

    public String getGeoAddressFromLatLong2(double latitude, double longitude) {

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            final List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                android.location.Address address = addressList.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    Address += (address.getAddressLine(i)) + (",");
                }
                try {

                    if (Address != null) {
                        Address = Address.substring(0, Address.length() - 1);
                    } else {
                        Address = "";
                    }

                    if (address.getLocality() != null) {
                        City = address.getLocality();
                    } else {
                        City = "";
                    }

                    if (address.getAdminArea() != null) {
                        State = address.getAdminArea();
                    } else {
                        State = "";
                    }

                    if (address.getCountryName() != null) {
                        Country = address.getCountryName();
                    } else {
                        Country = "";
                    }

                    if (address.getLocality() != null) {
                        Locality = address.getLocality();
                    } else {
                        Locality = "";
                    }

                    if (address.getPostalCode() != null) {
                        Pincode = address.getPostalCode();
                    } else {
                        Pincode = "";

                    }

                    if (address.getAdminArea() != null) {
                        mAdminArea = address.getAdminArea();
                    } else {
                        mAdminArea = "";

                    }

                    if (address.getSubAdminArea() != null) {
                        mSubAdminArea = address.getSubAdminArea();
                    } else {
                        mSubAdminArea = "";

                    }
                    if (address.getSubLocality() != null) {
                        mSubLocality = address.getSubLocality();
                    } else {
                        mSubLocality = "";

                    }

                    if (address.getThoroughfare() != null) {
                        mThoroughfare = address.getPostalCode();
                    } else {
                        mThoroughfare = "";

                    }

                    if (address.getSubThoroughfare() != null) {
                        mSubThoroughfare = address.getSubThoroughfare();
                    } else {
                        mSubThoroughfare = "";

                    }

                    if (address.getPhone() != null) {
                        mPhone = address.getPhone();
                    } else {
                        mPhone = "";
                    }

                    if (address.getUrl() != null) {
                        mUrl = address.getUrl();
                    } else {
                        mUrl = "";

                    }
                    if (address.getFeatureName() != null) {
                        mFeatureName = address.getFeatureName();
                    } else {
                        mFeatureName = "";

                    }

                } catch (Exception ex) {
                    if (addressList.get(0).getAddressLine(0) != null) {
                        Address = addressList.get(0).getAddressLine(0);//// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } else {
                        Address = "";
                    }

                    if (Address != null) {
                        Address = Address.substring(0, Address.length() - 1);
                    } else {
                        Address = "";
                    }

                    if (address.getLocality() != null) {
                        City = address.getLocality();
                    } else {
                        City = "";
                    }

                    if (address.getAdminArea() != null) {
                        State = address.getAdminArea();
                    } else {
                        State = "";
                    }

                    if (address.getCountryName() != null) {
                        Country = address.getCountryName();
                    } else {
                        Country = "";
                    }

                    if (address.getLocality() != null) {
                        Locality = address.getLocality();
                    } else {
                        Locality = "";
                    }

                    if (address.getPostalCode() != null) {
                        Pincode = address.getPostalCode();
                    } else {
                        Pincode = "";

                    }

                    if (address.getAdminArea() != null) {
                        mAdminArea = address.getAdminArea();
                    } else {
                        mAdminArea = "";

                    }

                    if (address.getSubAdminArea() != null) {
                        mSubAdminArea = address.getSubAdminArea();
                    } else {
                        mSubAdminArea = "";

                    }
                    if (address.getSubLocality() != null) {
                        mSubLocality = address.getSubLocality();
                    } else {
                        mSubLocality = "";

                    }

                    if (address.getThoroughfare() != null) {
                        mThoroughfare = address.getPostalCode();
                    } else {
                        mThoroughfare = "";

                    }

                    if (address.getSubThoroughfare() != null) {
                        mSubThoroughfare = address.getSubThoroughfare();
                    } else {
                        mSubThoroughfare = "";

                    }

                    if (address.getPhone() != null) {
                        mPhone = address.getPhone();
                    } else {
                        mPhone = "";
                    }

                    if (address.getUrl() != null) {
                        mUrl = address.getUrl();
                    } else {
                        mUrl = "";

                    }
                    if (address.getFeatureName() != null) {
                        mFeatureName = address.getFeatureName();
                    } else {
                        mFeatureName = "";

                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {

        }
        return null;
    }

    public String getAddress() { return Address; }

    public String getCity() { return City; }

    public String getState() {
        return State;
    }

    public String getCountry() {
        return Country;
    }

    public String getLocality() {
        return Locality;
    }

    public String getPincode() {
        return Pincode;
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

