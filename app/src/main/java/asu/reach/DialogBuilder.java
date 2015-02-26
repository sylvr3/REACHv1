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
    private static Landing landingActivity;
    private static STOP stopActivity;
    private static DailyDiary ddActivity;
    private static boolean end;

    public static DialogBuilder newInstance(String title) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public static DialogBuilder newInstance(String title, Landing a, EditText p) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        landingActivity = a;
        sticActivity = null;
        stopActivity = null;
        ddActivity = null;
        pin = p;
        return frag;
    }

    public static DialogBuilder newInstance(String title, STOP a, boolean e) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        stopActivity = a;
        sticActivity = null;
        landingActivity = null;
        ddActivity = null;
        end = e;
        return frag;
    }

    public static DialogBuilder newInstance(String title, DailyDiary a, boolean e) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        ddActivity = a;
        sticActivity = null;
        stopActivity = null;
        landingActivity = null;
        end = e;
        return frag;
    }

    public static DialogBuilder newInstance(String title, STIC a, EditText p) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        sticActivity = a;
        landingActivity = null;
        stopActivity = null;
        ddActivity = null;
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
        if(landingActivity != null){
            pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setView(pin)
                    .setPositiveButton("Ok", landingActivity)
                    .setNegativeButton("Cancel", landingActivity)
                    .create();
        }
        if(sticActivity != null) {
            pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setView(pin)
                    .setPositiveButton("Ok", sticActivity)
                    .setNegativeButton("Cancel", sticActivity)
                    .create();
        }
        if(stopActivity != null){
            if(end) {
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("Are you all done?")
                        .setPositiveButton("Ok", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            }else{
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("You have unfinished work.\nAre you sure you want to Leave?")
                        .setPositiveButton("Ok", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            }
        }
        if(ddActivity != null){
            if(end) {
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("Are you all done?")
                        .setPositiveButton("Ok", ddActivity)
                        .setNegativeButton("Cancel", ddActivity)
                        .create();
            }else{
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("You have unfinished work.\nAre you sure you want to Leave?")
                        .setPositiveButton("Ok", ddActivity)
                        .setNegativeButton("Cancel", ddActivity)
                        .create();
            }
        }
        return null;
    }
}
