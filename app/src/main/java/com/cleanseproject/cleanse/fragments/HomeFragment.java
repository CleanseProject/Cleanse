package com.cleanseproject.cleanse.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.cleanseproject.cleanse.adapters.EventListAdapter;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class HomeFragment extends Fragment {

    private final int FILTER_CLOSE = 0;
    private final int FILTER_DATE = 1;
    private final int FILTER_FAVOURITE = 2;

    private EventManagerService eventManagerService;
    private LocationService locationService;
    private SwipeRefreshLayout swipeRefresh;
    private EventListAdapter eventListAdapter;
    private RecyclerView rvEventos;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private ArrayList<Event> events;

    private boolean locationEnabled;
    private Location currentLocation;
    private int currentFilter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        eventManagerService = new EventManagerService();
        locationService = new LocationService(getContext());
        rvEventos = view.findViewById(R.id.rv_Eventos);
        progressBar = view.findViewById(R.id.home_fragment_pb);
        swipeRefresh = view.findViewById(R.id.swiperefresh);
        LinearLayoutManager llm = new GridLayoutManager(getActivity(), 1);
        rvEventos.setLayoutManager(llm);
        fab = view.findViewById(R.id.fab_btn);
        fab.setOnClickListener(v -> v.getContext().startActivity(
                new Intent(v.getContext(), AddEventActivity.class)));
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            updateRecycleView();
        });
        locationEnabled = locationService.checkPermission() && locationService.getCurrentLocation() != null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            String filter = bundle.getString("filter");
            if (filter != null && filter.equals("favourites")) {
                currentFilter = FILTER_FAVOURITE;
            }
        } else if (locationEnabled) {
            currentLocation = locationService.getCurrentLocation();
            currentFilter = FILTER_CLOSE;
        } else {
            currentFilter = FILTER_DATE;
        }
        cargarDatos();

    }

    public void cargarDatos() {
        LinearLayoutManager llm = new GridLayoutManager(getActivity(), 1);
        rvEventos.setLayoutManager(llm);
        eventManagerService = new EventManagerService();

        locationService = new LocationService(getContext());
    private void cargarDatos() {
        Log.d("filterType", currentFilter + "");
        events = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getActivity(), events);
        rvEventos.setAdapter(eventListAdapter);
        rvEventos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) fab.hide();
                else fab.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
        eventsRef.keepSynced(true);
        switch (currentFilter) {
            case FILTER_FAVOURITE:
                eventManagerService.getFavouriteEvents(this::rellenarEventos);
                break;
            case FILTER_CLOSE:
                eventManagerService.getCloseEvents(
                        new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        8587,
                        this::rellenarEventos);
                break;
            case FILTER_DATE:
                eventManagerService.getUpcomingEvents(this::rellenarEventos);
                break;
        }
    }

    public void changeFilter() {
        if (locationEnabled) {
            if (currentFilter == FILTER_CLOSE) {
                currentFilter = FILTER_DATE;
            } else if (currentFilter == FILTER_DATE) {
                currentFilter = FILTER_CLOSE;
            }
            cargarDatos();
        }
    }

    public void deleteEvent(String eventId) {
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (event.getId() != null)
                if (event.getId().equals(eventId))
                    iterator.remove();
        }
        eventListAdapter.notifyDataSetChanged();
    }

    private void updateRecycleView() {
        // TODO: Conexion con Firebase para updatear la lista de eventos
        cargarDatos();
        swipeRefresh.setRefreshing(false);
    }

    private void rellenarEventos(Event event) {
        Location location = new Location("");
        location.setLatitude(event.getLatitude());
        location.setLongitude(event.getLongitude());
        if (currentFilter != FILTER_DATE)
            event.setDistance(locationService.distance(location));
        else
            event.setDistance(-1);
        event.setFavourite(false);
        events.add(event);
        Collections.sort(events);
        eventListAdapter.notifyDataSetChanged();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
        eventManagerService.getFavouriteKeys(this::setFavourites);
    }

    private void setFavourites(ArrayList<String> keys) {
        for (String key : keys) {
            for (Event arrayEvent : events) {
                if (key.equals(arrayEvent.getId())) {
                    arrayEvent.setFavourite(true);
                    Log.d("favourite", arrayEvent.getName());
                }
            }
        }
        eventListAdapter.notifyDataSetChanged();
    }

}
