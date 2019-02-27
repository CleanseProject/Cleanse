package com.cleanseproject.cleanse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.callbacks.UserNameLoadCallback;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.fragments.ChatListFragment;
import com.cleanseproject.cleanse.fragments.HomeFragment;
import com.cleanseproject.cleanse.fragments.MapFragment;
import com.cleanseproject.cleanse.services.CleanseFirebaseMessagingService;
import com.cleanseproject.cleanse.services.NotificationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

public class HomeActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private DrawerLayout drawerLayout;
    private CircularImageView imagenUsuario;
    private TextView nombreUsuario;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter(CleanseFirebaseMessagingService.NOTIFICATION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onEvent, f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_home);
        context = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        notificationManager = new NotificationManager(findViewById(R.id.homeCoordinatorLayout));
        initializeUI();
    }

    @Override
    public void onBackPressed() {

    }

    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            notificationManager.showNotification(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.user_menu:
                logOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    private void initializeUI() {
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        imagenUsuario = headerLayout.findViewById(R.id.nav_header_imagen);
        nombreUsuario = headerLayout.findViewById(R.id.nav_header_usuario);
        imagenUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("userId", mAuth.getCurrentUser().getUid());
            startActivity(intent);
        });
        getNombreUsuario(mAuth.getCurrentUser().getUid(),
                username -> nombreUsuario.setText(username));
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    transaction.replace(R.id.content_frame, new HomeFragment());
                    break;
                case R.id.nav_favourites:
                    Bundle bundle = new Bundle();
                    HomeFragment homeFragment = new HomeFragment();
                    bundle.putString("filter", "favourites");
                    homeFragment.setArguments(bundle);
                    transaction.replace(R.id.content_frame, homeFragment);
                    break;
                case R.id.nav_chats:
                    transaction.replace(R.id.content_frame, new ChatListFragment());
                    break;
                case R.id.nav_map:
                    transaction.replace(R.id.content_frame, new MapFragment());
            }
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        });
        Intent intent = getIntent();
        String fragment = intent.getStringExtra("fragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null && fragment.equals("chats")) {
            transaction.replace(R.id.content_frame, new ChatListFragment());
            navigationView.getMenu().getItem(3).setChecked(true);
        } else {
            transaction.replace(R.id.content_frame, new HomeFragment());
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getNombreUsuario(String userId, UserNameLoadCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onUsernameLoaded(user.getName() + " " + user.getSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onEvent);
        super.onPause();
    }

}
