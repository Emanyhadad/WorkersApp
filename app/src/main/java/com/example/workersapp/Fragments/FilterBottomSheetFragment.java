package com.example.workersapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.workersapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    // Declare interface to communicate with parent activity
    public interface FilterListener {
        void onFilterApplied(boolean showOpenPosts, boolean showClosedPosts);
    }

    private FilterListener listener;

    private SwitchCompat switchOpenPosts;
    private SwitchCompat switchClosedPosts;
    private Button btnApplyFilter;

    // Required empty constructor
    public FilterBottomSheetFragment() {}

    // Override onAttach method to get the parent activity context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Check if parent activity implements FilterListener interface
        if (context instanceof FilterListener) {
            listener = (FilterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FilterListener");
        }
    }

    // Override onCreateView method to inflate the bottom sheet layout
    @SuppressLint( "MissingInflatedId" )
    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.bottom_sheet_postfilter, container, false);

        // Get references to UI components
        switchOpenPosts = view.findViewById(R.id.open_posts_radio_button);
        switchClosedPosts = view.findViewById(R.id.closed_posts_radio_button);
        btnApplyFilter = view.findViewById(R.id.btn_apply_filter);

        // Set click listener on apply filter button
        btnApplyFilter.setOnClickListener( v -> {
            boolean showOpenPosts = switchOpenPosts.isChecked();
            boolean showClosedPosts = switchClosedPosts.isChecked();
            listener.onFilterApplied(showOpenPosts, showClosedPosts);
            dismiss();
        } );

        return view;
    }
}
