package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sam_chordas.android.stockhawk.Constants;
import com.sam_chordas.android.stockhawk.data.HistoricalStockData;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON) {
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try {
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1) {
          jsonObject = jsonObject.getJSONObject("results")
            .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else {
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0) {
            for (int i = 0; i < resultsArray.length(); i++) {
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e) {
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice) {
    if(bidPrice.equals("null"))
      return null;
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange) {
    String weight = change.substring(0, 1);
    String ampersand = "";
    if (isPercentChange) {
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
      QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      String bidPrice = truncateBidPrice(jsonObject.getString("Bid"));
      if(bidPrice == null)
        return null;
      builder.withValue(QuoteColumns.BIDPRICE, bidPrice);
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
        jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-') {
        builder.withValue(QuoteColumns.ISUP, 0);
      } else {
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static boolean CheckNetwork(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      return true;
    } else {
      return false;
    }
  }

  public static void LogD(String TAG, String message) {
    int maxLogSize = 2000;
    for (int i = 0; i <= message.length() / maxLogSize; i++) {
      int start = i * maxLogSize;
      int end = (i + 1) * maxLogSize;
      end = end > message.length() ? message.length() : end;
      android.util.Log.d(TAG, message.substring(start, end));
    }
  }

  public static ArrayList<HistoricalStockData> ParseHistoricalChartData(String jsonData) throws JSONException {
    ArrayList<HistoricalStockData> historicalStockData = new ArrayList<>();

    if (jsonData == null || jsonData.isEmpty())
      return null;
    JSONObject jsonObject = new JSONObject(jsonData);
    if (jsonObject == null || jsonObject.length() == 0) {
      return null;
    }
    jsonObject = jsonObject.getJSONObject("query");
    if (jsonObject == null || jsonObject.length() == 0) {
      return null;
    }

    jsonObject = jsonObject.getJSONObject("results");
    if (jsonObject == null || jsonObject.length() == 0) {
      return null;
    }
    JSONArray jsonArray = jsonObject.getJSONArray("quote");
    if (jsonObject == null || jsonObject.length() == 0) {
      return null;
    }

    int i = 0;
    for (int j = jsonArray.length() - 1; j >= 0; j--) {
      jsonObject = jsonArray.getJSONObject(j);
      String receivedDate = jsonObject.getString("Date");
      while (i < Constants.HISTORICAL_DATA_DAYS_SPAN) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -(Constants.HISTORICAL_DATA_DAYS_SPAN - i));
        Date ithDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String iThDate = simpleDateFormat.format(ithDate);
        if (iThDate.equals(receivedDate)) {
          Double open = jsonObject.getDouble("Open");
          Double high = jsonObject.getDouble("High");
          Double low = jsonObject.getDouble("Low");
          Double close = jsonObject.getDouble("Close");
          Double volume = jsonObject.getDouble("Volume");
          Double adjClose = jsonObject.getDouble("Adj_Close");
          historicalStockData.add(new HistoricalStockData(receivedDate, open, high, low, close, volume, adjClose, i));
          i++;
          break;
        }
        i++;
      }
    }
    return historicalStockData;
  }
}
