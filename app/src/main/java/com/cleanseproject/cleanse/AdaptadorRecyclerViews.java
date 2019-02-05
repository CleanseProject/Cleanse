package com.cleanseproject.cleanse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.dataClasses.Event;

import java.util.ArrayList;

public class AdaptadorRecyclerViews extends RecyclerView.Adapter<AdaptadorRecyclerViews.MyViewHolder> {
private ArrayList<Event>listaEventos;

    public AdaptadorRecyclerViews(ArrayList<Event>listaEventos) {
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public AdaptadorRecyclerViews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtTitulo;
        public ImageView btnLike,btnShare,ivFoto;
        public MyViewHolder(TextView v, ImageView btnLike,ImageView btnShare, ImageView ivFoto) {

            super(v);
            this.txtTitulo = v;
            this.btnLike=btnLike;
            this.btnShare=btnShare;
            this.ivFoto=ivFoto;

        }
    }
}
