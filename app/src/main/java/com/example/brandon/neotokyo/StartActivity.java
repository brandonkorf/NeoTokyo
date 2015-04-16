package com.example.brandon.neotokyo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

public class StartActivity extends ActionBarActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = "StartActivity";

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // For our intents
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    //Data from the GameState
    GameState gameState = new GameState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // Create the Google Api Client with access to the Play Game services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Starts a connection with the Google Play API Client
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        setViewVisibility();
    }

    //Stops a connection with the Google Play API Client
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        setViewVisibility();
    }

    //Notifies us when sign-in is successful
    @Override
    public void onConnected(Bundle connectionHint) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)
        Log.d("MyActivity","Player is Connected");
        setViewVisibility();
    }

    //Called when connection fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
        setViewVisibility();
    }

    //Called when connection is suspended
    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
        setViewVisibility();
    }

    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
        Log.d("MyActivity","Sign In was clicked");
        setViewVisibility();
    }

    // Call when the sign-out button is clicked
    private void signOutClicked() {
        mSignInClicked = false;
        //Games.signOut(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        Log.d("MyActivity","Sign Out was clicked");
        setViewVisibility();
    }

    //Called when sign-in button is clicked
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            //mSignInClicked = true;
            //mGoogleApiClient.connect();
            signInClicked();
            // show sign-in button, hide the sign-out button

            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            //mSignInClicked = false;
            //Games.signOut(mGoogleApiClient);
            signOutClicked();

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
        setViewVisibility();
    }



    public void onStartMatchClicked(View view) {
        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 7, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }



    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

        updateStats();

        if (!isSignedIn) {
            findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameLayout).setVisibility(View.GONE);
            return;
        }


        ((TextView) findViewById(R.id.name_field)).setText(Games.Players.getCurrentPlayer(
                mGoogleApiClient).getDisplayName());
        findViewById(R.id.login_layout).setVisibility(View.GONE);

        if (isDoingTurn) {
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gameLayout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);


        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // Get the invitee list.
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // Get auto-match criteria.
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria)
                    .build();

            // Create and start the match.
            Games.TurnBasedMultiplayer
                    .createMatch(mGoogleApiClient, tbmc)
                    .setResultCallback(new MatchInitiatedCallback());
            isDoingTurn = true;
            setViewVisibility();
        }
    }

    public void initGame(TurnBasedMatch match){
        //this.gameState = new GameState(2);

       setViewVisibility();
    }

    public void showTurnUI(TurnBasedMatch match){
        setViewVisibility();
    }

    public void rollDice(View view){
        TextView[] t = new TextView[6];
        t[0] = (TextView)findViewById(R.id.die0);
        t[1] = (TextView)findViewById(R.id.die1);
        t[2] = (TextView)findViewById(R.id.die2);
        t[3] = (TextView)findViewById(R.id.die3);
        t[4] = (TextView)findViewById(R.id.die4);
        t[5] = (TextView)findViewById(R.id.die5);

        for (int i = 0; i < 6; i++){

            gameState.dice[i].roll();
            t[i].setText(gameState.dice[i].image);
        }
    }

    public void updateStats(){
        Player tempPlayer = gameState.player.get(0);
        ((TextView)findViewById(R.id.heart0)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp0)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy0)).setText(Integer.toString(tempPlayer.getEnergy()));

        tempPlayer = gameState.player.get(1);
        ((TextView)findViewById(R.id.heart1)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp1)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy1)).setText(Integer.toString(tempPlayer.getEnergy()));

        /*tempPlayer = turnData.player.get(2);
        ((TextView)findViewById(R.id.heart2)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp2)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy2)).setText(Integer.toString(tempPlayer.getEnergy()));*/

        int tokyoP = gameState.tokyoP;

        if(tokyoP == 0){
            ((TextView)findViewById(R.id.inTokyo0)).setText("Yes");
            ((TextView)findViewById(R.id.inTokyo1)).setText("No");
            ((TextView)findViewById(R.id.inTokyo2)).setText("No");
        }
        else if(tokyoP == 1){
            ((TextView)findViewById(R.id.inTokyo0)).setText("No");
            ((TextView)findViewById(R.id.inTokyo1)).setText("Yes");
            ((TextView)findViewById(R.id.inTokyo2)).setText("No");
        }
        else if(tokyoP == 2){
            ((TextView)findViewById(R.id.inTokyo0)).setText("No");
            ((TextView)findViewById(R.id.inTokyo1)).setText("No");
            ((TextView)findViewById(R.id.inTokyo2)).setText("Yes");
        }

        /* TODO: Fix this
        for (int i = 0; i < turnData.player.size(); i++){
            Player tempPlayer = turnData.player.get(i);
            ((TextView)findViewById(R.id.heart0)).setText(Integer.toString(tempPlayer.getHealth()));
            ((TextView)findViewById(R.id.vp0)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
            ((TextView)findViewById(R.id.energy0)).setText(Integer.toString(tempPlayer.getEnergy()));
        }
        */
    }
}
