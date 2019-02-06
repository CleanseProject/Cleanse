package com.cleanseproject.cleanse.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.AdaptadorRecyclerViews;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private AdaptadorRecyclerViews adaptador;
    private RecyclerView rvEventos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        rvEventos = view.findViewById(R.id.rv_Eventos);

        ArrayList<Event>listaEventos=rellenarEventos();
        adaptador= new AdaptadorRecyclerViews(listaEventos);


        LinearLayoutManager llm = new GridLayoutManager(getActivity(),1);
        rvEventos.setLayoutManager(llm);

        rvEventos.setAdapter(adaptador);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MyFirebaseMsgService", "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = task.getResult().getToken();
                    Log.d("FCMToken", token);
                });
    }

    private ArrayList<Event> rellenarEventos() {
        ArrayList<Event>listaEventos=new ArrayList<>();


        listaEventos.add(new Event("Carlos","","https://static3lonelyplanetes.cdnstatics.com/sites/default/files/styles/max_1300x1300/public/blog/PolinesiaFrancesa_playaRangiroa_GettyRF_524891702_Vpommeyrol_Getty%20Images_0.jpg?itok=RxeSU30C","",""));
        listaEventos.add(new Event("Adrián","","https://static3lonelyplanetes.cdnstatics.com/sites/default/files/styles/max_1300x1300/public/blog/PolinesiaFrancesa_playaRangiroa_GettyRF_524891702_Vpommeyrol_Getty%20Images_0.jpg?itok=RxeSU30C","",""));

        return  listaEventos;
    }

}
