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

public class LanguageDialog extends AppCompatDialogFragment {

    private LanguageDialogListener listener;
    private int choosed_language;

    public LanguageDialog(int choosed_language) {
        this.choosed_language = choosed_language;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] languages = getResources().getStringArray(R.array.language_choice);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.languageDialog_title)
                .setSingleChoiceItems(languages, choosed_language, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choosed_language = which;
                        listener.onSelectedLanguage(languages,choosed_language);
                        dismiss();
                    }
                });

        return builder.create();
    }

    public interface LanguageDialogListener{
        void onSelectedLanguage(String[] languages,int choosed_language);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (LanguageDialogListener) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString()+" must implement EditDialogListener!");
        }
    }

}
