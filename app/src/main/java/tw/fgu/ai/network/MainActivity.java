package tw.fgu.ai.network;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
                    stationHashMap.put("name",stationJSON.getString("site_name")); //1.到stationJSON抓site_name,丟進"name"的名義
                    stationHashMap.put("area",stationJSON.getString("site_area"));//2.到stationJSON抓site_area，放進"area"框格裡
                    list.add(stationHashMap);

                    Log.d("station",stationJSON.getString("site_area"));
                }
                SimpleAdapter adapter=new SimpleAdapter(
                        MainActivity.this,
                        list,//資料
                        R.layout.stationitem,//放資料的地方
                        new String[]{"name","area"},//1,2的"name","area"丟下來，看你要呈現什麼就寫上來，但要先在上面連結就是了
                        //             ▼        ▼
                        new int[]{R.id.tv_name,R.id.tv_area}
                );

                lv_station.setAdapter(adapter);

                lv_station.setOnItemClickListener(new AdapterView.OnItemClickListener() { //按下去
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //i=按到的HashMap
                        HashMap<String,Object>item=list.get(i);
                        Toast.makeText(MainActivity.this,(String)item.get("name"),Toast.LENGTH_SHORT).show();


                    }
                });

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }
}