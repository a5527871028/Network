package tw.fgu.ai.network;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    ArrayList<HashMap<String,Object>>list; //串列上(HashMap)的欄位(site_name,site_area)宣告
    ListView lv_station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=new ArrayList<>();
        lv_station=findViewById(R.id.lv_station);

        NetworkTask networkTask=new NetworkTask();
        networkTask.execute("http://www.yichengtech.tw/twblogs/tw_sites.php");

    }


    public class NetworkTask extends AsyncTask<String,Void,String> {

        ProgressDialog dialog;
        String resultString;
        @Override
        protected void onPreExecute() { //「執行前」
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog=ProgressDialog.show(MainActivity.this, "連線中", "請稍候");
        }

        @Override
        protected String doInBackground(String... params) { //「背景中」
            // TODO Auto-generated method stub
            HttpClient client=new DefaultHttpClient(); //把自己變成瀏覽器
            HttpGet get=new HttpGet(params[0]);

            try {
                HttpResponse response=client.execute(get);
                HttpEntity entity=response.getEntity();
                resultString= EntityUtils.toString(entity);

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
        protected void onPostExecute(String result) { //「執行後」
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
//            Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            Log.d("result",resultString);
            parseJSONArray();
        }
        void  parseJSONArray(){

            try{
                JSONArray jsonArray=new JSONArray(resultString);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject stationJSON=jsonArray.getJSONObject(i);
                    HashMap<String,Object>stationHashMap=new HashMap<>();
                    stationHashMap.put("name",stationJSON.getString("site_name")); //到stationJSON抓site_name,丟進"name"的名義
                    stationHashMap.put("area",stationJSON.getString("site_area"));//到stationJSON抓site_area，放進"area"框格裡
                    list.add(stationHashMap);


                    Log.d("station",stationJSON.getString("site_area"));
                }
                SimpleAdapter adapter=new SimpleAdapter(
                        MainActivity.this,
                        list,
                        R.layout.stationitem,
                        new String[]{"name","area"},
                        new int[]{R.id.tv_name,R.id.tv_area}
                );

                lv_station.setAdapter(adapter);
                
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }
}