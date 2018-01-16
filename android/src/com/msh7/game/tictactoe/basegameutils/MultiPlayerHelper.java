package com.msh7.game.tictactoe.basegameutils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.msh7.game.tictactoe.MainApplication;
import com.msh7.game.tictactoe.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Button Clicker 2000. A minimalistic game showing the multiplayer features of
 * the Google Play game services API. The objective of this game is clicking a
 * button. Whoever clicks the button the most times within a 20 second interval
 * wins. It's that simple. This game can be played with 2, 3 or 4 players. The
 * code is organized in sections in order to make understanding as clear as
 * possible. We start with the integration section where we show how the game
 * is integrated with the Google Play game services API, then move on to
 * game-specific UI and logic.
 * <p>
 * INSTRUCTIONS: To run this sample, please set up
 * a project in the Developer Console. Then, place your app ID on
 * res/values/ids.xml. Also, change the package name to the package name you
 * used to create the client ID in Developer Console. Make sure you sign the
 * APK with the certificate whose fingerprint you entered in Developer Console
 * when creating your Client Id.
 *
 * @author Bruno Oliveira (btco), 2013-04-26
 */
public class MultiPlayerHelper extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener {

    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */

    final static String TAG = "ButtonClicker2000";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;

    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];
    private Activity activity;

    void play() {
        resetGameVars();
        startGame(false);
    }

    void signin() {
        if (!BaseGameUtils.verifySampleSetup(activity, R.string.app_id)) {
            Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
        }

        // start the sign-in flow
        Log.d(TAG, "Sign-in button clicked");
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    void signout() {
        Log.d(TAG, "Sign-out button clicked");
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        mGoogleApiClient.disconnect();
    }

    void invite() {
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        switchToScreen(R.id.screen_wait);
        activity.startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    private void switchToScreen(int screen_wait) {
        ((MainApplication) activity).quickGame();
    }

    void seeInvites() {
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        switchToScreen(R.id.screen_wait);
        activity.startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    void acceptPopupInvitation() {
        acceptInviteToRoom(mIncomingInvitationId);
        mIncomingInvitationId = null;
    }

    void startGame() {
        startQuickGame();
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        TurnBasedMatchConfig.Builder rtmConfigBuilder = TurnBasedMatchConfig.builder();
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, rtmConfigBuilder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
                }
                break;
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        TurnBasedMatchConfig.Builder rtmConfigBuilder = TurnBasedMatchConfig.builder();
        rtmConfigBuilder.addInvitedPlayers(invitees);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    private void switchToMainScreen() {
        ((MainApplication) activity).dismissDialog();
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        keepScreenOn();
        resetGameVars();
        Games.TurnBasedMultiplayer.acceptInvitation(mGoogleApiClient, invId);
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToMainScreen();
        } else {
            switchToScreen(R.id.screen_sign_in);
        }
        super.onStop();
    }

    // Activity just got to the foreground. We switch to the wait screen because we will now
    // go through the sign-in flow (remember that, yes, every time the Activity comes back to the
    // foreground we go through the sign-in flow -- but if the user is already authenticated,
    // this flow simply succeeds and is imperceptible).
    @Override
    public void onStart() {
        if (mGoogleApiClient == null) {
            switchToScreen(R.id.screen_sign_in);
        } else if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Connecting client.");
            switchToScreen(R.id.screen_wait);
            mGoogleApiClient.connect();
        } else {
            Log.w(TAG,
                    "GameHelper: client was already connected on onStart()");
        }
        super.onStart();
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(1); // This will show the invitation popup
    }

    @Override
    public void onInvitationRemoved(String invitationId) {

        if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
            mIncomingInvitationId = null;
            switchToScreen(1); // This will hide the invitation popup
        }

    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        switchToScreen(R.id.screen_sign_in);
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
    }


    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mFinishedParticipants.clear();
    }

    // Start the gameplay phase of the game.
    void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        broadcastScore(false);
        switchToMainScreen();
    }

    /*
     * COMMUNICATIONS SECTION. Methods that implement the game's network
     * protocol.
     */

    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<String>();

    void broadcastScore(boolean test) {
        if (!mMultiplayer)
            return; // playing single-player mode

        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte) (test ? 'F' : 'U');

        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;
            Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, null, mMsgBuf, p.getParticipantId());
        }
    }

    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}