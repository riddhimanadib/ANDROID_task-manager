package adib.riddhiman.testapplication_taskmanager;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    String apiKey;
    ArrayList<String> taskList;
    ArrayAdapter adapter;
    ListView listView;
    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewTaskList);
        listView.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.textViewNoTask);
        editText = (EditText) findViewById(R.id.editTextTask);

        apiKey = getIntent().getStringExtra("apiKey");

        taskList = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        getAllTask(apiKey);

        Button buttonAdd = (Button) findViewById(R.id.buttonAddTask);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals(""))
                    addTask(editText.getText().toString());
            }
        });
    }

    public void getAllTask(String apiKey) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Submitting...");
        pDialog.show();

        String url = new AppUrl().URL_TASKS;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.addHeader("Authorization", apiKey);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    pDialog.hide();
                    if (!error) {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray = json.getJSONArray("tasks");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            taskList.add(jsonArray.getJSONObject(i).getString("task"));
                        }

                        adapter.addAll(taskList);
                        listView.setAdapter(adapter);
                        textView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(MainActivity.this, "Error at Tasks", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void addTask(final String task) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Submitting...");
        pDialog.show();

        String url = new AppUrl().URL_TASKS;

        RequestParams params = new RequestParams();
        params.put("task", task);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.addHeader("Authorization", apiKey);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    pDialog.hide();
                    if (!error) {
                        adapter.clear();
                        taskList.add(task);
                        adapter.addAll(taskList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        textView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        editText.setText("");

                    } else {
                        Toast.makeText(MainActivity.this, "Error at saving Tasks", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
