package com.cleanseproject.cleanse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanseproject.cleanse.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class HomeFragment extends Fragment {

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

}
