package com.sky21.booze_vendor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sky21.booze_vendor.EditProfileActivity;
import com.sky21.booze_vendor.Login.WelcomeActivity;
import com.sky21.booze_vendor.ProfileActivity;
import com.sky21.booze_vendor.R;
import com.sky21.booze_vendor.SharedHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    CardView logout,orders,settings;
    TextView backspace;
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_account, container, false);
        logout=v.findViewById(R.id.c5);
        orders=v.findViewById(R.id.c2);
        settings=v.findViewById(R.id.c4);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout_api();
                /*Intent intent=new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);*/

            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });


        return v;
    }

    private void logout_api()  {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://boozeapp.co/Booze-App-Api/api/logout";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true")) {

                        SharedHelper.putKey(getActivity(),"loggedin","false");

                        Toast.makeText(getActivity(), "Logout successfully!", Toast.LENGTH_SHORT).show();
                    /*  SharedPreferences preferences = getSharedElementEnterTransition("loginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = SharedPreferences.edit();
                        editor.clear();
                        editor.apply();*/
                        Intent intent = new Intent(getActivity(),
                                WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //finish();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getActivity(), "token"));
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
