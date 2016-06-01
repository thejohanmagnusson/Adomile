package se.johanmagnusson.android.adomile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.johanmagnusson.android.adomile.database.TripProvider;

public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = LogFragment.class.getSimpleName();

    private final int LOADER_ID = 1;

    private RecyclerView mTripList;
    private TripListAdapter mTripAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_log, container, false);

        mTripList = (RecyclerView) root.findViewById(R.id.trip_list);
        //todo: remove setHasFixedSize if implementing month name titles in list
        mTripList.setHasFixedSize(true);
        mTripList.setLayoutManager(new LinearLayoutManager(getActivity()));

        View emptyView = root.findViewById(R.id.empty_trip_list);

        mTripAdapter = new TripListAdapter(getActivity(), new TripListAdapter.OnClickListener() {
            @Override
            public void onClick(long id, TripListAdapter.ViewHolder viewHolder) {
                mPosition = viewHolder.getAdapterPosition();

                // Detail intent with transition animation
                Intent intent = new Intent(getActivity(), TripDetailActivity.class);
                intent.putExtra(TripDetailFragment.TRIP_ID_KEY, id);

                Pair iconShapeTrans = new Pair<>(viewHolder.iconShape, getString(R.string.transition_name_icon_shape));
                Pair iconTextTrans = new Pair<>(viewHolder.iconText, getString(R.string.transition_name_icon_text));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), iconShapeTrans, iconTextTrans);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        }, emptyView);

        mTripList.setAdapter(mTripAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        restartLoader();
    }

    public void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                TripProvider.Trips.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTripAdapter.swapCursor(data);

        if (mPosition != RecyclerView.NO_POSITION)
            mTripList.smoothScrollToPosition(mPosition);

        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTripAdapter.swapCursor(null);
    }

    private void updateEmptyView() {
        if (mTripAdapter.getItemCount() == 0) {
            TextView textView = (TextView) getView().findViewById(R.id.empty_trip_list);

            if (textView != null)
                textView.setText(R.string.empty_log_list);
        }
    }
}




























