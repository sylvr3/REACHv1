package asu.reach;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;

public class DialogBuilder extends DialogFragment{

    private static EditText pin;
    private static STIC sticActivity;
    private static Landing landingActivity;
    private static STOP stopActivity;
    private static DailyDiary ddActivity;
    private static WorryHeads whActivity;
    private static SAFE safeActivity;
    private static boolean end,date;

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
        whActivity = null;
        safeActivity = null;        //SAFE
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
        whActivity = null;
        safeActivity = null;        //SAFE
        end = e;
        return frag;
    }

    public static DialogBuilder newInstance(String title, DailyDiary a, boolean e, boolean d) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        ddActivity = a;
        sticActivity = null;
        stopActivity = null;
        landingActivity = null;
        whActivity = null;
        safeActivity = null;        //SAFE
        end = e;
        date = d;
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
        whActivity = null;
        safeActivity = null;        //SAFE
        pin = p;
        return frag;
    }
    public static DialogBuilder newInstance(String title, WorryHeads a) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        whActivity = a;
        sticActivity = null;
        landingActivity = null;
        stopActivity = null;
        ddActivity = null;
        safeActivity = null;        //SAFE
        return frag;
    }

    //SAFE
    public static DialogBuilder newInstance(String title, SAFE a) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        safeActivity = a;
        sticActivity = null;
        whActivity = null;
        landingActivity = null;
        stopActivity = null;
        ddActivity = null;
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
                        .setPositiveButton("Yes", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            }else{
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("You have unfinished work.\nAre you sure you want to Leave?")
                        .setPositiveButton("Yes", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            }
        }
        if(ddActivity != null){
            if(!date) {
                if (end) {
                    return new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_launcher)
                            .setTitle(title)
                            .setMessage("Are you all done?")
                            .setPositiveButton("Yes", ddActivity)
                            .setNegativeButton("Cancel", ddActivity)
                            .create();
                } else {
                    return new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_launcher)
                            .setTitle(title)
                            .setMessage("You have unfinished work.\nAre you sure you want to Leave?")
                            .setPositiveButton("Yes", ddActivity)
                            .setNegativeButton("Cancel", ddActivity)
                            .create();
                }
            }else {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                return new DatePickerDialog(ddActivity, ddActivity, year, month, day);
            }
        }
        if(whActivity != null){
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setMessage("Are you sure you want to Leave?")
                    .setPositiveButton("Yes", whActivity)
                    .setNegativeButton("Cancel", whActivity)
                    .create();
        }
        if(safeActivity != null){
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setMessage("Are you sure you want to Leave?")
                    .setPositiveButton("Yes", safeActivity)
                    .setNegativeButton("Cancel", safeActivity)
                    .create();
        }
        return null;
    }
}
