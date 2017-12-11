package me.andrewfischer.citationbuddy.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tupilabs.human_name_parser.HumanNameParser;
import com.tupilabs.human_name_parser.ParsedName;
import com.tupilabs.human_name_parser.SegmentedName;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;

public class DetailActivity extends AppCompatActivity {
    private final String TAG = "DetailActivity";

    @BindView(R.id.source_contributors)
    LinearLayout contributorView;

//    @BindView(R.id.author_fname)
//    EditText firstName;
//
//    @BindView(R.id.author_lname)
//    EditText lastName;
//
//    @BindView(R.id.author_mi)
//    EditText middleInitial;
//
//    @BindView(R.id.author_suffix)
//    EditText suffix;

    @BindView(R.id.source_title)
    EditText title;

    @BindView(R.id.source_edition)
    EditText edition;

    @BindView(R.id.source_pubdate)
    EditText pubDate;

    @BindView(R.id.source_publisher)
    EditText publisher;

    @BindView(R.id.source_series)
    EditText series;

    @BindView(R.id.source_vol)
    EditText volume;

    @BindView(R.id.source_vol_count)
    EditText volumeCount;

    @BindView(R.id.citation_confirm)
    Button confirmButton;


    private TextWatcher getNewContributorWatcher() {
        return new TextWatcher() {
            final View contributor = getLayoutInflater().inflate(R.layout.detail_contributor, null);
            boolean addedField = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // STUB
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    Log.d(TAG, "EMPTY!");
                    if (addedField) {
                        Log.d(TAG, "Removing empty contributor field");
                        contributorView.removeView(contributor);
                        addedField = false;

                    }
                } else {
                    if (!addedField) {
                        Log.d(TAG, "Adding new contributor field");
                        contributorView.addView(contributor);
                        TextWatcher newWatcher = getNewContributorWatcher();
                        EditText fName = (EditText) contributor.findViewById(R.id.author_fname);
                        EditText lname = (EditText) contributor.findViewById(R.id.author_lname);
                        EditText mi = (EditText) contributor.findViewById(R.id.author_mi);
                        EditText suff = (EditText) contributor.findViewById(R.id.author_suffix);
                        fName.addTextChangedListener(newWatcher);
                        lname.addTextChangedListener(newWatcher);
                        mi.addTextChangedListener(newWatcher);
                        suff.addTextChangedListener(newWatcher);
                        addedField = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bdl = getIntent().getExtras();
        final GoogleBookResult bookResult = bdl.getParcelable("searchedBook");

        // TITLE AND PUBLISHER
        title.setText(bookResult.getTitle());
        publisher.setText(bookResult.getPublisher());
        pubDate.setText(bookResult.getPubDate());

        // AUTHORS
        if (bookResult.getAuthors() != null && bookResult.getAuthors().size() > 0) {
            // add all authors and one extra field
            for (String author : bookResult.getAuthors()) {
                // create view
                final View contributor = getLayoutInflater().inflate(R.layout.detail_contributor, null);

                // segment author name
                HumanNameParser parser = new HumanNameParser();
                ParsedName parsedName = parser.parse(author);
                SegmentedName name = parsedName.toSegmented();

                // get edittexts
                EditText fName = (EditText) contributor.findViewById(R.id.author_fname);
                EditText lname = (EditText) contributor.findViewById(R.id.author_lname);
                EditText mi = (EditText) contributor.findViewById(R.id.author_mi);
                EditText suff = (EditText) contributor.findViewById(R.id.author_suffix);

                // set text
                fName.setText(name.getFirst());
                if (name.getMiddle() != null) { mi.setText(name.getMiddle().substring(0, 1)); }
                lname.setText(name.getLast());
                suff.setText(name.getSuffix());

                // add watcher
                TextWatcher newWatcher = getNewContributorWatcher();

                fName.addTextChangedListener(newWatcher);
                lname.addTextChangedListener(newWatcher);
                mi.addTextChangedListener(newWatcher);
                suff.addTextChangedListener(newWatcher);

                // add View
                contributorView.addView(contributor);
            }
            addEmptyContributorView();
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("MLA Citation" )
                        .create();

                TextView view = new TextView( dialog.getContext() );
                view.setText( Html.fromHtml(bookResult.getMLACitation()) );
                view.setPadding( 18, 18, 18, 18 );
                view.setTextSize(16);
                dialog.setView( view );
                dialog.show();
            }
        });
    }

    private void addEmptyContributorView() {
        // create view
        final View contributor = getLayoutInflater().inflate(R.layout.detail_contributor, null);
        // add watcher
        TextWatcher newWatcher = getNewContributorWatcher();

        // get edittexts
        EditText fName = (EditText) contributor.findViewById(R.id.author_fname);
        EditText lname = (EditText) contributor.findViewById(R.id.author_lname);
        EditText mi = (EditText) contributor.findViewById(R.id.author_mi);
        EditText suff = (EditText) contributor.findViewById(R.id.author_suffix);

        fName.addTextChangedListener(newWatcher);
        lname.addTextChangedListener(newWatcher);
        mi.addTextChangedListener(newWatcher);
        suff.addTextChangedListener(newWatcher);

        // add View
        contributorView.addView(contributor);
    }

}
