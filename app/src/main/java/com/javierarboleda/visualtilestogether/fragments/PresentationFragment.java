package com.javierarboleda.visualtilestogether.fragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.javierarboleda.visualtilestogether.BuildConfig;
import com.javierarboleda.visualtilestogether.R;
import com.javierarboleda.visualtilestogether.VisualTilesTogetherApp;
import com.javierarboleda.visualtilestogether.models.PresentLayout;
import com.javierarboleda.visualtilestogether.models.Tile;

import java.util.HashMap;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PresentationFragmentListener} interface
 * to handle interaction events.
 * Use the {@link PresentationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PresentationFragment extends Fragment
        implements PresentLayout.PresentLayoutListener {
    private static final String TAG = PresentationFragment.class.getSimpleName();
    private static final String ARG_EDITOR_MODE = "editor_mode";

   private boolean isEditorMode;
    private PercentFrameLayout mainLayout;
    private PercentFrameLayout viewContainer;
    private HashMap<Integer, ImageView> images;
    private HashMap<Integer, ImageView> tiles;

    private PresentLayout layout;
    private Query dbTiles;

    private PresentationFragmentListener mListener;

    public PresentationFragment() {
        // Required empty public constructor
    }


    public static PresentationFragment newInstance(boolean isEditorMode) {
        PresentationFragment fragment = new PresentationFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDITOR_MODE, isEditorMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEditorMode = getArguments().getBoolean(ARG_EDITOR_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_presentation, container, false);
        images = new HashMap<>();
        tiles = new HashMap<>();
        if (VisualTilesTogetherApp.getUser() == null ||
            VisualTilesTogetherApp.getChannel() ==  null) {
            return view;
        }
        if (layout == null) {
            layout = PresentLayout.createDemoLayout();
            /*
            if (database != null && !channelId.isEmpty()) {
                database.setValue(layout);
            }*/
        }
        layout.setListener(this);
        viewContainer = (PercentFrameLayout) view.findViewById(R.id.viewContainer);
        mainLayout = (PercentFrameLayout) view.findViewById(R.id.mainLayout);
        drawLayout(savedInstanceState);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PresentationFragmentListener) {
            mListener = (PresentationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (dbTiles != null && channelTileListener != null)
            dbTiles.removeEventListener(channelTileListener);
    }

    public interface PresentationFragmentListener {
        void onTileTapped(int position, Tile tile);
    }

    private void moveRelativeView(View view, Rect bounds) {
        if (BuildConfig.DEBUG && !(view.getLayoutParams()
                instanceof PercentFrameLayout.LayoutParams)) {
            throw new RuntimeException("View is not within a PercentRelativeLayout");
        }
        PercentFrameLayout.LayoutParams params = (PercentFrameLayout.LayoutParams)
                view.getLayoutParams();
        Point max = layout.getLayoutSize();
        params.getPercentLayoutInfo().topMarginPercent = bounds.top / (float) max.y;
        params.getPercentLayoutInfo().leftMarginPercent = bounds.left / (float) max.x;
        params.getPercentLayoutInfo().heightPercent = (bounds.bottom - bounds.top) / (float) max.y;
        params.getPercentLayoutInfo().widthPercent = (bounds.right - bounds.left) / (float) max.x;
        view.invalidate();
    }

    private void loadTile(int position, ImageView view, int durationMs) {
        if (view == null) {
            Log.e(TAG, "Null imageview reference in loadTile for position " + position);
            return;
        }
        if (layout == null) {
            Log.e(TAG, "Null layout reference in loadTile for position " + position);
            return;
        }
        Tile tile = layout.getTiles()[position];
        if (tile == null) {
            Log.e(TAG, "Null tile reference in loadTile for position " + position);
            return;
        }
        if (tile.getShapeUrl() == null) {
            Log.e(TAG, "Null getShapeUrl reference in loadTile for position " + position);
            return;
        }
        Glide.with(this).load(tile.getShapeUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade(durationMs)
                .into(view);
    }

    @Override
    public void updateTile(int position, Tile tile, int transitionMs) {
        loadTile(position, tiles.get(position), transitionMs);
    }


    public PresentLayout stepLayout(PresentLayout left, PresentLayout right, float stepPercent) {
        // TODO(chris): This enables animation.
        return null;
    }

    private void drawLayout(Bundle savedInstanceState) {
        if (layout == null) return;
        for (int i = 0; i < layout.getTileCount(); i++) {
            ImageView view = null;
            if (tiles.get(i) == null) {
                view = buildTileView(i, savedInstanceState);
                viewContainer.addView(view);
                tiles.put(i, view);
            }
            moveRelativeView(view, layout.getTilePositions()[i]);
            loadTile(i, view, 0);
        }
        // Delete stale tiles.
        for (int i = layout.getTileCount(); i < tiles.size(); i++) {
            viewContainer.removeView(tiles.get(i));
        }
        for (int i = 0; i < layout.getImageCount(); i++) {
            ImageView view = null;
            if (images.get(i) == null) {
                view = buildTileView(i, savedInstanceState);
                viewContainer.addView(view);
                images.put(i, view);
            }
            moveRelativeView(view, layout.getImagePositions()[i]);
            Glide.with(this).load(layout.getImageUrls()[i])
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view);
        }
        // Delete stale images.
        for (int i = layout.getImageCount(); i < images.size(); i++) {
            viewContainer.removeView(images.get(i));
        }
        Glide.with(this).load(layout.getBackgroundUrl()).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(
                        new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                mainLayout.setBackground(new BitmapDrawable(resource));
                            }
                        }
                );
        beginListeningForTiles();
    }

    private ValueEventListener channelTileListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (layout == null) return;
            long tileCount = dataSnapshot.getChildrenCount();
            HashMap<Integer, Tile> loadedTiles = new HashMap<>();
            int tilePos = 0;
            for (DataSnapshot record : dataSnapshot.getChildren()) {
                loadedTiles.put(tilePos++, record.getValue(Tile.class));
            }
            Log.i(TAG, "Tile count: " + tileCount);
            // No tiles = no update.
            if (tilePos == 0)
                return;
            for (int i = 0; i < layout.getTileCount(); i++) {
                int pos = (int) (i % tileCount);
                Log.i(TAG, "Loading tile index " + i + " with tile pos " + pos);
                if (loadedTiles.get(pos) != null) {
                    layout.setTile(i, loadedTiles.get(pos));
                } else {
                    Log.e(TAG, "WTF Tile is null!?: " + pos);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private void beginListeningForTiles() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbTiles = dbRef.child(Tile.TABLE_NAME)
                .orderByChild(Tile.CHANNEL_ID)
                .equalTo(VisualTilesTogetherApp.getUser().getChannelId());
        dbTiles.addValueEventListener(channelTileListener);
    }

    private ImageView buildTileView(final int position, Bundle savedInstanceState) {
        ImageView iv = (ImageView) getLayoutInflater(savedInstanceState).inflate(R.layout
                .iv_tile, null);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onTileTapped(position, layout.getTiles()[position]);
            }
        });
        return iv;
    }


    private int lastColor = 0;
    private Handler tintAnimationHandler = new Handler();
    private Runnable tintAnimation = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            int colorTo = Color.argb(255, random.nextInt(256), random.nextInt(256), random
                    .nextInt(256));
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                    lastColor, colorTo);
            colorAnimation.setDuration(1000);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    for (ImageView iv : tiles.values()) {
                        iv.setColorFilter((int) valueAnimator.getAnimatedValue());
                    }
                }
            });
            colorAnimation.start();
            colorAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // Start another.
                    tintAnimationHandler.postDelayed(tintAnimation, 1000);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            lastColor = colorTo;
        }
    };
    private void beginFunAnimation() {
        tintAnimationHandler.post(tintAnimation);
    }
}
