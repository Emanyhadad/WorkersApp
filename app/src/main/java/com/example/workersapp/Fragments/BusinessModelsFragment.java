package com.example.workersapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Activities.DetailsModelsActivity;
import com.example.workersapp.Adapters.ImageModelAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Model;
import com.example.workersapp.databinding.FragmentBusinessModelsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusinessModelsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentBusinessModelsBinding binding;
    private static final String ARG_PARAM1_IMAGE = "image";
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    List<String> imagesList;
    ImageModelAdapter adapter;

    private String documentId;
    String firstImageUrl;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    List<Model> models;

    public BusinessModelsFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static BusinessModelsFragment newInstance(String documentId) {
        BusinessModelsFragment fragment = new BusinessModelsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1_IMAGE, documentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentId = getArguments().getString(ARG_PARAM1_IMAGE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBusinessModelsBinding.inflate(inflater, container, false);
        sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sp.edit();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        imagesList = new ArrayList<>();
        models = new ArrayList<>();
        firebaseFirestore.collection("forms").document( Objects.requireNonNull( firebaseUser.getPhoneNumber( ) ) )
                .collection("userForm")
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()){
                        binding.progressBar.setVisibility(View.GONE);
                    }
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        List<String> images = (List<String>) document.get("images");
                        String doc = (String) document.get("documentId");
                        if (!images.isEmpty()) {
                            models.add(new Model(images, doc));
                            adapter = new ImageModelAdapter(models,
                                    getContext(),
                                    documentId -> {
                                        Intent intent = new Intent(getContext(), DetailsModelsActivity.class);
                                        intent.putExtra("documentId", documentId);
                                        startActivity(intent);
                                    } );
                            RecyclerView fragRv = getActivity().findViewById(R.id.FragRV);
                            fragRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            fragRv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "انتظر", Toast.LENGTH_SHORT).show();
                        }
                    }

                } );
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        models.clear();
        firebaseFirestore.collection("forms").document( Objects.requireNonNull( firebaseUser.getPhoneNumber( ) ) )
                .collection("userForm")
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()){
                        binding.progressBar.setVisibility(View.GONE);
                    }
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        List<String> images = (List<String>) document.get("images");
                        String doc = (String) document.get("documentId");
                        if (!images.isEmpty()) {
                            models.add(new Model(images, doc));
                            adapter = new ImageModelAdapter(models,
                                    getContext(),
                                    documentId -> {
                                        Intent intent = new Intent(getContext(), DetailsModelsActivity.class);
                                        intent.putExtra("documentId", documentId);
                                        startActivity(intent);
                                    } );
                            RecyclerView fragRv = getActivity().findViewById(R.id.FragRV);
                            fragRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            fragRv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "انتظر", Toast.LENGTH_SHORT).show();
                        }
                    }

                } );

    }
}