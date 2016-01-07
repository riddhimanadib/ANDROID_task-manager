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

public class RegisterActivity extends AppCompatActivity {
    String name;
    String email;
    String password;
    EditText editTextRegName;
    EditText editTextRegEmail;
    EditText editTextRegPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegName = (EditText) findViewById(R.id.editTextRegName);
        editTextRegEmail = (EditText) findViewById(R.id.editTextRegEmail);
        editTextRegPassword = (EditText) findViewById(R.id.editTextRegPass);

        Button button = (Button) findViewById(R.id.buttonRegSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextRegName.getText().toString();
                email = editTextRegEmail.getText().toString();
                password = editTextRegPassword.getText().toString();

                if (name.equals("") || email.equals("") || password.equals(""))
                    Toast.makeText(RegisterActivity.this, "Enter values in all fields", Toast.LENGTH_SHORT).show();
                else {
                    registerUser(name, email, password);
                }
            }
        });
    }

    public void registerUser(String name, String email, String password) {

        String url = new AppUrl().URL_REGISTER;

        try{
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Submitting...");
            pDialog.show();

            RequestParams params = new RequestParams();
            params.put("name",name);
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
                            Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            final Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, LoginActivity.class);
                            finish();
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "User ID already Exists", Toast.LENGTH_SHORT).show();
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
