package com.cleanseproject.cleanse.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class UsersInEventAdapter extends RecyclerView.Adapter<UsersInEventAdapter.Holder> {
    private ArrayList<User> listaUsuarios;
    private Context context;

    public UsersInEventAdapter(ArrayList<User>listaUsuarios){
        this.listaUsuarios=listaUsuarios;
    }

    @NonNull
    @Override
    public UsersInEventAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_photo,null,false);


        return new UsersInEventAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.setPhotos();
    }



    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {

        private CircularImageView ivUser;

        private Context context;

        public Holder(View v) {
            super(v);
            ivUser=v.findViewById(R.id.ivUser);

            context=v.getContext();
        }




        public void setPhotos() {
            /**
             * TODO: Cambiar por la foto de User en Firebase
             */
            ivUser.setImageResource(R.drawable.imagen);
        }
    }
}
