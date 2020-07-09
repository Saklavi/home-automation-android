package com.wolty.homeautomation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wolty.homeautomation.userModel.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Context mContext;
    protected EditText inputLogin;
    protected EditText inputPassword;
    protected Button buttonLogin;
    protected Map<String, String> postParam;
    protected Utils utils;
    protected ProgressDialog progressDialog;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;
        utils = new Utils(mContext);
        requestQueue = Volley.newRequestQueue(mContext);

        //If there is a token -> redirect to dashboard and save token to user model
        String tmp_token = utils.retrieveToken();
        //Toast.makeText(mContext, tmp_token, Toast.LENGTH_SHORT).show();
        if(tmp_token.length() > 0) {
            User.setToken(tmp_token);
            fetchUser();
        }

        inputLogin = findViewById(R.id.inputLogin);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postParam = new HashMap<>();
                //TODO: CHECK IF NOT EMPTY
                postParam.put("email", inputLogin.getText().toString());
                postParam.put("password", inputPassword.getText().toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                utils.getBaseUrl() + "/user/login",
                    new JSONObject(postParam),
                    new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext, "Login success !", Toast.LENGTH_SHORT).show();

                        try {
                            //Retrieve token from json object and store it in User model and Shared Preferences for future.
                            String token = response.getString("access_token");
                            User.setToken(token);
                            utils.storeToken(token);

                            takeToDashboard();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        String status;

                        if (code == 401) {
                            status = "Wrong credentials.";
                        } else if (code == 422){
                            status = "Bad syntax.";
                        }
                        else {
                            status = "Server error. Try again later.";
                        }

                        Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
                    }
                }) {
                @Override
                public Map<String, String> getHeaders() {
                    return utils.setHeaders(null);
                }
            };

            requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void fetchUser() {
        progressDialog = ProgressDialog.show(mContext, "Logging in", "Please wait for a while.", true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                utils.getBaseUrl() + "/user",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("status"))
                            {
                                if(response.getString("status").equals("success"))
                                {
                                    JSONObject data = response.getJSONObject("user");
                                    User.setEmail(data.getString("email"));
                                    User.setName(data.getString("name"));

                                    //Toast.makeText(mContext, User.getEmail(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    takeToDashboard();
                                } else {
                                    destroyToken();
                                    progressDialog.dismiss();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return utils.setHeaders(User.getToken());
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void takeToDashboard() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void destroyToken() {
        User.setToken("");
        utils.storeToken("");
    }
}