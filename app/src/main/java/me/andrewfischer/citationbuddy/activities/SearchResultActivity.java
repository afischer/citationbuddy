package me.andrewfischer.citationbuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.adapters.SearchResultAdapter;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;

public class SearchResultActivity extends AppCompatActivity {
    private ArrayList<GoogleBookResult> books = new ArrayList<GoogleBookResult>(); // image items for recycler view
    private SearchResultAdapter adapter;

    @BindView(R.id.search_results)
    RecyclerView searchResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        Bundle bdl = getIntent().getExtras();
        books = bdl.getParcelableArrayList("results");

        adapter = new SearchResultAdapter(books);
        searchResultView.setLayoutManager(new LinearLayoutManager(this));
        searchResultView.setAdapter(adapter);
    }
}
