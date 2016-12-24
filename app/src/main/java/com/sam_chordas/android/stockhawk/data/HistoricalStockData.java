package com.sam_chordas.android.stockhawk.data;

/**
 * Created by ramkr on 18-Nov-16.
 */

public class HistoricalStockData {
  int index;
  String Date;
  double Open;
  double High;
  double Low;
  double Close;
  double Volume;
  double Adj_Close;

  public HistoricalStockData() {
  }

  ;

  public HistoricalStockData(String d, double o, double h, double l, double c, double v, double ac, int index) {
    Date = d;
    Open = o;
    High = h;
    Low = l;
    Close = c;
    Volume = v;
    Adj_Close = ac;
    this.index = index;
  }

  public String getDate() {
    return Date;
  }

  public void setDate(String date) {
    Date = date;
  }

  public double getOpen() {
    return Open;
  }

  public void setOpen(double open) {
    Open = open;
  }

  public double getHigh() {
    return High;
  }

  public void setHigh(double high) {
    High = high;
  }

  public double getLow() {
    return Low;
  }

  public void setLow(double low) {
    Low = low;
  }

  public double getClose() {
    return Close;
  }

  public void setClose(double close) {
    Close = close;
  }

  public double getVolume() {
    return Volume;
  }

  public void setVolume(double volume) {
    Volume = volume;
  }

  public double getAdj_Close() {
    return Adj_Close;
  }

  public void setAdj_Close(double adj_Close) {
    Adj_Close = adj_Close;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }
}
