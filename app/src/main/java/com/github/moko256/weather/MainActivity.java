package com.github.moko256.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    String[] selectobj={
            "Tokyo,jp",
            "Osaka,jp",
            "Hachioji,jp",
            "Kunitachi,jp",
            "Washington,us",
            "Hawaii,us",
            "Ottawa,ca",
            "Tokyo,jp"
    };
    String cis="Tokyo",cos="jp";
    LinearLayout weather_disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final Toolbar myActionBar=setSupportActionBar(R.id.tool_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        TabLayout tab=(TabLayout)findViewById(R.id.tab_layout);
        tab.addTab(tab.newTab().setText(R.string.Toolbar_Tab1));
        tab.addTab(tab.newTab().setText(R.string.Toolbar_Tab2));
        weather_disp=(LinearLayout)findViewById(R.id.as);
        setting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class myPref extends AppCompatActivity{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.preference);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_reload:
                setting();
                break;
            case R.id.action_fullScreen:
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case R.id.action_custWea:
                custwea();
                break;
            case R.id.action_settings:
                Intent nextActivity=new Intent(this, PrefsActivity.class);

                startActivity(nextActivity);
                break;
            case R.id.action_about:
                AlertDialog.Builder ab=new AlertDialog.Builder(this);
                ab.setTitle(R.string.action_about).setMessage("OpenWeatherMap - actual and forecast weather").setPositiveButton("OK", null).create().show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void custwea(){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout=inflater.inflate(R.layout.custwea_prompt,	(ViewGroup)findViewById(R.id.custweaPrompt));

        AlertDialog.Builder custweaPrompt=new AlertDialog.Builder(MainActivity.this);
        custweaPrompt.setTitle("City and Country");

        custweaPrompt.setView(layout);

        final EditText ci=(EditText)layout.findViewById(R.id.custweaPrompt_city);
        final EditText co=(EditText)layout.findViewById(R.id.custweaPrompt_country);

        ci.setHint(cis);
        co.setHint(cos);

        custweaPrompt.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which){
                cis=ci.getText().toString();
                cos=co.getText().toString();
                selectobj[7]=cis+","+cos;
            }
        });

        custweaPrompt.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which){
            }
        });
        custweaPrompt.setCancelable(true).create().show();

    }

    public void getJson(final String scurl){

        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                Request request = new Request.Builder().url(scurl).get().build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    cancel(true);
                }

                return result;
            }
            protected void onPostExecute(String result) {
                callback(result);
            }
        }.execute();
    }

    public void callback(String ss){
        try {
        int i=0;
        JSONObject json = new JSONObject(ss);
        String nameT=json.getJSONObject("city").getString("name");
        JSONObject list=json.getJSONArray("list").getJSONObject(0);

        TextView nam=new TextView(this),temp=new TextView(this),tMin=new TextView(this),tMax=new TextView(this),description=new TextView(this),icon=new TextView(this),wmain=new TextView(this),pressure=new TextView(this),humidity=new TextView(this);

    /*
    public ImageView showIconFunc(String num){
        String url="weather_icon/"+num+".svg";
        try{
            ImageView showIcon=setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(url)));
        }catch(IOException e){
            Log.d("Assets", "Error");
        }
        return showIcon;
    }

    switch(m){
        case 0:
        */
            weather_disp.removeAllViews();

            JSONObject te=list.getJSONObject("temp");
            JSONObject we=list.getJSONArray("weather").getJSONObject(0);

            nam.setText(nameT);
            nam.setTextSize(32);
            nam.setTextColor(Color.BLACK);

            temp.setText(String.valueOf((int) Float.parseFloat(te.getString("day")))+"°");
            temp.setTextSize(32);
            temp.setTextColor(Color.BLACK);

            wmain.setText(we.getString("main"));
            wmain.setTextSize(24);
            wmain.setTextColor(Color.BLACK);

            tMax.setText("max "+te.getString("max")+"°");
            tMax.setTextSize(16);
            tMax.setTextColor(Color.RED);

            tMin.setText("min "+te.getString("min")+"°");
            tMin.setTextSize(16);
            tMin.setTextColor(Color.BLUE);

            icon.setText(we.getString("icon"));

            pressure.setText(list.getString("pressure")+" hPa");
            pressure.setTextSize(16);
            pressure.setTextColor(Color.BLACK);

            humidity.setText(list.getString("humidity")+" %");
            humidity.setTextSize(16);
            humidity.setTextColor(Color.BLACK);

            description.setText(we.getString("description")+".");
            description.setTextSize(16);
            description.setTextColor(Color.BLACK);

            weather_disp.addView(nam);
            //weather_disp.addView(showIconFunc(svg),createParam("100px","100px"));
            weather_disp.addView(temp);
            weather_disp.addView(wmain);
            weather_disp.addView(tMax);
            weather_disp.addView(tMin);
            weather_disp.addView(pressure);
            weather_disp.addView(humidity);
            weather_disp.addView(description);

        } catch (JSONException e) {
            Snackbar.make(weather_disp,e.toString(),Snackbar.LENGTH_SHORT).show();
        }
}

    public void setting() {
        Spinner spinner = (Spinner) findViewById(R.id.toolbar_spinner);
        int idx = spinner.getSelectedItemPosition();
        TextView pwt = new TextView(this);
        weather_disp.removeAllViews();
        pwt.setText(R.string.wait_text);
        weather_disp.addView(pwt);
        ConnectivityManager cm = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo()!=null) {
            getJson("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + selectobj[idx] + "&mode=json&cnt=7&units=metric&APPID="+ BuildConfig.OPEN_WEATHER_MAP_APP_ID);
        }else {
            Snackbar.make(weather_disp, R.string.offLineText, Snackbar.LENGTH_SHORT).show();
        }
    }
}