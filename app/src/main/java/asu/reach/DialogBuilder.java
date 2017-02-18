package asu.reach;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

public class DialogBuilder extends DialogFragment{

    private static EditText pin;
    private static STIC sticActivity;
    private static Landing landingActivity;
    private static STOP stopActivity;
    private static DailyDiary ddActivity;
    private static WorryHeads whActivity;
    private static Safe safeActivity;
    private static boolean end,date;
    private CheckBox checkBox;

    public static final String PREFS_NAME = "DoNotShowAgain";


    private void doNotShowAgain() {
        // persist shared preference to prevent dialog from showing again.
        Log.d("MainActivity", "TODO: Persist shared preferences.");
    }

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
        safeActivity = null;        //Safe
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
        safeActivity = null;        //Safe
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
        safeActivity = null;        //Safe
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
        safeActivity = null;        //Safe
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
        safeActivity = null;        //Safe
        return frag;
    }

    //Safe
    public static DialogBuilder newInstance(String title, Safe a) {
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
        AlertDialog.Builder adb = new AlertDialog.Builder(safeActivity);
        LayoutInflater inflater = LayoutInflater.from(safeActivity);
        View neverShow = inflater.inflate(R.layout.checkbox, null);
        checkBox = (CheckBox) neverShow.findViewById(R.id.checkbox);
        if(safeActivity != null){
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setMessage("Are you sure you want to Leave?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String checkBoxResult = "NOT checked";
                            if (checkBox.isChecked())
                                checkBoxResult = "checked";
                            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("skipMessage", checkBoxResult);
                            editor.commit();
                        }
                    })
                            .setNegativeButton("Cancel", safeActivity)
                            .create();
        }

        return null;
    }
}
