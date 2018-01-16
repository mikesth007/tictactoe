package com.msh7.game.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.msh7.game.tictactoe.TicTacToeGame;
import com.msh7.game.tictactoe.basegameutils.BaseGameUtils;
import com.msh7.game.tictactoe.interfaces.DesktopGoogleServices;

import java.util.ArrayList;

public class AndroidLauncher extends AndroidApplication implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener {
    private static final String TAG = AndroidLauncher.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Current turn-based match
    private TurnBasedMatch mTurnBasedMatch;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public SkeletonTurn mTurnData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new TicTacToeGame(new DesktopGoogleServices()), config);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): Disconnecting from Google APIs");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("CONNECTED:: ");
        // Retrieve the TurnBasedMatch from the connectionHint
        if (bundle != null) {
            mTurnBasedMatch = bundle.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (mTurnBasedMatch != null) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }

                updateMatch(mTurnBasedMatch);
                return;
            }
        }

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
            return;
        }

        // Launch the sign-in flow if the button was clicked or if auto sign-in is enabled
        if (mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        System.out.println("INVITED");
    }

    @Override
    public void onInvitationRemoved(String s) {
        System.out.println("INVITE REMOVED");
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        System.out.println("MATCH RECEIVED");
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        System.out.println("MATCH REMOVED");
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        mTurnData = new SkeletonTurn();
        // Some basic turn data
        mTurnData.message = "First turn";

        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an auto-match partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                //setGameplayUI();
                //pass data
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;
    }

    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;
        showWarning("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }


    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        showWarning("Left", "You've left this match.");
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        if (isDoingTurn) {
            updateMatch(match);
            return;
        }
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode, int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }

    private void showWarning(String title, String message) {
        System.out.println(title + ":::" + message);
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(this, "Stored action for later.  (Please remove this toast before release.)", Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    private void signIn() {
        mTurnBasedMatch = null;
        mGoogleApiClient.connect();
    }

    private void signOut() {
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
