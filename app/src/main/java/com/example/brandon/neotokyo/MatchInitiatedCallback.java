package com.example.brandon.neotokyo;

import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

/**
 * Created by Brandon on 4/15/2015.
 */
public class MatchInitiatedCallback implements
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        // Check if the status code is not success.
        Status status = result.getStatus();
        if (!(status.isSuccess())) {
            //showError(status.getStatusCode());
            Log.d("Status",Integer.toString(status.getStatusCode()));
            return;
        }

        TurnBasedMatch match = result.getMatch();

        // If this player is not the first player in this match, continue.
        if (match.getData() != null) {
            //showTurnUI(match);
            Log.d("Debug", "Not First player");
            return;
        }

        // Otherwise, this is the first player. Initialize the game state.
        //StartActivity.initGame(match);
        Log.d("Debug", "Init Game State");

        // Let the player take the first turn
        //StartActivity.showTurnUI(match);
        Log.d("Debug", "First Player's Turn");
    }
}