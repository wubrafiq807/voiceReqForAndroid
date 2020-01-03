package com.istvn.speechrecognitionsystem.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.istvn.speechrecognitionsystem.BuildConfig;
import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Facebook
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    // Google
    private static final int RC_SIGN_IN = 22;
    GoogleSignInClient mGoogleSignInClient;

    // Twitter
    TwitterLoginButton twitterLoginButton;

    private static final String TAG = "RRR";
    ProgressDialog progressDialog;

    EditText usernameEditText;
    TextView forgotPassword;

    // Our custom shared preference class object
    private SaveDataOnPreference sharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Twitter.initialize(this);

        setContentView(R.layout.activity_login);
        sharedPreference = new SaveDataOnPreference(this);

        if (sharedPreference.getUserId() != null){
            startMainActivity();
        }

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotPasswordTextView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO: Do Something When Text Typing Done.
                    //Log.d(TAG, "onEditorAction: ");
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = usernameEditText.getText().toString().trim();

                // Login Authentication
                networkCallLogin(email, passwordEditText.getText().toString().trim());

            }
        });

        /** Social Login Integration Start From Here */

        /** Facebook */

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        LoginButton fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setOnClickListener(this);

        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    String email = object.getString("email");

                                    if(!networkCallExistEmail(email))
                                        networkCallRegisterUser(email, 2);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: Facebook Login");
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };

        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        /*if (BuildConfig.DEBUG)
            Log.d(TAG, "Facebook isLoggedIn: " + isLoggedIn);*/

        // Get the Profile information of Facebook
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                /*if (BuildConfig.DEBUG && currentProfile != null)
                    Log.d(TAG, "Facebook Profile: " + currentProfile.getFirstName() + ", " + currentProfile.getLastName());*/
            }
        };

        LoginManager.getInstance().logOut();
        /** Facebook Integration End */

        /** Google */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        // Set the dimensions of the sign-in button.
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(this);
        /** Google Integration END */

        /** Twitter */
        twitterLoginButton = findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                twitterLogin(session);
            }

            @Override
            public void failure(TwitterException exception) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "failure TwitterException: " + exception);
            }
        });

        /** Twitter Integration End*/

        // Registration TextView Click Listener
        TextView registrationTextView = findViewById(R.id.registrationTextView);
        registrationTextView.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

    }

    /**
     * If User Registered Already
     * @param
     */
     private boolean networkCallExistEmail(String existEmail) {
         progressDialog.show();
         GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
         Call<LoginResponse> call = service.isExistUser("/api-v1/users/check/" + existEmail);
         boolean rs = false;
         try {
             LoginResponse result = call.execute().body();
             //Log.d("RRR", "networkCall: Exist User: " + result.getMessage());

             if (result.getResult() != null) {
                 rs = true;
                 sharedPreference.saveUser(result);
                 startMainActivity();
             }

             progressDialog.dismiss();
         } catch (IOException e) {
             e.printStackTrace();
             progressDialog.dismiss();
         }

         return rs;
     }

    /**
     * If Social Account Account is Valid, Then give login access
     * @param email
     * @param login_type
     */
    private void networkCallRegisterUser(final String email, int login_type) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.registerUser(email, null, login_type);
        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //Log.d(TAG, "onResponse: Social Login Success");
                startMainActivity();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: Social Site Login: " +t);
            }
        });
    }

    /**
     * Login Authentication Method
     * @param username
     * @param password
     */
    private void networkCallLogin(final String username, String password) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.getUser("/api-v1/users/?email=" + username +"&password=" + password);
        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                //Log.d(TAG, "onResponse: "+ response.body().getResult());
                if (response.body().getResult() != null){

                    sharedPreference.saveUser(response.body());
                    startMainActivity();
                }else {
                    usernameEditText.setError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Twitter Login Method
     * @param session
     */
    private void twitterLogin(final TwitterSession session) {

        /*if (BuildConfig.DEBUG)
            Log.d(TAG, "Twitter UserName: " + session.getUserName());*/

        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {

                /*if (BuildConfig.DEBUG)
                    Log.d(TAG, "success Twitter: " + result.data);*/

                if(!networkCallExistEmail(result.data))
                    networkCallRegisterUser(result.data, 4);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        /** For Facebook */
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        /** For Google */
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        /** For Twitter */
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * For Facebook
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    /** Google Integration Method Start */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            //Intent intent = new Intent(this, MainActivity.class);

            /*if (BuildConfig.DEBUG)
                Log.d(TAG, "Google updateUI: " + account.getDisplayName() + ", " + account.getEmail());*/

           // Bundle bundle = new Bundle();
           // bundle.putParcelable("account", account);

           // intent.putExtras(bundle);

           // startActivity(intent);
           // this.finish();

            if(!networkCallExistEmail(account.getEmail()))
                networkCallRegisterUser(account.getEmail(), 3);

            signOut();

        } else {

            /*if (BuildConfig.DEBUG)
                Log.d(TAG, "updateUI: Account Not Found");*/
        }
    }

    /**
     * OnClick Action
     * @param v -> View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.registrationTextView:
                gotToRegistrationPage();
                break;
            case R.id.forgotPasswordTextView:
                startActivity(new Intent(this, ResetPassword.class));
                break;
        }
    }

    /**
     * Move to Registration Page
     */
    private void gotToRegistrationPage() {

        Intent intent = new Intent(this, UserRegistration.class);
        startActivity(intent);
    }

    /**
     * Sigh In method for Google
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Handle sign in result for Google
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            /*if (BuildConfig.DEBUG)
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());*/
            updateUI(null);
        }
    }

    /**
     * Sign Out method for google
     */
    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    /** Google Integration Method End */

}
