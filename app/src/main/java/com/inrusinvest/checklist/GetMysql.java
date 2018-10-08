package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class GetMysql {


    public void getIntroTextView (final TextView textView, final Integer sql, final String date1, final String date2,
                                 final String cou, final String number) {

        @SuppressLint("StaticFieldLeak")
        class SendPostRequest extends AsyncTask<String, Void, String> {

            protected void onPreExecute() {
            }

            protected String doInBackground(String... arg0) {

                try {

                    URL url = new URL("");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("sql", sql);
                    postDataParams.put("start", date1);
                    postDataParams.put("end", date2);
                    postDataParams.put("getcount", cou);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);

                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuilder sb = new StringBuilder("");
                        String line;

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    } else {
                        return "false : " + responseCode;
                    }
                } catch (Exception e) {
                    return "Exception: " + e.getMessage();
                }

            }

            @Override
            protected void onPostExecute(String result) {

                try {
                    loadIntoTextView(result, textView, number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SendPostRequest getJSON = new SendPostRequest();
        getJSON.execute();
    }
    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString().trim();
    }

    private void loadIntoTextView(String json, TextView textView, String number) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            String count = obj.getString(number);
            textView.append(count);
        }
    }
}
