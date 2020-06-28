package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.dataTools.Prescription;
import com.example.myapplication.dataTools.PrescriptionViewModel;

import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private OnItemClickListener listener;
    private List<Prescription> prescriptions;
    private Prescription selected_prescription;

    private Context context;
    private Activity activity;
    private PrescriptionViewModel prescriptionViewModel;
    private FragmentManager manager;

    // Constructor
    public MyAdapter(List<Prescription> prescriptions){
        this.prescriptions = prescriptions;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView tv_title,tv_dateOfCreation,buttonViewOption;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_presc_title);
            tv_dateOfCreation = itemView.findViewById(R.id.tv_presc_date);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null && position!= RecyclerView.NO_POSITION) {
                        listener.onItemClick(prescriptions.get(position));
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),121,0,R.string.menu_rename);
            menu.add(this.getAdapterPosition(),122,1,R.string.menu_share);
            menu.add(this.getAdapterPosition(),123,2,R.string.menu_delete);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        prescriptionViewModel = ViewModelProviders.of((FragmentActivity) activity).get(PrescriptionViewModel.class);
        holder.tv_title.setText(prescriptions.get(position).getTitle());
        holder.tv_dateOfCreation.setText(prescriptions.get(position).getDateOfCreation().toString());
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //will show popup menu here
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                String t1 = activity.getResources().getString(R.string.menu_rename);
                String t2 = activity.getResources().getString(R.string.menu_share);
                String t3 = activity.getResources().getString(R.string.menu_delete);
                popup.getMenu().add(position,131,0,t1);
                popup.getMenu().add(position,132,1,t2);
                popup.getMenu().add(position,133,2,t3);
//                popup.inflate(R.menu.option_menu_prescription);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 131:
                                selected_prescription = prescriptions.get(position);
                                renameBtn(selected_prescription.getTitle());
                                break;
                            case 132:
                                shareBtn(prescriptions.get(position).getOriginalText()+"\n\n"+prescriptions.get(position).getTranslatedText());
                                break;
                            case 133:
                                deleteBtn(position);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        try{
            return prescriptions.size();
        }catch (Exception e){
            Log.e(String.valueOf(e),"Erreur dans la taille de la liste");
            return 0;
        }
    }

    public void shareBtn(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,text);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public void renameBtn(String oldTitle){
        EditDialog dialog = new EditDialog(oldTitle);
        dialog.show(manager,"Edit dialog");
    }

    public void updatePrescription(Prescription prescription){
        prescriptionViewModel.update(prescription);
        Toast.makeText(context,R.string.toast_renamed_prescription,Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    public void deleteBtn(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.deleteDialog_title)
                .setIcon(R.drawable.warning_icon)
                .setMessage(R.string.deleteDialog_message)
                .setNegativeButton(R.string.deleteDialog_negative_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.deleteDialog_positive_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prescriptionViewModel.delete(prescriptions.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(context,R.string.toast_deleted_prescription,Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog quitDialog = builder.create();
        quitDialog.show();
    }

    public void setPrescriptions(List<Prescription> prs){
        this.prescriptions = prs;
        notifyDataSetChanged();
    }

    public void setMyContext(Context context){
        this.context = context;
    }

    public void setActivity(Activity activity){
        this.activity=activity;
    }

    public void setFragmentManager(FragmentManager manager){this.manager = manager;}

    public interface OnItemClickListener{
        void onItemClick(Prescription prescription);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public Prescription getSelectedPrescription(){
        return selected_prescription;
    }

}
