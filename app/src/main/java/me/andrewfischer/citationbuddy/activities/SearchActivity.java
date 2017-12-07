package me.andrewfischer.citationbuddy.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.andrewfischer.citationbuddy.R;
import me.andrewfischer.citationbuddy.adapters.SearchResultAdapter;
import me.andrewfischer.citationbuddy.models.GoogleBookResult;
import me.andrewfischer.citationbuddy.services.GoogleBooksService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    String TAG = "SearchActivity";

    private SearchResultAdapter adapter;

    SurfaceView cameraView;

    @BindView(R.id.floating_search_view)
    FloatingSearchView searchView;

    @BindView(R.id.search_results_list)
    RecyclerView searchResultView;

    CameraSource cameraSource;
    private Camera camera = null;

    ArrayList<GoogleBookResult> bookResults = new ArrayList<>();
    String mLastSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        bindSearchBar();
        setupResultsList();
    }



    private void bindSearchBar() {
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                String menuID = getResources().getResourceName(item.getItemId());

                if (menuID.equals("me.andrewfischer.citationbuddy:id/button_scan")) {
                    Log.d("SearchActionButton", "Barcode button pressed");
                    initializeBarcodeScanner();
                }
            }
        });


        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (oldQuery.equals("") && newQuery.equals("")) {
                    bookResults.clear();
                    searchView.clearSuggestions();
                } else if (oldQuery.equals(newQuery)) {
                    searchView.clearSuggestions();
                } else {
                    Log.d("QueryCHange", "Performing search with query " + newQuery);
                    Log.d(TAG, "OLD: " + oldQuery + " NEW: " + newQuery);

                    searchView.showProgress();
                    GoogleBooksService.getService().getBook(newQuery).enqueue(new Callback<GoogleBooksService.GetResponse>() {
                        @Override
                        public void onResponse(Call<GoogleBooksService.GetResponse> call, Response<GoogleBooksService.GetResponse> response) {
                            if (response.body() != null && response.body().books != null) {
                                Log.d("SearchActivity", response.body().books.size() + " books found");
                                bookResults.clear();
                                bookResults.addAll(response.body().books);
                                searchView.hideProgress();
                                searchView.swapSuggestions(bookResults);
                                adapter.notifyDataSetChanged();
                            } else {
                                bookResults.clear();
                            }
                        }

                        @Override
                        public void onFailure(Call<GoogleBooksService.GetResponse> call, Throwable t) {
                            String url = call.request().url().toString();
                            Log.e("MainActivity", "Error requesting books from " + url, t);
                        }
                    });
                }
                mLastSearch = oldQuery;
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                // set last search to current body so we don't double search
                mLastSearch = searchSuggestion.getBody();
                searchView.showProgress();
                GoogleBooksService.getService().getBook(mLastSearch).enqueue(new Callback<GoogleBooksService.GetResponse>() {
                    @Override
                    public void onResponse(Call<GoogleBooksService.GetResponse> call, Response<GoogleBooksService.GetResponse> response) {
                        Log.d(TAG, "searching for " + searchSuggestion.getBody() + " " + mLastSearch);
                        if (response.body() != null && response.body().books != null) {
                            Log.d(TAG, response.body().books.size() + " books found");
                            bookResults.clear();
                            bookResults.addAll(response.body().books);
                            searchView.swapSuggestions(bookResults);
                            searchView.hideProgress();
                            adapter.notifyDataSetChanged();
                            searchView.clearSearchFocus();
                        }
                    }

                    @Override
                    public void onFailure(Call<GoogleBooksService.GetResponse> call, Throwable t) {
                        String url = call.request().url().toString();
                        Log.e(TAG, "Error requesting books from " + url, t);
                    }
                });;
            }

            @Override
            public void onSearchAction(String currentQuery) {
                Log.d("FloatingSearch", "Search Button clicked: " + currentQuery);
                searchView.clearSuggestions();
            }
        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        searchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                searchResultView.setTranslationY(newHeight);
            }
        });


        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
//                searchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                searchView.setSearchText(mLastSearch);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");


            }
        });

    }




    private void initializeBarcodeScanner() {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        final Context context = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_scanner, null);
        builder.setView(customLayout);
        final AlertDialog dialog = builder.create();

        cameraView = (SurfaceView) customLayout.findViewById(R.id.barcode_camera_view);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                    camera = getCamera(cameraSource);
                    Camera.Parameters params = camera.getParameters();

                    // fix FPS bug
                    final int[] previewFpsRange = new int[2];
                    params.getPreviewFpsRange(previewFpsRange);
                    if (previewFpsRange[0] == previewFpsRange[1]) {
                        final List<int[]> supportedFpsRanges = params.getSupportedPreviewFpsRange();
                        for (int[] range : supportedFpsRanges) {
                            if (range[0] != range[1]) {
                                params.setPreviewFpsRange(range[0], range[1]);
                                break;
                            }
                        }
                    }
                    camera.setParameters(params);
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    camera.setParameters(params);


                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }

        });

        // Set barcode processor
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    searchView.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            String barcodeText = barcodes.valueAt(barcodes.size() - 1).displayValue;
                            searchView.setSearchText(barcodeText);
                            dialog.hide();
//                            performSearch(barcodeText);
                        }
                    });
                }
            }
        });

        // show alert dialog w/ camera
        dialog.show();
    }

    private void setupResultsList() {
        adapter = new SearchResultAdapter(bookResults);
        searchResultView.setAdapter(adapter);
        searchResultView.setLayoutManager(new LinearLayoutManager(searchResultView.getContext()));
    }


    /**
     * Returns the Camera object for given CameraSource
     * Taken from https://stackoverflow.com/q/35811411
     * @param cameraSource
     * @return the Camera
     */
    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

}
