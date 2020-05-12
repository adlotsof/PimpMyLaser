package com.trsonn.pimpmylaser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.trsonn.pimpmylaser.MeasurementFragment.OnListFragmentInteractionListener;
import com.trsonn.pimpmylaser.dummy.DummyContent.DummyItem;

import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMeasurementRecyclerViewAdapter extends RecyclerView.Adapter<MyMeasurementRecyclerViewAdapter.ViewHolder> {

    private final List<Measurement> mValues;


    public MyMeasurementRecyclerViewAdapter(List<Measurement> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mTimeView.setText(new Date(mValues.get(position).getTime() * 1000).toString());
        holder.mDistanceView.setText("" + mValues.get(position).getMeasuredDistance());
        holder.mAngleView.setText("" + mValues.get(position).getAngle());
        holder.mHeightView.setText("" + mValues.get(position).getCalculatedHeight());


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTimeView;
        public final TextView mHeightView;
        public final TextView mAngleView;
        public final TextView mDistanceView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTimeView = (TextView) view.findViewById(R.id.time);
            mHeightView = (TextView) view.findViewById(R.id.height);
            mAngleView = view.findViewById(R.id.angle);
            mDistanceView = view.findViewById(R.id.distance);
        }


    }
}
