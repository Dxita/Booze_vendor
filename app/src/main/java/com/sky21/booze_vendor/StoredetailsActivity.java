package com.sky21.booze_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoredetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
   EditText store_name,storeaddress, licence_no;
    TextView valid_till;
    ImageView backspace;
    CircleImageView circleImageView;
    Button submit;
    DatePickerDialog datePickerDialog;
    String add;
    ProgressBar progressBar;
    String token,state;
    private final int GALLERY = 1;
    Bitmap bitmap;
    private RequestQueue rQueue;
    private String upload_URL = "https://boozeapp.co/Booze-App-Api/api/store/add";

    ArrayList<HashMap<String, String>> storeList = new ArrayList<>();
    String[] country = { "Uttar Pradesh", "Maharashtra", "Bihar", "West Bengal", "Madhya Pradesh","Tamil Nadu","Rajasthan","Karnataka","Gujarat","Andhra Pradesh","Odisha","Telangana","Kerala","Jharkhand","Assam","Punjab","Chhattisgarh","Haryana","Uttarakhand","Himachal Pradesh","Tripura","Meghalaya","Manipur","Nagaland","Goa","Arunachal Pradesh","Mizoram","Sikkim","Delhi","Jammu and Kashmir","Puducherry","Chandigarh","Dadra and Nagar Haveli and Daman and Diu","\tAndaman and Nicobar Islands","Ladakh","Lakshadweep"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storedetails);
        getSupportActionBar().hide();

        final Intent intent=getIntent();
        add=intent.getStringExtra("ADDRESS");

   // token=SharedHelper.getKey(StoredetailsActivity.this,"token");
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        submit=findViewById(R.id.submit);
        circleImageView=findViewById(R.id.image);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(galleryIntent, GALLERY);

            }
        });

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


     storeaddress.setText(add);
        /*storeaddress.setOnClickListener(new View.OnClickListener() {
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
*/

        store_name=findViewById(R.id.store_name);
        licence_no=findViewById(R.id.licence_no);
        valid_till=findViewById(R.id.valid_till);


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


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data(bitmap);
            }
        });
    }

    private void data(final Bitmap bitmap) {
//        progressBar.setVisibility(View.VISIBLE);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("ressssssoo",new String(response.data));
                        rQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            jsonObject.toString().replace("\\\\","");


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
                   //    progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
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
            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(StoredetailsActivity.this);
        rQueue.add(volleyMultipartRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("outputFormat",
                        Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY);

            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access Gallery.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {


                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    // imageView.setImageBitmap(bitmap);
                    circleImageView.setVisibility(View.VISIBLE);
                    circleImageView.setImageBitmap(bitmap);

                    //securelayout.setVisibility(View.GONE);



                    //   data();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(StoredetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void api() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url="https://boozeapp.co/Booze-App-Api/api/store/add";
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
