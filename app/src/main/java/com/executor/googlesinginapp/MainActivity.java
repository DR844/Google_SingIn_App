package com.executor.googlesinginapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

import java.io.IOException;
import java.util.Collections;

import javax.mail.MessagingException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    GoogleAccountCredential credential;
    static final int REQUEST_ACCOUNT_PICKER = 2;
    SharedPreferenceManager moSharedPreferenceManager;
    SignInButton mbGoogleButton;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbGoogleButton = findViewById(R.id.sign_in_button);
        moSharedPreferenceManager = new SharedPreferenceManager(MainActivity.this);

        mbGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FooTask().execute();
            }
        });

        credential =
                GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(GmailScopes.GMAIL_SEND));
        if (moSharedPreferenceManager.getAccountName() != null) {

            /*  SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);*/

            credential.setSelectedAccountName(moSharedPreferenceManager.getAccountName());


        } else {
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        }

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("125313351875-tr4kogrt2ovmic4anqaofssm1ubdrp3v.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_PICKER)
            if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    credential.setSelectedAccountName(accountName);
                    moSharedPreferenceManager.setAccountName(accountName);
                }
            }
    }

    private class FooTask extends AsyncTask<Void, Void, String> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(Void... params) {
            String token = "";
            try {

                token = GoogleAuthUtil.getToken(MainActivity.this, credential.getSelectedAccount(), "oauth2:https://www.googleapis.com/auth/gmail.send");
            } catch (UserRecoverableAuthException e) {
                // Requesting an authorization code will always throw
                // UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
                // because the user must consent to offline access to their data.  After
                // consent is granted control is returned to your activity in onActivityResult
                // and the second call to GoogleAuthUtil.getToken will succeed.
                startActivityForResult(e.getIntent(), 1234);

            } catch (GoogleAuthException e) {
                Log.i(TAG, "" + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.i(TAG, "" + e.getMessage());
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                //use token here
                try {
                    SendMessage.sendEmail(MainActivity.this, moSharedPreferenceManager.getAccountName(),
                            "dipakrana2001@gmail.com", token);
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}