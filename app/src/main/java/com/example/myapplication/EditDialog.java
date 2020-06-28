package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditDialog extends AppCompatDialogFragment {

    private EditDialogListener listener;
    private EditText edt_new_title;
    private String edt_new_title_hint;

    public EditDialog(String oldTitle) {
        edt_new_title_hint = oldTitle;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_dialog_layout,null);
        builder.setView(view)
                .setIcon(R.drawable.ic_edit_gray_24dp)
                .setTitle(R.string.editDialog_title)
                .setNegativeButton(R.string.editDialog_negative_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.editDialog_positive_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_title = edt_new_title.getText().toString();
                        listener.onConfirmClicked(new_title);
                    }
                });
        edt_new_title = view.findViewById(R.id.edt_new_title);
        edt_new_title.setText(edt_new_title_hint);
        return builder.create();
    }

    public interface EditDialogListener{
        void onConfirmClicked(String new_title);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (EditDialogListener) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString()+" must implement EditDialogListener!");
        }
    }
}
