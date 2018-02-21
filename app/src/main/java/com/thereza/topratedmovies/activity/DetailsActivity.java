package com.thereza.topratedmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thereza.topratedmovies.R;
import com.thereza.topratedmovies.api.MovieApi;
import com.thereza.topratedmovies.api.MovieService;
import com.thereza.topratedmovies.models.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    TextView title, year, desc;
    ImageView img;
    private int id = 0;
    private MovieService movieService;
    ProgressBar progressBar;
    private static final String TAG = "DetailsActivity";
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        getDeatilsData();
        movieService = MovieApi.getClient().create(MovieService.class);
        loadFirstPage();
    }

    public void initView(){
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        title =(TextView) findViewById(R.id.titleTxt);
        year =(TextView) findViewById(R.id.yearTxt);
        desc =(TextView) findViewById(R.id.descTxt);
        img =(ImageView) findViewById(R.id.imgPoster);

    }

    public  void getDeatilsData(){

        Bundle extras = getIntent().getExtras();

        String mId="";

        if (extras != null) {
            mId = extras.getString("movieId");

        }
        id = Integer.parseInt(mId);

    }

    private void loadFirstPage() {
        Call<Result> call = movieService.getDetailsById(
                id,
                getString(R.string.my_api_key)
        );

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {

                    Glide
                            .with(getBaseContext())
                            .load(BASE_URL_IMG+response.body().getPosterPath())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                            .crossFade()
                            .into(img);
                    year.setText( response.body().getReleaseDate().substring(0,4)  // we want the year only
                            + " | "
                            + response.body().getOriginalLanguage().toUpperCase());
                    title.setText( response.body().getTitle());
                    desc.setText( response.body().getOverview());
                    Log.d(TAG, "onResponse: "+ response.body().getId());
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }


}
