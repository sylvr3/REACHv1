package asu.reach;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DialogBuilder extends DialogFragment{

    private static EditText pin;
    private static STIC sticActivity;

    public static DialogBuilder newInstance(String title) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public static DialogBuilder newInstance(String title, STIC a, EditText p) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        sticActivity = a;
        pin = p;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        pin.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                .setMessage("Please enter a PIN")
                .setView(pin)
                .setPositiveButton("Ok", sticActivity)
                .setNegativeButton("Cancel", sticActivity)
                .create();
    }
}
