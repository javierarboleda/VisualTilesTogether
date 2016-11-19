package com.javierarboleda.visualtilestogether.fragments;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.javierarboleda.visualtilestogether.VisualTilesTogetherApp;
import com.javierarboleda.visualtilestogether.models.Channel;

public class TileListFragmentInChannel extends TileListFragment {
    @Override
    Query getDbQuery(DatabaseReference dbRef) {
        VisualTilesTogetherApp visualTilesTogetherApp = (VisualTilesTogetherApp) getActivity()
                .getApplication();
        return dbRef
                .child(visualTilesTogetherApp.getUser().getChannelId())
                .child(Channel.TILE_IDS)
                .orderByKey();
    }
}
