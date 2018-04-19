package com.fravier.travel.global;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.fravier.travel.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by francis on 10/05/2016.
 */
public class CustomAppCompatActivity extends AppCompatActivity {
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public Context context;
    public String endpoint;
    public Intent intent;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;


    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_line);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setProgress(String title, String content) {
        builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .cancelable(false)
                .progress(true, 0);


        dialog = builder.build();
    }

    public void prepareData(Map<String, String> parameters, Map<String, String> headers, String endpoint, Intent intent) {
        this.intent = intent;
        this.endpoint = endpoint;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        performData(GlobalVars.BASE_URL + endpoint, parameters, headers);
    }

    public void performData(String url, Map<String, String> params, Map<String, String> headers) {
        System.out.println(url);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String responseString) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    boolean error = jsonObject.getBoolean("error");
//                    int code = jsonObject.getInt("code");

                    if (!error) {
                        JSONObject jsonMessage = jsonObject.getJSONArray("message").getJSONObject(0);


                        parseJSON(jsonMessage);
                    } else {
                        String strMessage = jsonObject.getString("message");
                        Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        stringRequest.setParams(params);
        stringRequest.setHeaders(headers);
        requestQueue.add(stringRequest);
    }

    public void parseJSON(JSONObject result) {
        if (result.length() > 0) {
            try {
//                String status = result.getString("status");
//                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();

                switch (getEndpoint()) {
                    case "/agents/login":
                        GlobalVars.stockist_id = result.getString("STOCKIST_ID");
                        String firstName = result.getString("OWNER_FIRSTNAME");
                        String secondName = result.getString("OWNER_LASTNAME");
                        GlobalVars.username = firstName + " " + secondName;
                        GlobalVars.telephone = result.getString("OWNER_PHONENUMBER");

                        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();

                    case "ministatement":
//                        JSONArray statement = result.getJSONArray("statement");
//                        GlobalVars.MINI_STATEMENT = statement;
//                        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                        finish();

                        break;


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Error in parsing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Intent getIntent() {
        return intent;
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public static String[] getStringArray(JSONArray jsonArray) {
        String[] stringArray = null;
        int length = jsonArray.length();
        if (jsonArray != null) {
            stringArray = new String[length];
            for (int i = 0; i < length; i++) {
                stringArray[i] = jsonArray.optString(i);
            }
        }
        return stringArray;
    }

//    private void showOutage(String title, String message) {
//        new MaterialDialog.Builder(this)
//                .title(title)
//                .content(message)
//                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
//                .cancelable(false)
//                .titleGravity(GravityEnum.CENTER)
//                .positiveColorRes(R.color.colorPrimary)
////                .icon(getResources().getDrawable(R.drawable.ic_launcher))
////                .limitIconToDefaultSize()
//                .positiveText(getResources().getString(R.string.proceed))
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        Intent out = new Intent(getBaseContext(), SignIn.class);
//                        out.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(out);
//                        finish();
//                    }
//                })
//                .show();
//    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        GlobalVars.lastActivity = new Date().getTime();
//        return super.dispatchTouchEvent(ev);
//    }

    @Override
    public void onResume() {
        super.onResume();
        String className = getLocalClassName();

        switch (className) {
            case "SignIn":

                break;
            case "Launcher":

                break;
            default:
//                initiateTimeout();
                break;
        }
//
    }

//    public void initiateTimeout() {
//        if ((new Date().getTime() - GlobalVars.lastActivity) > GlobalVars.timeout) {
//            showOutage(getResources().getString(R.string.app_name),
//                    getResources().getString(R.string.timeout));
//        }
//    }
}
