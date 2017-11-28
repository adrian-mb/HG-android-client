package hg.hg_android_client.mainscreen.on_trip;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hg.hg_android_client.R;

public class TripReportDialog extends Dialog {

    private String report;
    private String distance;
    private String cost;

    public TripReportDialog(@NonNull Context context) {
        super(context);
    }

    private void initializeDistance() {
        TextView distanceCounter = (TextView) findViewById(R.id.distance_counter);
        distanceCounter.setText(distance);
    }

    private void initializeCost() {
        TextView costCounter = (TextView) findViewById(R.id.cost_counter);
        costCounter.setText(cost);
    }

    private void initializeReport() {
        TextView reportView = (TextView) findViewById(R.id.report_message);
        reportView.setText(report);
    }

    private void initializeCloseButton() {
        Button b = (Button) findViewById(R.id.button_ok);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        initializeDistance();
        initializeCost();
        initializeReport();
        initializeCloseButton();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismiss();
    }

}
