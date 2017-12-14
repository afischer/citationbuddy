package me.andrewfischer.citationbuddy.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;

/**
 * Created by afischer on 12/13/17.
 */

public class CitationListAdapter extends RecyclerView.Adapter<CitationListAdapter.CitationHolder> {
    private ArrayList<String> citations = new ArrayList<>();

    public CitationListAdapter(ArrayList<String> citations) {
        this.citations = citations;
    }


    @Override
    public CitationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_citation, parent, false);
        return new CitationHolder(view);
    }

    @Override
    public void onBindViewHolder(CitationHolder holder, int position) {
        holder.citationText.setText(Html.fromHtml(citations.get(position)));
    }

    @Override
    public int getItemCount() {
        return citations.size();
    }


    public class CitationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cite_item_text)
        TextView citationText;

        @BindView(R.id.cite_item_delete)
        ImageButton deleteButton;

        @BindView(R.id.cite_item_share)
        ImageButton shareButton;

        public CitationHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}
