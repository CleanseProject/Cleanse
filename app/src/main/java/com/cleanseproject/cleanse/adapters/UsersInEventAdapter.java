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
import com.cleanseproject.cleanse.activities.EventDetailsActivity;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class UsersInEventAdapter extends RecyclerView.Adapter<UsersInEventAdapter.Holder> {

    private final ArrayList<User> listaUsuarios;

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

        private final CircularImageView ivUser;
        private final TextView lblUsername;

        private final Context context;

        Holder(View view) {
            super(view);
            ivUser = view.findViewById(R.id.ivUser);
            lblUsername = view.findViewById(R.id.event_user_name);
            context = view.getContext();
        }

        void bind(User user) {
            lblUsername.setText(user.getName());
            if (!user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                ivUser.setOnClickListener(v -> ((EventDetailsActivity) context).privateChat(user));
            new ImageManagerService().userImageDownloadUrl(user.getUserId(), url ->
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivUser));
        }
    }
}
