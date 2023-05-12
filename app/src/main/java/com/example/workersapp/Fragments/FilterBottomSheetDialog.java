package com.example.workersapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.workersapp.R;
import com.example.workersapp.databinding.BottomSheetPostfilterBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private FilterListener filterListener;



    public interface FilterListener {
        void onFilterSelected(String status);
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_postfilter, container, false);

        BottomSheetPostfilterBinding binding= BottomSheetPostfilterBinding.inflate( getLayoutInflater() );


        binding.btnApplyFilter.setOnClickListener( v -> {
            boolean openPostsChecked = binding.openPostsCheckbox.isChecked();
            boolean closedPostsChecked = binding.closedPostsCheckbox.isChecked();

            // Do something with the checked values
        } );

        return binding.getRoot();
    }
}
