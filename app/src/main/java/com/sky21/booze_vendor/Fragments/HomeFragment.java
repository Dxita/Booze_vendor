package com.sky21.booze_vendor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.bumptech.glide.Glide;
import com.sky21.booze_vendor.R;
import com.sky21.booze_vendor.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
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
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    String state,token;

    String[] category_list = {"Vodka","Gin","Tequila","Rum","Whiskey","Beer","Brandy"};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        //   unselect_all_tv=findViewById(R.id.unselect_all_tv);
        token= SharedHelper.getKey(getActivity(),"token");

        /*final Intent intent=getIntent();
        state=intent.getStringExtra("STATE");*/
        list = new ArrayList<String>();

        backspace=v.findViewById(R.id.addressId);
        progressBar=v.findViewById(R.id.progressbar);

        category_rv=v.findViewById(R.id.category_rv);


        layoutManager = new LinearLayoutManager(getActivity());
        category_rv.setLayoutManager(layoutManager);
        category_rv.setItemAnimator(new DefaultItemAnimator());


        api();




        return v;
    }

    private void api() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://boozeapp.co/Booze-App-Api/api/states/products?state_id=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true"))
                    {

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0; i<jsonArray.length(); i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            HashMap<String,String>param=new HashMap<>();
                            param.put("state",object.getString("state"));

                            arrayList.add(param);


                            JSONArray jsonArray1=object.getJSONArray("products");
                            {
                                for (int j=0; j<jsonArray1.length();  j++)
                                {
                                    JSONObject object1=jsonArray1.getJSONObject(j);
                                    HashMap<String,String>map=new HashMap<>();

                                    map.put("id",object1.getString("id"));
                                    map.put("state_id",object1.getString("state_id"));
                                    map.put("name",object1.getString("name"));
                                    map.put("price",object1.getString("price"));
                                    map.put("size",object1.getString("size"));
                                    map.put("image",object1.getString("image"));


                                    Log.d("mapppppppppppppp", String.valueOf(map));
                                    storeList.add(map);


                                    //  Toast.makeText(MainActivity.this, ""+response , Toast.LENGTH_SHORT).show();
                                }

                            }
                            Mainadapter mainadapter = new Mainadapter(getActivity(), storeList);
                            category_rv.setAdapter(mainadapter);
                        }


                    }


                    else {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
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
            holder.price.setText("Price"+" "+getString(R.string.rupee)+map.get("price"));

            holder.size.setText("Size"+" "+map.get("size")+"ml");
            Glide.with(context).load(map.get("image")).into(holder.img);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked())
                    {
                        add(map.get("id"));
                    }
                    else
                    {
                        remove(map.get("id"));

                    }
                }
            });



        }


        @Override
        public int getItemCount() {
            return storeList.size();
        }
    }

    private void remove(final String id) {
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        String url="https://boozeapp.co/Booze-App-Api/api/remove-product";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true"))
                    {
                        Toast.makeText(getActivity(), "Product removed successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
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
                Map<String,String>map=new HashMap<>();
                map.put("store_id","1");
                map.put("product_id",id);
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void add(final String id) {
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        String url="https://boozeapp.co/Booze-App-Api/api/add-product";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true"))
                    {
                        Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
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
                Map<String,String>map=new HashMap<>();
                map.put("store_id","1");
                map.put("product_id",id);
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price,size;
        CheckBox checkBox;
        ImageView img;
        public MyViewHolder(View view) {
            super(view);

            name=view.findViewById(R.id.name);
            price=view.findViewById(R.id.price);
            size=view.findViewById(R.id.size);
            checkBox=view.findViewById(R.id.checkbox);
            img=view.findViewById(R.id.img);
        }
    }


}
