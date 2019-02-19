package com.cleanseproject.cleanse.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.AddEventActivity;
import com.cleanseproject.cleanse.adapters.AdaptadorRecyclerViews;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private EventManagerService eventManagerService;
    private LocationService locationService;

    private SwipeRefreshLayout swipeRefresh;
    private AdaptadorRecyclerViews adaptador;
    private RecyclerView rvEventos;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    private ArrayList<Event> events;

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
        fab.setOnClickListener(v -> v.getContext().startActivity(
                new Intent(v.getContext(), AddEventActivity.class)));
        swipeRefresh.setOnRefreshListener(() -> {
            //swipeRefresh.setRefreshing(true);
            updateRecycleView();
        });

        LinearLayoutManager llm = new GridLayoutManager(getActivity(), 1);
        rvEventos.setLayoutManager(llm);
        eventManagerService = new EventManagerService();
        locationService = new LocationService(getContext());
        events = new ArrayList<>();
        adaptador = new AdaptadorRecyclerViews(events);
        rvEventos.setAdapter(adaptador);
        Location currentLocation = locationService.getCurrentLocation();
        eventManagerService.getCloseEvents(
                new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()),
                10,
                this::rellenarEventos);
    }

    private void updateRecycleView() {
        // TODO: Conexion con Firebase para updatear la lista de eventos
        swipeRefresh.setRefreshing(false);
    }

    private void rellenarEventos(Event event) {
        events.add(event);
        adaptador.notifyDataSetChanged();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

}
