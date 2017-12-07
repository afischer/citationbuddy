package me.andrewfischer.citationbuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.tupilabs.human_name_parser.HumanNameParser;
import com.tupilabs.human_name_parser.ParsedName;
import com.tupilabs.human_name_parser.SegmentedName;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.author_fname)
    EditText firstName;

    @BindView(R.id.author_lname)
    EditText lastName;

    @BindView(R.id.author_mi)
    EditText middleInitial;

    @BindView(R.id.author_suffix)
    EditText suffix;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bdl = getIntent().getExtras();
        GoogleBookResult bookResult = bdl.getParcelable("searchedBook");

        title.setText(bookResult.getTitle());
        publisher.setText(bookResult.getPublisher());
        pubDate.setText(bookResult.getPubDate());
        HumanNameParser parser = new HumanNameParser();
        ParsedName parsedName = parser.parse(bookResult.getAuthors().get(0));
        SegmentedName name = parsedName.toSegmented();
        firstName.setText(name.getFirst());
        middleInitial.setText(name.getMiddle());
        lastName.setText(name.getLast());
        suffix.setText(name.getSuffix());
    }
}
