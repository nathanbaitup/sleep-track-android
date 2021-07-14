package com.example.assessment.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.assessment.R;
import com.example.assessment.SleepTrackDB;
import com.example.assessment.entities.Diary;
import com.example.assessment.entities.MorningMonitoring;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    LineChart hrChart;
    CombinedChart sleepChart;

    private SleepTrackDB sleepTrackDB;
    private Handler handler;

    // Checkboxes initialization
    AppCompatCheckBox sleepQuantityCheck;
    AppCompatCheckBox sleepQualityCheck;
    AppCompatCheckBox wakingHRCheck;
    AppCompatCheckBox standingHRCheck;

    // Boxes on dashboard initialization
    AppCompatTextView diaryEntryAmount;
    AppCompatTextView monitoringAmount;

    // Initialization of ArrayList for graphs.
    ArrayList<Entry> wakingHR = new ArrayList<>();
    ArrayList<Entry> standingHR = new ArrayList<>();
    List<BarEntry> sleepQuantity = new ArrayList<>();
    ArrayList<Entry> sleepQuality = new ArrayList<>();

    /**
     * Creates the Dashboard Fragment View.
     *
     * @param inflater - the layout inflater.
     * @param container - the container.
     * @param savedInstanceState - the saved instance state.
     * @return returns the inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Builds the database.
        if (getActivity() != null) {
            sleepTrackDB = Room.databaseBuilder(getActivity(), SleepTrackDB.class, "SleepTrackDB").build();
        }

        // Creates a means of running database operations in a background thread.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Creates an object that enables code to be run on the main UI thread.
        handler = HandlerCompat.createAsync(Looper.getMainLooper());

        // Finds the charts from the layout file.
        hrChart = view.findViewById(R.id.hr_chart);
        sleepChart = view.findViewById(R.id.hours_slept_chart);

        //Finds the boxes from the layout file.
        monitoringAmount = view.findViewById(R.id.monitoring_amount);
        diaryEntryAmount = view.findViewById(R.id.diary_amount);

        // Run in a background thread to initialize the graphs as database access is required.
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Gets all morning monitoring entries.
                final List<MorningMonitoring> monitoringList = sleepTrackDB.morningMonitoringDAO().getAllMonitoring();
                // Gets all diary entries.
                final List<Diary> diaries = sleepTrackDB.diaryDAO().getAllDiaryEntries();

                // Checks that the monitoringList does not equal 0 so a null pointer exception is thrown.
                if (monitoringList.size() != 0) {

                    // Uses the monitoringList to get heart rate and sleep data to be used within the graphs.
                    DashboardFragment.this.setupGraphData(monitoringList);

                    // Initializes the heart rate chart checkboxes and creates the heart rate chart.
                    DashboardFragment.this.hrCheckboxAndGraphSetup(view);
                    DashboardFragment.this.setHRChartData();

                    // Initializes the sleep chart checkboxes and creates the sleep graph.
                    DashboardFragment.this.sleepCheckboxAndGraphSetup(view);
                    DashboardFragment.this.setSleepChartData();
                }

                // Sets the diary and monitoring amounts to the number of current entries
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        diaryEntryAmount.setText(String.valueOf(diaries.size()));
                        monitoringAmount.setText(String.valueOf(monitoringList.size()));
                    }
                });

            }
        });

        return view;
    }

    /**
     * Gathering data to be added to the graphs.
     */
    private void setupGraphData(List<MorningMonitoring> monitoringList) {
        // If greater than seven, get the newest 7 entries.
        if (monitoringList.size() > 7) {
            int j = monitoringList.size() - 1;
            int dayNum = 1;
            for (int i = 0; i < 7; i++) {
                wakingHR.add(new Entry(dayNum, monitoringList.get(j - i).getWakingHR()));
                standingHR.add(new Entry(dayNum, monitoringList.get(j - i).getStandingHR()));
                sleepQuantity.add(new BarEntry(dayNum, monitoringList.get(j - i).getSleepQuantity()));
                sleepQuality.add(new Entry(dayNum, monitoringList.get(j - i).getSleepQuality()));
                dayNum++;
            }
        } else {
            int dayNum = 1;
            // Update the chart by the size of the list.
            for (int i = 0; i < monitoringList.size(); i++) {
                wakingHR.add(new Entry(dayNum, monitoringList.get(i).getWakingHR()));
                standingHR.add(new Entry(dayNum, monitoringList.get(i).getStandingHR()));
                sleepQuantity.add(new BarEntry(dayNum, monitoringList.get(i).getSleepQuantity()));
                sleepQuality.add(new Entry(dayNum, monitoringList.get(i).getSleepQuality()));
                dayNum++;
            }
        }
    }

    /*
     * Heart Rate Chart SETUP
     */

    /**
     * Creates the sleep chart by checking what checkbox is pressed and displaying the required
     * information to display to the user.
     */
    private void setHRChartData() {
        // REFERENCE ACCESSED 02/05/2021
        // https://stackoverflow.com/a/49172894
        // Used to learn how to get the line graph set up correctly.
        LineDataSet wakingHRSet = new LineDataSet(wakingHR, "Waking Heart Rate");
        LineDataSet standingHRSet = new LineDataSet(standingHR, "Standing Heart Rate");
        LineData hrLineData = new LineData();
        wakingHRSet.setColor(R.color.red);
        // END REFERENCE.

        if (wakingHRCheck.isChecked() && standingHRCheck.isChecked()) {
            hrLineData.addDataSet(wakingHRSet);
            hrLineData.addDataSet(standingHRSet);
        } else if (wakingHRCheck.isChecked()) {
            hrLineData.clearValues();
            hrLineData.addDataSet(wakingHRSet);
        } else if (standingHRCheck.isChecked()) {
            hrLineData.clearValues();
            hrLineData.addDataSet(standingHRSet);
        }

        // Builds the chart.
        handler.post(new Runnable() {
            @Override
            public void run() {
                DashboardFragment.this.configureHRChart();
                hrChart.setData(hrLineData);
                hrChart.notifyDataSetChanged();
                hrChart.invalidate();
            }
        });
    }

    /**
     * Initiates the checkboxes to show and hide chart elements when pressed.
     *
     * @param view - the fragment view.
     */
    private void hrCheckboxAndGraphSetup(View view) {
        wakingHRCheck = view.findViewById(R.id.waking_hr_checkbox);
        standingHRCheck = view.findViewById(R.id.standing_hr_checkbox);

        handler.post(new Runnable() {
            @Override
            public void run() {
                wakingHRCheck.setChecked(true);
                standingHRCheck.setChecked(true);
            }
        });

        // REFERENCE accessed 05/05/2021
        // https://stackoverflow.com/questions/48056121/show-hide-linegraph-bargraph-on-checkbox-listener-bargraph-not-working-mpandro
        // Used to find how to change the data shown in the graph via OnClick listener.
        wakingHRCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    wakingHRCheck.setChecked(!standingHRCheck.isChecked());
                }
                DashboardFragment.this.setHRChartData();
            }
        });
        standingHRCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    standingHRCheck.setChecked(!wakingHRCheck.isChecked());
                }
                DashboardFragment.this.setHRChartData();
            }
        });
        // END REFERENCE
    }

    /**
     * Configures the heart rate chart styling.
     */
    private void configureHRChart() {
        // REFERENCE ACCESSED 02/05/2021
        // https://stackoverflow.com/a/49172894
        // Used to learn how to get the line graph set up correctly.
        hrChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        hrChart.getAxisRight().setEnabled(false);
        hrChart.getDescription().setText("");
        hrChart.setPinchZoom(true);
        //END REFERENCE.
    }

    /*
     * Sleep Data Chart SETUP
     */

    /**
     * Creates the sleep chart by checking what checkbox is pressed and displaying the required
     * information to display to the user.
     */
    private void setSleepChartData() {
        BarDataSet sleepQuantitySet = new BarDataSet(sleepQuantity, "Sleep Quantity");
        LineDataSet sleepQualitySet = new LineDataSet(sleepQuality, "Sleep Quality");
        BarData sleepBarData = new BarData(sleepQuantitySet);
        LineData sleepLineData = new LineData(sleepQualitySet);
        CombinedData combinedData = new CombinedData();
        sleepQualitySet.setColor(R.color.red);
        sleepBarData.setBarWidth(0.5f);

        // REFERENCE accessed 05/05/2021
        // https://stackoverflow.com/a/48092917
        // Used to learn how to switch between the line and bar graphs.
        if (sleepQuantityCheck.isChecked() && sleepQualityCheck.isChecked()) {
            combinedData.setData(sleepLineData);
            combinedData.setData(sleepBarData);
        } else if (sleepQuantityCheck.isChecked()) {
            LineData ld = new LineData();
            ld.clearValues();
            combinedData.setData(ld);
            combinedData.setData(sleepBarData);
        } else if (sleepQualityCheck.isChecked()) {
            BarData bd = new BarData();
            bd.clearValues();
            combinedData.setData(bd);
            combinedData.setData(sleepLineData);
        }
        // END REFERENCE

        // Builds the chart.
        handler.post(new Runnable() {
            @Override
            public void run() {
                DashboardFragment.this.configureSleepChart();
                sleepChart.setData(combinedData);
                sleepChart.notifyDataSetChanged();
                sleepChart.invalidate();
            }
        });

    }

    /**
     * Initiates the checkboxes to show and hide chart elements when pressed.
     *
     * @param view - the fragment view.
     */
    private void sleepCheckboxAndGraphSetup(View view) {
        sleepQuantityCheck = view.findViewById(R.id.sleep_quantity_checkbox);
        sleepQualityCheck = view.findViewById(R.id.sleep_quality_checkbox);

        handler.post(new Runnable() {
            @Override
            public void run() {
                sleepQualityCheck.setChecked(true);
                sleepQuantityCheck.setChecked(true);
            }
        });

        // REFERENCE accessed 05/05/2021
        // https://stackoverflow.com/questions/48056121/show-hide-linegraph-bargraph-on-checkbox-listener-bargraph-not-working-mpandro
        // Used to find how to change the data shown in the graph via OnClick listener.
        sleepQuantityCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    sleepQuantityCheck.setChecked(!sleepQualityCheck.isChecked());
                }
                DashboardFragment.this.setSleepChartData();
            }
        });
        sleepQualityCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    sleepQualityCheck.setChecked(!sleepQuantityCheck.isChecked());
                }
                DashboardFragment.this.setSleepChartData();
            }
        });
        // END REFERENCE
    }

    /**
     * Configures the sleep chart styling.
     */
    private void configureSleepChart() {
        sleepChart.getDescription().setText("");
        sleepChart.getAxisRight().setEnabled(false);
        sleepChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

}

