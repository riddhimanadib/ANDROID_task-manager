package adib.riddhiman.testapplication_taskmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    String email;
    String password;
    String apikey;
    EditText editTextRegEmail;
    EditText editTextRegPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextRegEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextRegPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        Button button = (Button) findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editTextRegEmail.getText().toString();
                password = editTextRegPassword.getText().toString();

                if ( email.equals("") || password.equals(""))
                    Toast.makeText(LoginActivity.this, "Enter values in all fields", Toast.LENGTH_SHORT).show();
                else {
                    loginUser(email, password);
                }
            }
        });
    }

    public void loginUser(String email, String password) {

        //String url = "http://103.9.185.219:8080/harriken/Json_index.php";
        String url = new AppUrl().URL_LOGIN;

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Submitting...");
            pDialog.show();

            RequestParams params = new RequestParams();
            params.put("email",email);
            params.put("password",password);

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);

            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject json = new JSONObject(response);
                        Boolean error = json.getBoolean("error");

                        pDialog.hide();
                        if(!error)
                        {
                            //Toast.makeText(LoginActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            apikey = json.getString("apiKey");
                            final Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            intent.putExtra("apiKey", apikey);
                            finish();
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login Failed : Incorrect Credentials", Toast.LENGTH_SHORT).show();
                        }

                        pDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    pDialog.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
