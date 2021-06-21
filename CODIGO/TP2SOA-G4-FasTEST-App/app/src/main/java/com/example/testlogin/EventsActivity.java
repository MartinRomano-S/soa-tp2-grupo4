package com.example.testlogin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.adapters.EventsListAdapter;
import com.example.testlogin.models.Event;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pantalla para la visualización de la lista de eventos
 */
public class EventsActivity extends AppCompatActivity {

    RecyclerView listEvents;
    List<Event> events;
    SharedPreferencesManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        listEvents = findViewById(R.id.listEvents);
        spm = SharedPreferencesManager.getInstance(EventsActivity.this);

        listEvents.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.divider)));
        listEvents.addItemDecoration(divider);

        try {
            events = spm.getEventList();
        } catch (JSONException e) {
            events = new ArrayList<>();
        }

        EventsListAdapter evAdapter = new EventsListAdapter(events);
        listEvents.setAdapter(evAdapter);
    }
}
