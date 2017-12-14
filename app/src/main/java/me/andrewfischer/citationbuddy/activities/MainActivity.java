package me.andrewfischer.citationbuddy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.activities.SearchActivity;
import me.andrewfischer.citationbuddy.adapters.CitationListAdapter;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ArrayList<String> citations = new ArrayList<>();

    @BindView(R.id.button_add)
    FloatingActionButton addBtn;

    @BindView(R.id.citation_list)
    RecyclerView citationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestPermissions();

        RecyclerView.Adapter adapter = new CitationListAdapter(citations);
        citationListView.setAdapter(adapter);
        citationListView.setLayoutManager(new LinearLayoutManager(citationListView.getContext()));

        SQLiteDatabase db = openOrCreateDatabase("citationdb",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Books(citation VARCHAR);");

        Cursor resultSet = db.rawQuery("Select * from Books",null);
        for (int i = 0; i < resultSet.getCount(); i++) {
            resultSet.moveToNext();
            citations.add(resultSet.getString(0));
            adapter.notifyDataSetChanged();
            Log.d(TAG, resultSet.getString(0));
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchActivity);
            }
        });
    }

    private void requestPermissions() {
        Log.d("MainActivity", "Requesting permissions");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String[] permissions, int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permissions_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.permissions_denied, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}
