package tools;

import static android.view.View.SCROLLBARS_OUTSIDE_OVERLAY;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.qweather.sdk.view.HeContext.context;

import android.location.Location;
import android.util.Log;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Handler;



public class ApiUtils {
    private Context mContext;
    private String appID = "ttpdspkpqtnthntm";
    private String appSecret= "KUiTZZGnl4DfAksgK4JuZELNyZxvVSKR";
    private String province,city;


    public ApiUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void getYiyan(final ApiCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://api.treason.cn/API/yiyan.php ");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(10000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        callback.onSuccess(response.toString());
                    } else {
                        callback.onError(String.valueOf(responseCode));
                    }
                } catch (IOException e) {
                    callback.onError(e.toString());
                }
            }
        }).start();
    }

    public void getLocation(final ApiCallback callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = IPUtils.getIpAddress(mContext);
                try {
                    URL url = new URL("https://www.mxnzp.com/api/ip/self?app_id=ttpdspkpqtnthntm&app_secret=KUiTZZGnl4DfAksgK4JuZELNyZxvVSKR");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        // 解析JSON响应
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        city = dataObject.getString("city");
                        province = dataObject.getString("province");
                        callBack.onSuccess(province+ " "+city);
                    } else {
                        Log.e(TAG,"请求失败，数据："+responseCode);
                        callBack.onError(String.valueOf(responseCode));
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    //历史上的今天
    public void getHistoryToday(final ApiCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("https://api.leafone.cn/api/lishi");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK){
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        JSONArray listArray = dataObject.getJSONArray("list");
                        if (listArray != null && listArray.length() >= 3) {
                            String[] titles = new String[3];
                            for(int i = 0 ;i<3;i++){
                                JSONObject Item = listArray.getJSONObject(i);
                                String title = Item.getString("title");
                                String time = Item.getString("time");
                                titles[i] = time + "：" + title;
                            }
                            callback.onNewsSuccess(titles);
                        }
                    }else {
                        Log.e(TAG,"请求失败，错误码："+responseCode);
                        callback.onError(String.valueOf(responseCode));
                    }
                }catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


}
