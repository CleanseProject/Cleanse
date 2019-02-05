package com.cleanseproject.cleanse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vista_evento,null,false);


        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.asignarDatos(listaEventos.get(i));

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtTitulo;
        public ImageView btnLike,btnShare,ivFoto;
        public MyViewHolder(View v) {

            super(v);
            txtTitulo=v.findViewById(R.id.txtTitulo);
            btnLike=v.findViewById(R.id.btnLike);
            btnShare=v.findViewById(R.id.btnShare);
            ivFoto=v.findViewById(R.id.ivFoto);

        }

        public void asignarDatos(Event event) {

            txtTitulo.setText(event.getName());
            //ivFoto.setImage

            }
    }
}
