package utn.frgp.edu.ar.carpooling;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerViajeFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public static DatePickerViajeFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerViajeFragment fragment = new DatePickerViajeFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), listener, year, month, day);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR,+1);
        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        return dpDialog;
    }
}
