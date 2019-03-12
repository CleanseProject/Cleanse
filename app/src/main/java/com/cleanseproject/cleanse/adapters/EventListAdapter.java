package com.cleanseproject.cleanse.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.EventDetailsActivity;
import com.cleanseproject.cleanse.activities.HomeActivity;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.fragments.HomeFragment;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.ImageManagerService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Event> listaEventos;

    public EventListAdapter(Context context, ArrayList<Event> listaEventos) {
        this.context = context;
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public EventListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vista_evento, null, false);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private EventManagerService eventManagerService;
        private ImageManagerService imageManagerService;
        private TextView txtTitulo, txtDistancia;
        private ImageView ivFoto;
        private ImageButton btnLike;
        private Context context;

        public MyViewHolder(View v) {
            super(v);
            txtTitulo = v.findViewById(R.id.tvTitulo);
            txtDistancia = v.findViewById(R.id.txtDistancia);
            btnLike = v.findViewById(R.id.btnLike);
            ivFoto = v.findViewById(R.id.ivEvento);
            context = v.getContext();
            imageManagerService = new ImageManagerService();
            eventManagerService = new EventManagerService();
        }

        public void asignarDatos(Event event) {
            txtTitulo.setText(event.getName());
            String distancia;
            if (event.getDistance() != -1) {
                if (event.getDistance() >= 1000)
                    distancia = Math.round(event.getDistance() / 1000) + " km";
                else
                    distancia = Math.round(event.getDistance()) + " m";
            } else {
                distancia = formatDate(event.getEventDate());
            }
            txtDistancia.setText(distancia);
            imageManagerService.eventImageDownloadUrl(
                    event.getId(),
                    imageUrl -> {
                        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(itemView.getContext());
                        progressDrawable.setStrokeWidth(7);
                        progressDrawable.setCenterRadius(50);
                        progressDrawable.start();
                        Glide.with(context)
                                .load(imageUrl)
                                .placeholder(progressDrawable)
                                .into(ivFoto);
                    });
            ivFoto.setOnClickListener(v -> {
                ((HomeActivity) context).showEventDetails(event.getId());
            });
            eventManagerService.isUserAdmin(event.getId(),
                    isAdmin -> {
                        if (isAdmin) {
                            btnLike.setImageResource(R.drawable.corazon_pressed);
                        } else {
                            if (event.isFavourite())
                                btnLike.setImageResource(R.drawable.corazon_pressed);
                            else
                                btnLike.setImageResource(R.drawable.corazon_transparente);
                            btnLike.setOnClickListener(v -> {
                                if (!event.isFavourite()) {
                                    btnLike.setImageResource(R.drawable.corazon_pressed);
                                    eventManagerService.setEventAsFavourite(event.getId());
                                    event.setFavourite(true);
                                } else {
                                    btnLike.setImageResource(R.drawable.corazon_transparente);
                                    eventManagerService.deleteFavouriteEvent(event.getId());
                                    event.setFavourite(false);
                                }
                            });
                        }
                    });
        }
    }

    private String formatDate(long time) {
        Date date = new Date(time);
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return dateFormat.format(date);
    }

}
