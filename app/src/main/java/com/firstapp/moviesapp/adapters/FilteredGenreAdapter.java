package com.firstapp.moviesapp.adapters;

import static com.firstapp.moviesapp.utils.Constants.DEFAULT_GENRE_ID;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.models.Genre;

import java.util.List;

public class FilteredGenreAdapter extends RecyclerView.Adapter<FilteredGenreAdapter.ViewHolder> {
    List<Genre> genres;
    MutableLiveData<Genre> filteredGenre;
    LifecycleOwner activity;
    Context context;

    public FilteredGenreAdapter(LifecycleOwner activity, MutableLiveData<Genre> genreId, List<Genre> genres) {
        this.genres = genres;
        this.activity = activity;
        this.filteredGenre = genreId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_movie_genre, parent, false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genre.setOnClickListener((view) -> {
            Genre filteredGenreValue = filteredGenre.getValue();
            assert filteredGenreValue != null : "Genre is is null";
            // when genre is not filtered
            if (filteredGenreValue.id == DEFAULT_GENRE_ID)
                filteredGenre.postValue(genre);
            else {
                // remove filter
                if (filteredGenreValue.id == genre.id)
                    filteredGenre.postValue(new Genre(DEFAULT_GENRE_ID, ""));
                    // add filter
                else
                    filteredGenre.postValue(genre);
            }
        });
        filteredGenre.observe(activity, (id) -> {
            if (id.id == genre.id)
                // add filter
                holder.genre.setBackgroundResource(R.drawable.bg_movie_genre_is_chosen);
            else
                // remove filter
                holder.genre.setBackgroundResource(R.drawable.bg_movie_genre);
        });
        holder.genre.setText(genre.name);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        TextView genre = itemView.findViewById(R.id.tvGenre);
    }
}
