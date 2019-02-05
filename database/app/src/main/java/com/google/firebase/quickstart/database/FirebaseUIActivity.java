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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.quickstart.database.models.User;
import com.google.firebase.quickstart.database.utilities.Util;

import java.util.Collections;

/**
 * Demonstrate authentication using the FirebaseUI-Android library. This activity demonstrates
 * using FirebaseUI for basic email/password sign in.
 *
 * For more information, visit https://github.com/firebase/firebaseui-android
 */
public class FirebaseUIActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView mDetailView;

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = Util.getDatabase().getReference();


        mDetailView = findViewById(R.id.detail);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        ActionBar greg = getSupportActionBar();
        greg.hide();


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
        if (user != null && user.isEmailVerified()) {
            startActivity(new Intent(FirebaseUIActivity.this, MainActivity.class));
            finish();
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign in succeeded
                FirebaseUser user = mAuth.getCurrentUser();
                String username = usernameFromEmail(user.getEmail());
                writeNewUser(user.getUid(), username, user.getEmail());
                boolean verified = user.isEmailVerified();
                if(verified) {
                    startActivity(new Intent(FirebaseUIActivity.this, MainActivity.class));
                    finish();
                } else {
                    user.sendEmailVerification();
                }
                updateUI(user);
            } else {
                // Sign in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void startSignIn() {
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setLogo(R.mipmap.ic_launcher)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            // Signed in
            boolean verified = user.isEmailVerified();
            if(verified) {
                findViewById(R.id.verify_message).setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.verify_message).setVisibility(View.VISIBLE);
            }
            mDetailView.setText(getString(R.string.id_fmt, user.getEmail()));
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            // Signed out
            findViewById(R.id.verify_message).setVisibility(View.INVISIBLE);
            mDetailView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this);
        updateUI(null);
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
