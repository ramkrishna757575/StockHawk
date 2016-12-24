package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.Constants;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalStockData;
import com.sam_chordas.android.stockhawk.service.HistoricStockTaskService;

import java.util.ArrayList;

public class StockGraphActivity extends AppCompatActivity implements HistoricStockTaskService.IHistoricalData {

  LineChart chart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_line_graph);
    chart = (LineChart) findViewById(R.id.chart);

    String symbol = getIntent().getExtras().get(Constants.INTENT_SYMBOL).toString();
    if (symbol != null && !symbol.isEmpty()) {
      HistoricStockTaskService historicStockTaskService = new HistoricStockTaskService(this, symbol);
      historicStockTaskService.execute();
    }
  }

  @Override
  public void GetHistoricalData(ArrayList<HistoricalStockData> historicalStockData) {
    if (historicalStockData == null)
      return;
    PlotGraph(historicalStockData);
  }

  private void PlotGraph(ArrayList<HistoricalStockData> historicalStockData) {

    /*ArrayList<Entry> openDataSet = new ArrayList<>();
    ArrayList<Entry> highDataSet = new ArrayList<>();
    ArrayList<Entry> lowDataSet = new ArrayList<>();
    ArrayList<Entry> closeDataSet = new ArrayList<>();
    ArrayList<Entry> volumeDataSet = new ArrayList<>();*/
    ArrayList<Entry> adjCloseDataSet = new ArrayList<>();

    for (int i = 0; i < historicalStockData.size(); i++) {
      HistoricalStockData hsd = historicalStockData.get(i);
      /*openDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getOpen()));
      highDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getHigh()));
      lowDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getLow()));
      closeDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getClose()));
      volumeDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getVolume()));*/
      adjCloseDataSet.add(new Entry(hsd.getIndex(), (float) hsd.getAdj_Close()));
    }

    if (historicalStockData.size() > 0) {
      // remove axis
      YAxis leftAxis = chart.getAxisLeft();
      leftAxis.setTextColor(Color.WHITE);
      leftAxis.setTextSize(10f);

      YAxis rightAxis = chart.getAxisRight();
      rightAxis.setEnabled(false);


      XAxis xaxis = chart.getXAxis();
      xaxis.setDrawGridLines(false);
      xaxis.setDrawAxisLine(true);
      xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
      xaxis.setTextColor(Color.WHITE);
      xaxis.setTextSize(10f);

      // hide legend
      Legend legend = chart.getLegend();
      legend.setTextColor(Color.WHITE);
      legend.setTextSize(12f);

      chart.setBackgroundColor(Color.parseColor("#181B21"));
      chart.setDrawGridBackground(false);
      chart.setDrawBorders(false);
      chart.setDescription(null);

      ArrayList<ILineDataSet> lines = new ArrayList<>();

      /*LineDataSet openDataSetLine = new LineDataSet(openDataSet, "Open");
      openDataSetLine.setDrawValues(false);
      openDataSetLine.setColor(Color.parseColor("#176A17"));
      openDataSetLine.setLineWidth(4f);
      openDataSetLine.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      lines.add(openDataSetLine);

      LineDataSet closeDataSetLine = new LineDataSet(closeDataSet, "Close");
      closeDataSetLine.setDrawValues(false);
      closeDataSetLine.setColor(Color.parseColor("#801515"));
      closeDataSetLine.setLineWidth(4f);
      closeDataSetLine.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      lines.add(closeDataSetLine);

      LineDataSet highDataSetLine = new LineDataSet(highDataSet, "High");
      highDataSetLine.setDrawValues(false);
      highDataSetLine.setColor(Color.parseColor("#0B2A9B"));
      highDataSetLine.setLineWidth(4f);
      highDataSetLine.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      lines.add(highDataSetLine);

      LineDataSet lowDataSetLine = new LineDataSet(lowDataSet, "Low");
      lowDataSetLine.setDrawValues(false);
      lowDataSetLine.setColor(Color.parseColor("#FFEC00"));
      lowDataSetLine.setLineWidth(4f);
      lowDataSetLine.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      lines.add(lowDataSetLine);*/

      LineDataSet adjCloseDataSetLine = new LineDataSet(adjCloseDataSet, "Adjusted close price since past 30 days");
      adjCloseDataSetLine.setDrawValues(false);
      adjCloseDataSetLine.setColor(Color.BLUE);
      adjCloseDataSetLine.setLineWidth(4f);
      adjCloseDataSetLine.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      lines.add(adjCloseDataSetLine);

      chart.setData(new LineData(lines));
      chart.invalidate();
    }
  }
}
