package com.example.angel.silentplaces.recycler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.angel.silentplaces.R;


import org.joda.time.DateTime;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class PlaceItem extends AbstractFlexibleItem<PlaceItem.PlaceViewHolder> {


    private static int idStatic = -1;

    private static final int SECONDS_CLOSE = 2;
    private int id;
    private String placeName;
    private String placeDirection;
    private Context context;


    public PlaceItem(Context context, String placeName,  String placeDirection) {
        this.context = context;
        this.id = idStatic++;
        this.placeName=placeName;
        this.placeDirection=placeDirection;

    }




    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof PlaceItem) {
            PlaceItem inItem = (PlaceItem) inObject;
            return this.id == inItem.id;
        }
        return false;
    }


    @Override
    public int hashCode() {
        return String.valueOf(id).hashCode();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.places_item_list_layout;
    }

    @Override
    public PlaceViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return null;
    }


    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, PlaceViewHolder placeViewHolder, int position, List<Object> payloads) {
        placeViewHolder.setFields(placeName, placeDirection);
    }


    public class PlaceViewHolder extends FlexibleViewHolder implements View.OnClickListener, SwipeLayout.SwipeListener {


        @BindView(R.id.placeNameTextView)
        public TextView placeNameTextView;
        @BindView(R.id.placeDirectionTextView)
        public TextView placeDirectionTextView;


        Timer timer = new Timer();


        PlaceViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            int id = view.getId();

            switch (id) {

            }
        }


        void setFields(String placeName , String placeDirection) {
            this.placeNameTextView.setText(placeName);
            this.placeDirectionTextView.setText(placeDirection);

        }


        @Override
        public void onStartOpen(final SwipeLayout layout) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) layout.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.close(true);
                        }
                    });


                }
            }, SECONDS_CLOSE * 1000);

        }

        @Override
        public void onOpen(SwipeLayout layout) {

        }

        @Override
        public void onStartClose(SwipeLayout layout) {

        }

        @Override
        public void onClose(SwipeLayout layout) {

        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    }
}
