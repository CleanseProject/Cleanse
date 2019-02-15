package com.cleanseproject.cleanse.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.EventDetailsActivity;
import com.cleanseproject.cleanse.dataClasses.Event;

import java.util.ArrayList;

public class AdaptadorRecyclerViews extends RecyclerView.Adapter<AdaptadorRecyclerViews.MyViewHolder>{
private ArrayList<Event>listaEventos;

    public AdaptadorRecyclerViews(ArrayList<Event>listaEventos) {
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public AdaptadorRecyclerViews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vista_evento,null,false);


        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.asignarDatos(listaEventos.get(i));

    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitulo;
        public ImageView ivFoto;
        public ImageButton btnLike, btnShare;
        public boolean liked;
        public MyViewHolder(View v) {

            super(v);
            txtTitulo=v.findViewById(R.id.tvTitulo);
            btnLike=v.findViewById(R.id.btnLike);
            btnShare=v.findViewById(R.id.btnShare);
            ivFoto=v.findViewById(R.id.ivEvento);
            liked=false;

        }

        public void asignarDatos(Event event) {

            txtTitulo.setText(event.getName());


            ivFoto.setBackgroundResource(R.drawable.imagen);

            ivFoto.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(this, EventDetailsActivity.class);
//                    intent.putExtra("Evento", event);
//                    startActivity(intent);
                }
            });


            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!liked){
                        btnLike.setImageResource(R.drawable.corazon_rosa);
                        liked=true;

                    }else{
                        liked=false;
                        btnLike.setImageResource(R.drawable.corazon_azul);
                    }

                }
            });
            }

    }

}
