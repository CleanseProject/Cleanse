package com.cleanseproject.cleanse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.AddEventActivity;
import com.cleanseproject.cleanse.adapters.AdaptadorRecyclerViews;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private EventManagerService eventManagerService;
    private SwipeRefreshLayout swipeRefresh;
    private AdaptadorRecyclerViews adaptador;
    private RecyclerView rvEventos;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        rvEventos = view.findViewById(R.id.rv_Eventos);
        progressBar = view.findViewById(R.id.home_fragment_pb);
        swipeRefresh = view.findViewById(R.id.swiperefresh);
        fab = view.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AddEventActivity.class);
                v.getContext().startActivity(i);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //swipeRefresh.setRefreshing(true);

                updateRecycleView();

            }


        });

        LinearLayoutManager llm = new GridLayoutManager(getActivity(), 1);
        rvEventos.setLayoutManager(llm);
        eventManagerService = new EventManagerService();
        eventManagerService.getCloseEvents(new GeoLocation(37.7832, -122.4056),
                10,
                events -> rellenarEventos(events));
    }

    private void updateRecycleView() {
        /**
         * TODO: Conexion con Firebase para updatear la lista de eventos
         */



        swipeRefresh.setRefreshing(false);
    }

    private void rellenarEventos(ArrayList<Event> events) {
        adaptador = new AdaptadorRecyclerViews(events);
        rvEventos.setAdapter(adaptador);
        progressBar.setVisibility(View.GONE);
    }

}
