package se.johanmagnusson.android.adomile;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import se.johanmagnusson.android.adomile.database.TripColumns;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{

    private static final String TAG = TripListAdapter.class.getSimpleName();

    private final Context mContext;
    private final OnClickListener mOnClickListener;
    private final View mEmptyView;

    private Cursor mCursor;

    public interface OnClickListener {
        void onClick(long id, ViewHolder viewHolder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView icon;
        public final TextView destination;
        public final TextView date;
        public final TextView km;
        public final TextView mileage;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.icon);
            destination = (TextView) itemView.findViewById(R.id.trip_destination);
            date = (TextView) itemView.findViewById(R.id.trip_date);
            km = (TextView) itemView.findViewById(R.id.trip_km);
            mileage = (TextView) itemView.findViewById(R.id.trip_mileage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //get item position from adapter
            int position = getAdapterPosition();

            //move to the position of the cursor, get data and handle click
            mCursor.moveToPosition(position);
            long id = mCursor.getLong(mCursor.getColumnIndex(TripColumns.PreviousTripId));
            mOnClickListener.onClick(id, this);
        }
    }


    public TripListAdapter(Context context, OnClickListener onClickListener, View emptyView) {
        mContext = context;
        mOnClickListener = onClickListener;
        mEmptyView = emptyView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(parent instanceof RecyclerView){
            int layoutId = R.layout.trip_list_item;

            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);

            return new ViewHolder(view);
        }
        else
            throw new RuntimeException("Not bound to RecyclerViewSelection");
    }

    @Override
    public void onBindViewHolder(TripListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        //todo: move to utility, same code is used in register fragment to show last trip

        boolean isWork = mCursor.getInt(mCursor.getColumnIndex(TripColumns.TripType)) == 1 ? true : false;
        holder.icon.setImageResource(Utility.getResourceForTripIcon(isWork));

        String destination = mCursor.getString(mCursor.getColumnIndex(TripColumns.Destination));
        holder.destination.setText(destination);

        String date = mCursor.getString(mCursor.getColumnIndex(TripColumns.Date));
        holder.date.setText(date);

        int mileage = mCursor.getInt(mCursor.getColumnIndex(TripColumns.Mileage));
        holder.mileage.setText(Integer.toString(mileage));

        // Get start destination for the trip to calculate distance traveled. First trip has no previous destination.
        if(mCursor.moveToNext()) {
            int startMileage = mCursor.getInt(mCursor.getColumnIndex(TripColumns.Mileage));

            holder.km.setText(String.format(mContext.getResources().getString(R.string.trip_km), mileage - startMileage));
        }
        else
            holder.km.setText(String.format(mContext.getResources().getString(R.string.trip_km), 0));
    }

    @Override
    public int getItemCount() {
        if(mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}

































