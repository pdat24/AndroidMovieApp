package com.firstapp.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.models.Movie;
import com.firstapp.moviesapp.ui.activities.DetailActivity;
import com.firstapp.moviesapp.utils.Constants;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    List<Movie> movies;
    Context context;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_movie, parent, false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.container.setOnClickListener((view) -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Constants.MOVIE_ID, movie.id);
            intent.putExtra(Constants.VOTE_AVERAGE, movie.vote_average);
            intent.putExtra(Constants.OVERVIEW, movie.overview);
            intent.putExtra(Constants.TITLE, movie.title);
            intent.putExtra(Constants.BACKDROP_IMAGE_URL, movie.backdrop_path);
            context.startActivity(intent);
        });
        holder.title.setText(movie.title);
        Glide.with(context)
            .asDrawable()
            .load(Constants.IMAGE_HOST_URL + movie.poster_path)
            .into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(
                    @NonNull Drawable resource,
                    @Nullable Transition<? super Drawable> transition
                ) {
                    holder.thumbnail.setBackground(resource);
                }
            });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        View container = itemView.findViewById(R.id.container);
        TextView title = itemView.findViewById(R.id.tvMovieTitle);
        ImageView thumbnail = itemView.findViewById(R.id.ivMovieThumbnail);
    }
}
