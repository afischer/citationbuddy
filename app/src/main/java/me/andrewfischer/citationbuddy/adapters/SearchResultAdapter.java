package me.andrewfischer.citationbuddy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.activities.DetailActivity;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;

/**
 * Created by afischer on 11/27/17.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder> {
    private ArrayList<GoogleBookResult> books;


    public SearchResultAdapter(ArrayList<GoogleBookResult> books) {
        this.books = books;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false);
        return new SearchResultHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        final GoogleBookResult book= books.get(position);
        Log.d("SearchResultAdapter", "Got imageItem " + book.toString());

        final Context holderContext = holder.itemView.getContext();
        holder.titleText.setText(book.getTitle());
        if (book.getAuthors() != null && book.getAuthors().size() > 0) {
            holder.authorText.setText(book.getAuthors().get(0));
        }
        holder.publisherText.setText(book.getPublisher());
        holder.dateText.setText(book.getPubDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailActivity = new Intent(holderContext, DetailActivity.class);
                detailActivity.putExtra("searchedBook", book);
                holderContext.startActivity(detailActivity);
            }
        });

        Log.d("SearchResultAdapter", "URL" + book.getThumbnailURL());
        Picasso.with(holder.coverImageView.getContext()).load(book.getThumbnailURL()).into(holder.coverImageView);
    }

    public class SearchResultHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.result_author)
        TextView authorText;

        @BindView(R.id.result_cover)
        ImageView coverImageView;

        @BindView(R.id.result_pubdate)
        TextView dateText;

        @BindView(R.id.result_publisher)
        TextView publisherText;

        @BindView(R.id.result_title)
        TextView titleText;

        public SearchResultHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }


    }


}
