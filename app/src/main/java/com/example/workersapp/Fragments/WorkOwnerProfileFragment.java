package com.example.workersapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workersapp.Adapters.FinishedJobsAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.FragmentWorkOwnerProfileBinding;

public class WorkOwnerProfileFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public WorkOwnerProfileFragment() {
        // Required empty public constructor
    }


    public static WorkOwnerProfileFragment newInstance(String param1, String param2) {
        WorkOwnerProfileFragment fragment = new WorkOwnerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentWorkOwnerProfileBinding binding = FragmentWorkOwnerProfileBinding.inflate(inflater, container, false);

        FinishedJobsAdapter finishedJobsAdapter = new FinishedJobsAdapter();
         binding.rcFinishedJobs.setAdapter(finishedJobsAdapter);
         binding.rcFinishedJobs.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));

        return binding.getRoot();
    }
}