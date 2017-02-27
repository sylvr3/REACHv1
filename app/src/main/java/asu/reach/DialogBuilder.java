package asu.reach;


import android.app.Activity;
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

public class DialogBuilder extends DialogFragment {

    private static EditText pin;
    private static STIC sticActivity;
    private static Landing landingActivity;
    private static STOP stopActivity;
    private static DailyDiary ddActivity;
    private static WorryHeads whActivity;
    private static Safe safeActivity;
    private static SafeWebView safeWebViewActivity;
    private static boolean end, date;

    public static final String PREFS_NAME = "DoNotShowAgain";


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
        ddActivity = null;
        return frag;
    }

    public static DialogBuilder newInstance(String title, SafeWebView a) {
        DialogBuilder frag = new DialogBuilder();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        safeWebViewActivity = a;
        safeActivity = null;
        sticActivity = null;
        whActivity = null;
        landingActivity = null;
        stopActivity = null;
        ddActivity = null;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() == null)
            setShowsDialog(false);
        else
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        if (landingActivity != null) {
            pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setView(pin)
                    .setPositiveButton("Ok", landingActivity)
                    .setNegativeButton("Cancel", landingActivity)
                    .create();
        }
        if (sticActivity != null) {
            pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setView(pin)
                    .setPositiveButton("Ok", sticActivity)
                    .setNegativeButton("Cancel", sticActivity)
                    .create();
        }
        if (stopActivity != null) {
            if (end) {
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("Are you all done?")
                        .setPositiveButton("Yes", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            } else {
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(title)
                        .setMessage("You have unfinished work.\nAre you sure you want to Leave?")
                        .setPositiveButton("Yes", stopActivity)
                        .setNegativeButton("Cancel", stopActivity)
                        .create();
            }
        }
        if (ddActivity != null) {
            if (!date) {
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
            } else {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                return new DatePickerDialog(ddActivity, ddActivity, year, month, day);
            }
        }
        if (whActivity != null) {
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setMessage("Are you sure you want to Leave?")
                    .setPositiveButton("Yes", whActivity)
                    .setNegativeButton("Cancel", whActivity)
                    .create();
        }

        final SharedPreferences settings = safeActivity.getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        boolean doNotShowDialog = settings.getBoolean("skipMessage", false);
        View checkBoxView = View.inflate(safeActivity, R.layout.checkbox, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setText("Do not ask me again");
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("skipMessage", checkBox.isChecked());
                    editor.commit();

                }
            }
        });
        if (!doNotShowDialog && safeActivity != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(safeActivity);
            builder.setTitle(title);
            return builder.setMessage("Are you sure you want to Leave?")
                    .setView(checkBoxView)
                    .setPositiveButton("Yes", safeActivity)
                    .setNegativeButton("No", safeActivity)
                    .create();
        }

        Intent intent = new Intent(getContext(), Landing.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);

        return null;
    }
}

