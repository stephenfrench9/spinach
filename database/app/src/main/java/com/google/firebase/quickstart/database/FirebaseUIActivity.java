package com.google.firebase.quickstart.database;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.quickstart.database.models.User;

import java.util.Collections;

/**
 * Demonstrate authentication using the FirebaseUI-Android library. This activity demonstrates
 * using FirebaseUI for basic email/password sign in.
 *
 * For more information, visit https://github.com/firebase/firebaseui-android
 */
public class FirebaseUIActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    private TextView mStatusView;
    private TextView mDetailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        mAuth = FirebaseAuth.getInstance();

        mStatusView = findViewById(R.id.status);
        mDetailView = findViewById(R.id.detail);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        ActionBar greg = getSupportActionBar();
        greg.hide();

        Log.d("BASIL", "onCreate(): end");

    }

    @Override
    protected void onStart() {
        Log.d("BASIL", "onStart(): start");
        super.onStart();
        updateUI(mAuth.getCurrentUser());
        Log.d("BASIL", "onStart(): end");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BASIL", "onActivityResult(): start");


        if (requestCode == RC_SIGN_IN) {
            Log.d("BASIL", "onActivityResult(): middle");
            if (resultCode == RESULT_OK) {
                // Sign in succeeded
                FirebaseUser user = mAuth.getCurrentUser();
                boolean verified = user.isEmailVerified();
                Log.d("BASIL", "onActivityResult(): " + String.valueOf(verified));
                updateUI(user);
                startActivity(new Intent(FirebaseUIActivity.this, MainActivity.class));
                finish();
            } else {
                // Sign in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
        Log.d("BASIL", "onActivityResult(): end");
    }

    private void startSignIn() {
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        Log.d("BASIL", "StartSignIn(): start");
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setLogo(R.mipmap.ic_launcher)
                .build();

        Log.d("BASIL", "StartSignIn(): middle");
        startActivityForResult(intent, RC_SIGN_IN);
        Log.d("BASIL", "StartSignIn(): end");
    }

    private void updateUI(FirebaseUser user) {
        Log.d("BASIL", "updateUI(): start");
        if (user != null) {
            // Signed in
            mStatusView.setText(getString(R.string.firebaseui_status_fmt, user.getEmail()));
            mDetailView.setText(getString(R.string.id_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            // Signed out
            mStatusView.setText(R.string.signed_out);
            mDetailView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
        Log.d("BASIL", "updateUI(): end");
    }

    private void signOut() {
        Log.d("BASIL", "singOut(): start");
        AuthUI.getInstance().signOut(this);
        updateUI(null);
        Log.d("BASIL", "singOut(): end");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                startSignIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }
}
