package com.firstapp.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.models.MovieDetail;
import com.firstapp.moviesapp.ui.activities.DetailActivity;
import com.firstapp.moviesapp.utils.Constants;

import java.text.DecimalFormat;
import java.util.List;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.ViewHolder> {
    List<MovieDetail> mMovies;
    Context mContext;

    public FavouriteMoviesAdapter(List<MovieDetail> movies) {
        mMovies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_favourite_movies,
                parent, false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieDetail movie = mMovies.get(position);
        holder.container.setOnClickListener((v) -> {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(Constants.MOVIE_ID, movie.id);
            intent.putExtra(Constants.VOTE_AVERAGE, movie.vote_average);
            intent.putExtra(Constants.OVERVIEW, movie.overview);
            intent.putExtra(Constants.TITLE, movie.title);
            intent.putExtra(Constants.BACKDROP_IMAGE_PATH, movie.backdrop_path);
            intent.putExtra(Constants.POSTER_PATH, movie.poster_path);
            mContext.startActivity(intent);
        });
        holder.title.setText(movie.title);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        holder.voteAverage.setText(decimalFormat.format(movie.vote_average));
        holder.releaseDay.setText(movie.release_date);
        holder.overview.setText(movie.overview);
        Glide.with(mContext)
            .load(Constants.IMAGE_HOST_URL + movie.poster_path)
            .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        ImageView thumbnail = itemView.findViewById(R.id.ivThumbnail);
        TextView title = itemView.findViewById(R.id.tvTitle);
        TextView voteAverage = itemView.findViewById(R.id.tvVoteAverage);
        TextView releaseDay = itemView.findViewById(R.id.tvReleaseDay);
        TextView overview = itemView.findViewById(R.id.tvOverview);
        View container = itemView.findViewById(R.id.container);
    }
}
