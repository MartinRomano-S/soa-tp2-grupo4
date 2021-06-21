package com.example.testlogin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * EventsListAdapter class
 * Esta clase se encarga de adaptar el objeto Event
 * en una lista.
 * Solo contiene los métodos necesarios para la clase heredada RecyclerView.Adapter
 */
public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private List<Event> events;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtEventDate;
        private TextView txtEventType;
        private TextView txtEventDescription;

        ViewHolder(View view) {
            super(view);
            txtEventDate = view.findViewById(R.id.txtEventDate);
            txtEventType = view.findViewById(R.id.txtEventType);
            txtEventDescription = view.findViewById(R.id.txtEventDescription);
        }

        TextView getTxtEventDate() { return txtEventDate; }
        TextView getTxtEventType() { return txtEventType; }
        TextView getTxtEventDescription() { return txtEventDescription; }
    }

    /**
     * Inicializa el contenido de nuestra lista
     *
     * @param events ArrayList<Event> contiene la lista de eventos
     */
    public EventsListAdapter(List<Event> events) {
        this.events = events;
    }

    @NotNull
    @Override
    public EventsListAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_item, viewGroup, false);

        return new EventsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventsListAdapter.ViewHolder viewHolder, final int position) {

        final Event event = events.get(position);

        viewHolder.getTxtEventDate().setText(event.getEventDate().toString());
        viewHolder.getTxtEventType().setText(event.getType());
        viewHolder.getTxtEventDescription().setText(event.getDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return events.size();
    }
}
