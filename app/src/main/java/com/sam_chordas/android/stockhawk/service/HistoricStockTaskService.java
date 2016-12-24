package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.os.AsyncTask;

import com.sam_chordas.android.stockhawk.data.HistoricalStockData;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ramkr on 16-Nov-16.
 */

public class HistoricStockTaskService extends AsyncTask<String, Integer, ArrayList<HistoricalStockData>> {
  private String LOG_TAG = HistoricStockTaskService.class.getSimpleName();

  private IHistoricalData delegate;
  Context mContext;
  boolean isNetworkAvailable;
  String symbol;
  String url;
  String startDate, endDate;

  public HistoricStockTaskService(Context context, String symbol) {
    delegate = (IHistoricalData) context;
    mContext = context;
    this.symbol = symbol;
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 0);
    SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
    endDate = formattedDate.format(cal.getTime());
    cal.add(Calendar.DATE, -30);
    startDate = formattedDate.format(cal.getTime());
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    isNetworkAvailable = Utils.CheckNetwork(mContext);
    if (isNetworkAvailable && symbol != null && !symbol.isEmpty()) {
      StringBuilder urlStringBuilder = new StringBuilder();
      try {
        // Base URL for the Yahoo query
        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
        urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = ", "UTF-8"));
        urlStringBuilder.append(URLEncoder.encode("\"" + symbol + "\" and startDate = " + "\"" + startDate + "\" and endDate = " + "\"" + endDate + "\"", "UTF-8"));
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
          + "org%2Falltableswithkeys&callback=");
        url = urlStringBuilder.toString();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected ArrayList<HistoricalStockData> doInBackground(String... params) {
    if (isNetworkAvailable) {
      try {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
          .url(url)
          .build();
        Response response = client.newCall(request).execute();
        String jsonData = response.body().string();
        ArrayList<HistoricalStockData> historicalStockData = Utils.ParseHistoricalChartData(jsonData);
        return historicalStockData;
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  protected void onPostExecute(ArrayList<HistoricalStockData> historicalStockData) {
    super.onPostExecute(historicalStockData);
    delegate.GetHistoricalData(historicalStockData);
  }

  public interface IHistoricalData {
    void GetHistoricalData(ArrayList<HistoricalStockData> historicalStockData);
  }
}
