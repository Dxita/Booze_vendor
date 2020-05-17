package com.sky21.booze_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView backspace,unselect_all_tv;
    TextView category_tv,brand_tv,price_tv;
    RecyclerView category_rv;
    List<String> list;
    ImageView profile,search;

    Button add_products, clear_all;
RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    ArrayList<HashMap<String, String>> storeList = new ArrayList<>();
String state,token;
    Mainadapter mainadapter;
    String[] category_list = {"Vodka","Gin","Tequila","Rum","Whiskey","Beer","Brandy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //   unselect_all_tv=findViewById(R.id.unselect_all_tv);
        token=SharedHelper.getKey(MainActivity.this,"token");

        final Intent intent=getIntent();
        state=intent.getStringExtra("STATE");
        list = new ArrayList<String>();
        profile=findViewById(R.id.myprofileId);
        search=findViewById(R.id.searchView);
        backspace=findViewById(R.id.addressId);
        progressBar=findViewById(R.id.progressbar);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       category_rv=findViewById(R.id.category_rv);
       add_products=findViewById(R.id.add_products);
       clear_all=findViewById(R.id.clear_all);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(getApplicationContext());
        category_rv.setLayoutManager(layoutManager);
        category_rv.setItemAnimator(new DefaultItemAnimator());


        api();


       add_products.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MainActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();
               storeList.clear();

              api();
              mainadapter.notifyDataSetChanged();

           }
       });

       clear_all.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              storeList.clear();
                api();
                mainadapter.notifyDataSetChanged();
               Toast.makeText(MainActivity.this, "Select products to add!", Toast.LENGTH_SHORT).show();
           }
       });

       /* unselect_all_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategoryRecyclerView();
            }
        });*/


    }

    private void api() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://missionlockdown.com/BoozeApp/api/states/products?state_id=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    if (!(storeList== null))
                    {
                        storeList.clear();
                    }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true"))
                    {

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0; i<jsonArray.length(); i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);

                            JSONArray jsonArray1=object.getJSONArray("products");
                            {
                                for (int j=0; j<jsonArray.length(); j++)
                                {
                                    JSONObject object1=jsonArray1.getJSONObject(j);
                                    HashMap<String,String>map=new HashMap<>();

                                    map.put("id",object1.getString("id"));
                                    map.put("state_id",object1.getString("state_id"));
                                    map.put("name",object1.getString("name"));
                                    map.put("price",object1.getString("price"));
                                    map.put("quantity",object1.getString("quantity"));

                                    Log.d("mapppppppppppppp", String.valueOf(map));
                                    storeList.add(map);

                                    //  Toast.makeText(MainActivity.this, ""+response , Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                         mainadapter = new Mainadapter(getApplicationContext(), storeList);
                        category_rv.setAdapter(mainadapter);

                        }


                    else {
                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
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

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class Mainadapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> storelist = new ArrayList<>();

        public Mainadapter(Context context, ArrayList<HashMap<String, String>> storeList) {
            this.context=context;
            this.storelist=storeList ;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.filter_item_list,null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final HashMap<String, String> map = storelist.get(position);
            holder.name.setText(map.get("name"));
            holder.price.setText(getString(R.string.rupee)+map.get("price"));

            holder.size.setText(map.get("quantity")+"ml");



        }


        @Override
        public int getItemCount() {
            return storeList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price,size;
        public MyViewHolder(View view) {
            super(view);

            name=view.findViewById(R.id.name);
            price=view.findViewById(R.id.price);
            size=view.findViewById(R.id.size);
        }
    }
}
