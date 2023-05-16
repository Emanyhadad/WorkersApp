package com.example.workersapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.databinding.FragmentWorkInProgressBinding;


public class WorkInProgressFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public WorkInProgressFragment() {
        // Required empty public constructor
    }


    public static WorkInProgressFragment newInstance(String param1, String param2) {
        WorkInProgressFragment fragment = new WorkInProgressFragment();
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
        FragmentWorkInProgressBinding binding = FragmentWorkInProgressBinding.inflate(inflater, container, false);
        WorkInProgressAdapter workInProgressAdapter = new WorkInProgressAdapter();
        binding.rcWorkInProgress.setAdapter(workInProgressAdapter);
        binding.rcWorkInProgress.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        return binding.getRoot();
    }
}