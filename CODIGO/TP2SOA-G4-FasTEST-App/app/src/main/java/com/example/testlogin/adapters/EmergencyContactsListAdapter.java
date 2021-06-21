package com.example.testlogin.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.models.EmergencyContact;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * EmergencyContactsListAdapter class
 * Esta clase se encarga de adaptar el objeto EmergencyContact
 * en una lista.
 * Solo contiene los métodos necesarios para la clase heredada RecyclerView.Adapter
 */
public class EmergencyContactsListAdapter extends RecyclerView.Adapter<EmergencyContactsListAdapter.ViewHolder> {
    private List<EmergencyContact> emergencyContacts;
    private Activity callerActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtECNameItem;
        private TextView txtECNumberItem;
        private TextView txtECID;
        private ImageView imgDelete;

        ViewHolder(View view) {
            super(view);
            txtECNameItem = view.findViewById(R.id.txtECNameItem);
            txtECNumberItem = view.findViewById(R.id.txtECNumberItem);
            txtECID = view.findViewById(R.id.txtECID);
            imgDelete = view.findViewById(R.id.imgDelete);
        }

        TextView getTxtECNameItemView() {
            return txtECNameItem;
        }
        TextView getTxtECNumberItemView() {
            return txtECNumberItem;
        }
        TextView getTxtECIDView() {
            return txtECID;
        }
        ImageView getImgDelete() {
            return imgDelete;
        }
    }

    /**
     * Inicializa el contenido de nuestra lista
     *
     * @param emergencyContacts ArrayList<EmergencyContact> contiene la lista de contactos de emergencias
     */
    public EmergencyContactsListAdapter(List<EmergencyContact> emergencyContacts, Activity callerActivity) {
        this.emergencyContacts = emergencyContacts;
        this.callerActivity = callerActivity;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.emergency_contact_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final EmergencyContact emergencyContact = emergencyContacts.get(position);

        final int contactID = emergencyContact.getId();
        viewHolder.getTxtECIDView().setText(String.valueOf(contactID));
        viewHolder.getTxtECNameItemView().setText(emergencyContact.getName());
        viewHolder.getTxtECNumberItemView().setText(String.valueOf(emergencyContact.getPhoneNumber()));

         //También permitimos el borrado de los contactos por medio de este OnClickListener
        viewHolder.getImgDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emergencyContacts.remove(emergencyContact);
                SharedPreferencesManager spm = SharedPreferencesManager.getInstance(callerActivity);
                spm.saveEmergencyContactList(emergencyContacts);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencyContacts.size();
    }
}
