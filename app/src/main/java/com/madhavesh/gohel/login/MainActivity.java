package com.madhavesh.gohel.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static java.lang.System.load;

public class MainActivity extends AppCompatActivity {


    private static final String EMAIL = "email" ;
    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView textname,textEmail;

    private CallbackManager callbackManager;
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textEmail = (TextView)findViewById(R.id.email);
        textname = (TextView)findViewById(R.id.fullname);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        circleImageView = (CircleImageView) findViewById(R.id.image);



        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","profile_photo"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
                if(currentAccessToken==null)
                {
                    textname.setText("");
                    textEmail.setText("");
                    circleImageView.setImageResource(0);

                    Toast.makeText(MainActivity.this, "User Logged out ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadUserProfile(currentAccessToken);
                }
        }
    };

    private void loadUserProfile(AccessToken newaccesstoken)
    {
        GraphRequest graphRequest = GraphRequest.newMeRequest(newaccesstoken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email =object.getString("email");
                    String id = object.getString("id");

                    String img_url = "https://graph.facebook.com/"+id+"/picture?type=normal";

                    textname.setText(first_name +" "+last_name);
                    textEmail.setText(email);

                    RequestOptions requestOptions  = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(img_url).into(circleImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parmaeters =new Bundle();
        parmaeters.putString("fields","first_name,last_name,email,id");
        graphRequest.setParameters(parmaeters);
        graphRequest.executeAsync();
    }

    public void show(View view) {
        Intent intent =new Intent(MainActivity.this,UserProfile.class);
        startActivity(intent);
    }
}