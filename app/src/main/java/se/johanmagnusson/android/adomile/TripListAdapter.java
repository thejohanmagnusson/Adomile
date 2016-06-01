package se.johanmagnusson.android.adomile;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.johanmagnusson.android.adomile.database.TripColumns;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{

    private static final String TAG = TripListAdapter.class.getSimpleName();

    private final Context mContext;
    private final OnClickListener mOnClickListener;
    private final View mEmptyView;

    private Cursor mCursor;


    private final Drawable mPrivateIcon;
    private final Drawable mWorkIcon;
    private final String mPrivateLetter;
    private final String mWorkLetter;

    public interface OnClickListener {
        void onClick(long id, ViewHolder viewHolder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View iconShape;
        public final TextView iconText;
        public final TextView destination;
        public final TextView date;
        public final TextView km;
        public final TextView mileage;

        public ViewHolder(View itemView) {
            super(itemView);

            iconShape = itemView.findViewById(R.id.icon_shape);
            iconText = (TextView) itemView.findViewById(R.id.icon_text);
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
            long id = mCursor.getLong(mCursor.getColumnIndex(TripColumns.ID));
            mOnClickListener.onClick(id, this);
        }
    }


    public TripListAdapter(Context context, OnClickListener onClickListener, View emptyView) {
        mContext = context;
        mOnClickListener = onClickListener;
        mEmptyView = emptyView;

        mPrivateIcon = Utility.getDrawable(mContext, R.drawable.circle_private);
        mWorkIcon = Utility.getDrawable(mContext, R.drawable.circle_work);

        mPrivateLetter = mContext.getResources().getString(R.string.trip_type_letter_private);
        mWorkLetter = mContext.getResources().getString(R.string.trip_type_letter_work);
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

        boolean isWork = mCursor.getInt(mCursor.getColumnIndex(TripColumns.TripType)) == Utility.WORK;

        holder.iconShape.setBackground(isWork ? mWorkIcon : mPrivateIcon);
        holder.iconText.setText(isWork ? mWorkLetter : mPrivateLetter);
        int stringId = isWork ? R.string.trip_type_work : R.string.trip_type_private;
        holder.iconText.setContentDescription(mContext.getResources().getString(stringId));
        
        String destination = mCursor.getString(mCursor.getColumnIndex(TripColumns.Destination));
        holder.destination.setText(destination);
        holder.destination.setContentDescription(destination);

        String date = mCursor.getString(mCursor.getColumnIndex(TripColumns.Date));
        holder.date.setText(date);
        holder.date.setContentDescription(date);
        
        int mileage = mCursor.getInt(mCursor.getColumnIndex(TripColumns.Mileage));
        String formattedMileage = String.format(mContext.getResources().getString(R.string.trip_mileage), mileage);
        holder.mileage.setText(formattedMileage);
        holder.mileage.setContentDescription(formattedMileage);
        
        // Get start destination for the trip to calculate distance traveled. First trip has no previous destination.
        if(mCursor.moveToNext()) {
            int startMileage = mCursor.getInt(mCursor.getColumnIndex(TripColumns.Mileage));

            String formattedStartMileage = String.format(mContext.getResources().getString(R.string.trip_mileage_summary), mileage - startMileage);
            holder.km.setText(formattedStartMileage);
        }
        else {
            String km = String.format(mContext.getResources().getString(R.string.trip_mileage_summary), 0);
            holder.km.setText(km);
            holder.km.setContentDescription(km);
        }
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

































