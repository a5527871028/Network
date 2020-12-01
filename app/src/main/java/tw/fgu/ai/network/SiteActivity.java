package tw.fgu.ai.network;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SiteActivity extends AppCompatActivity {

    String name,id;
    ActionBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Bundle bundle = getIntent().getExtras();
        id=bundle.getString("id");

        NetworkTask networkTask=new NetworkTask();
        networkTask.execute("http://www.yichengtech.tw/twblogs/tw_site_info.php?siteID="+id);

        bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        bar.setTitle(name);
        bar.setSubtitle("subtitle");
        //bar.hide();
    }

    public class NetworkTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String resultString;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(SiteActivity.this);
            dialog.setCancelable(false);
            dialog = ProgressDialog.show(SiteActivity.this, "連線中", "Wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                resultString = EntityUtils.toString(entity);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
//            Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            Log.d("result", resultString);
            parseJSONArray();
        }
        public void parseJSONArray()
        {
            try {
                JSONArray siteArray=new JSONArray(resultString);
                JSONObject site=siteArray.getJSONObject(0);
                name=site.getString("site_name");
                bar.setTitle(name);
                bar.setSubtitle(site.getString("site_area")+site.getString("site_subarea"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}