package com.sky21.booze_vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StoredetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
   EditText store_name, licence_no;
    TextView storeaddress,valid_till;
    ImageView backspace;
    Button submit;
    DatePickerDialog datePickerDialog;
    String add;
    ProgressBar progressBar;
    String token,state;
    ArrayList<HashMap<String, String>> storeList = new ArrayList<>();
    String[] country = { "Uttar Pradesh", "Maharashtra", "Bihar", "West Bengal", "Madhya Pradesh","Tamil Nadu","Rajasthan","Karnataka","Gujarat","Andhra Pradesh","Odisha","Telangana","Kerala","Jharkhand","Assam","Punjab","Chhattisgarh","Haryana","Uttarakhand","Himachal Pradesh","Tripura","Meghalaya","Manipur","Nagaland","Goa","Arunachal Pradesh","Mizoram","Sikkim","Delhi","Jammu and Kashmir","Puducherry","Chandigarh","Dadra and Nagar Haveli and Daman and Diu","\tAndaman and Nicobar Islands","Ladakh","Lakshadweep"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storedetails);
        getSupportActionBar().hide();

        final Intent intent=getIntent();
        add=intent.getStringExtra("ADD");

   // token=SharedHelper.getKey(StoredetailsActivity.this,"token");
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        submit=findViewById(R.id.submit);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        storeaddress=findViewById(R.id.adddress);

        backspace=findViewById(R.id.backspace);

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        storeaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sn = store_name.getText().toString();
                String ln = licence_no.getText().toString();
                String vt = valid_till.getText().toString();

                if (sn.isEmpty()) {
                    store_name.requestFocus();
                    store_name.setError("Enter your store name");

                } else if (ln.isEmpty()) {
                    licence_no.requestFocus();
                    licence_no.setError("Enter store licence number");
                } else if (vt.isEmpty()) {
                    valid_till.requestFocus();
                    valid_till.setError("Enter your password");
                } else {
                    Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                    intent.putExtra("NAME",store_name.getText().toString());
                    intent.putExtra("LIC",licence_no.getText().toString());
                    intent.putExtra("VALID",valid_till.getText().toString());
                    intent.putExtra("STATE",state);
                    startActivity(intent);
                }

            }
        });


        store_name=findViewById(R.id.store_name);
        licence_no=findViewById(R.id.licence_no);
        valid_till=findViewById(R.id.valid_till);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        valid_till.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(StoredetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        valid_till.setText(dayOfMonth + "/"
                                + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
    }

    private void api() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url="https://missionlockdown.com/BoozeApp/api/store/add";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);


                    Log.d("response",response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true"))
                    {
                        JSONObject jsonobj=jsonObject.getJSONObject("stores");
                        {

                            HashMap<String,String> params=new HashMap<>();
                            params.put("name",jsonobj.getString("name"));
                            params.put("address",jsonobj.getString("address"));
                            params.put("state_id",jsonobj.getString("name"));
                            params.put("license_number",jsonobj.getString("license_number"));
                            params.put("license_valid_till",jsonobj.getString("license_valid_till"));
                            params.put("id",jsonobj.getString("id"));

                        }

                        Toast.makeText(StoredetailsActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(StoredetailsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("name",store_name.getText().toString());
                map.put("address",add);
                map.put("state_id",state);
                map.put("license_number",licence_no.getText().toString());
                map.put("license_valid_till",valid_till.getText().toString());

                return map;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public void onClick(View v) {

    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        state=country[position];
        Toast.makeText(getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
