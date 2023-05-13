package com.example.workersapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.workersapp.Activities.LoginActivity;
import com.example.workersapp.R;
import com.example.workersapp.databinding.BottomSheetPostfilterBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private FilterListener filterListener;
    public static boolean closedPostsChecked;
    public static boolean   openPostsChecked;


    public interface FilterListener {
        void onFilterSelected(String status);
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetPostfilterBinding binding= BottomSheetPostfilterBinding.inflate(inflater, container, false);

        binding.btnApplyFilter.setOnClickListener( v -> {
             openPostsChecked = binding.openPostsCheckbox.isChecked();
             closedPostsChecked = binding.closedPostsCheckbox.isChecked();
            SharedPreferences.Editor editor = LoginActivity.sharedPreferences.edit();
            if ( LoginActivity.sharedPreferences.getString( "jobStatesOpen","open" ).equals( "open" )  ){
                binding.openPostsCheckbox.setChecked( true );
                binding.openPostsCheckbox.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_bg));
                binding.openPostsCheckbox.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray1));

            }
            if (LoginActivity.sharedPreferences.getString( "jobStatesClose","close" ).equals( "close" )  ){
                FilterBottomSheetDialog.closedPostsChecked=true;
                binding.closedPostsCheckbox.setChecked( true );
                binding.closedPostsCheckbox.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_bg));
                binding.closedPostsCheckbox.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray1));


            }

            if (openPostsChecked && closedPostsChecked) {
                // both open and closed posts are selected, so no filtering needed
                dismiss();
            } else {
                List <String> jobStates = new ArrayList <>();
                if (openPostsChecked) {
                    jobStates.add("open");
                    binding.openPostsCheckbox.setChecked( true );
                    binding.openPostsCheckbox.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_bg));
                    binding.openPostsCheckbox.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray1));

                    editor.putString("jobStatesOpen", "open");
                }else if (closedPostsChecked) {
                    jobStates.add("close");
                    editor.putString("jobStatesClose", "close");
                    binding.closedPostsCheckbox.setChecked( true );
                    binding.closedPostsCheckbox.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.et_bg));
                    binding.closedPostsCheckbox.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray1));

                }
                editor.apply();

                // send the selected filter values back to the parent fragment
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof PostsFragment) {
                    ((PostsFragment) parentFragment).applyFilter(jobStates);
                }
                dismiss();
            }
        });

        return binding.getRoot();
    }
}
