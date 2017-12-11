package me.andrewfischer.citationbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tupilabs.human_name_parser.HumanNameParser;
import com.tupilabs.human_name_parser.ParsedName;
import com.tupilabs.human_name_parser.SegmentedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by afischer on 11/27/17.
 */


public class GoogleBookResult implements Parcelable, SearchSuggestion {

    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("volumeInfo")
    @Expose
    private BookInfo bookInfo;

    public String getKind() {
        return kind;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return bookInfo.getTitle();
    }

    public String getPubDate() {
        return bookInfo.getPublishedDate();
    }
    public void setPubDate(String pubDate) {
        bookInfo.publishedDate = pubDate;
    }

    public List<String> getAuthors() {
        return bookInfo.getAuthors();
    }
    public void SetAuthors(List<String> authors) { bookInfo.authors = authors; }

    public String getThumbnailURL() {
        if (bookInfo.getImageLinks() != null) {
            return bookInfo.getImageLinks().get("smallThumbnail");
        }
        return null;
    }

    public String getPublisher() {
        return bookInfo.getPublisher();
    }

    @Override
    public String toString() {
        return "<" + bookInfo.getPrintType() + " " + id  +" (" + bookInfo.title + ")>";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookInfo.getTitle());
        dest.writeString(bookInfo.getPublisher());
        dest.writeString(bookInfo.getPublishedDate());
        dest.writeString(getThumbnailURL());
        if (bookInfo.getAuthors() != null && bookInfo.authors.size() > 0) {
            dest.writeInt(bookInfo.getAuthors().size());
            for (String author : bookInfo.getAuthors()) {
                dest.writeString(author);
            }
        } else {
            dest.writeInt(1);
            dest.writeString("");
        }
    }

    protected GoogleBookResult(Parcel in) {
        this.bookInfo = new BookInfo();
        bookInfo.setTitle(in.readString());
        bookInfo.setPublisher(in.readString());
        setPubDate(in.readString());
        bookInfo.setThumbnailURL(in.readString());
        int numAuthors = in.readInt();
        for (int i = 0; i < numAuthors; i++) {
            bookInfo.addAuthor(in.readString());
        }
    }


    public static final Creator<GoogleBookResult> CREATOR = new Creator<GoogleBookResult>() {
        @Override
        public GoogleBookResult createFromParcel(Parcel in) {
            return new GoogleBookResult(in);
        }

        @Override
        public GoogleBookResult[] newArray(int size) {
            return new GoogleBookResult[size];
        }
    };

    @Override
    public String getBody() {
        return getTitle();
    }

    public String getMLACitation() {
        String citation = "";
        for (String author : getAuthors()) {
            HumanNameParser parser = new HumanNameParser();
            ParsedName parsedName = parser.parse(author);
            SegmentedName name = parsedName.toSegmented();
            citation += name.getLast() + ", ";
            citation += name.getFirst();
            if (name.getMiddle() != null) {
                citation += " " + name.getMiddle().substring(0, 1) + ". ";
            } else {
                citation += ". ";
            }
        }
        citation += "<i>" + getTitle() + "</i>. ";
        citation += getPublisher() + ", " + getPubDate() + ". Print.";
        return citation;
    }

    class BookInfo {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("subtitle")
        @Expose
        private String subtitle;
        @SerializedName("authors")
        @Expose
        private List<String> authors = null;
        @SerializedName("publisher")
        @Expose
        private String publisher;
        @SerializedName("publishedDate")
        @Expose
        private String publishedDate;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("industryIdentifiers")
        @Expose
        private List<Map<String, String>> industryIdentifiers = null;
        @SerializedName("pageCount")
        @Expose
        private Integer pageCount;
        @SerializedName("printType")
        @Expose
        private String printType;
        @SerializedName("categories")
        @Expose
        private List<String> categories = null;
        @SerializedName("maturityRating")
        @Expose
        private String maturityRating;
        @SerializedName("allowAnonLogging")
        @Expose
        private transient Boolean allowAnonLogging;

        @SerializedName("imageLinks")
        @Expose
        private Map<String, String> imageLinks;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("previewLink")
        @Expose
        private String previewLink;
        @SerializedName("infoLink")
        @Expose
        private String infoLink;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public List<String> getAuthors() {
            return authors;
        }
        public void addAuthor(String author) {
            if (bookInfo.authors == null) {
                bookInfo.authors = new ArrayList<String>();
            }
            bookInfo.authors.add(author);
        }
        public void setAuthors(List<String> authors) {
            bookInfo.authors = authors;
        }


        public String getPublisher() {
            return publisher;
        }
        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }


        public String getPublishedDate() {
            return publishedDate;
        }

        public String getDescription() {
            return description;
        }

        public List<Map<String, String>> getIgetIndustryIdentifiersndustryIdentifiers() {
            return industryIdentifiers;
        }

        public Integer getPageCount() {
            return pageCount;
        }


        public String getPrintType() {
            return printType;
        }

        public List<String> getCategories() {
            return categories;
        }


        public String getMaturityRating() {
            return maturityRating;
        }

        public Map<String, String> getImageLinks() {
            return imageLinks;
        }
        public void setThumbnailURL(String URL) {
            imageLinks = new HashMap<String, String>();
            imageLinks.put("smallThumbnail", URL);
        }

        public String getLanguage() {
            return language;
        }

        public String getPreviewLink() {
            return previewLink;
        }

        public String getInfoLink() {
            return infoLink;
        }

    }

}

