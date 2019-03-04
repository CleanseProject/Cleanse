package com.cleanseproject.cleanse.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class UsersInEventAdapter extends RecyclerView.Adapter<UsersInEventAdapter.Holder> {

    private ArrayList<User> listaUsuarios;
    private Context context;

    public UsersInEventAdapter(ArrayList<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UsersInEventAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_photo, null, false);
        return new UsersInEventAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(listaUsuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {

        private CircularImageView ivUser;
        private TextView lblUsername;

        private Context context;

        public Holder(View view) {
            super(view);
            ivUser = view.findViewById(R.id.ivUser);
            lblUsername = view.findViewById(R.id.event_user_name);
            context = view.getContext();
        }

        public void bind(User user) {
            // TODO: Cambiar por la foto de User en Firebase
            lblUsername.setText(user.getName());
            new ImageManagerService().userImageDownloadUrl(user.getUserId(), url ->
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.centerCropTransform().circleCropTransform())
                            .into(ivUser));
        }
    }
}
