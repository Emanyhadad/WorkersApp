package com.example.workersapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Activities.DetailsModelsActivity;
import com.example.workersapp.Adapters.ImageModelAdapter;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.R;
import com.example.workersapp.databinding.FragmentBusinessModelsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BusinessModelsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
//    String documentId;

    public BusinessModelsFragment() {
        // Required empty public constructor
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
            Toast.makeText(getContext(),"frag: "+documentId , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentBusinessModelsBinding binding = FragmentBusinessModelsBinding.inflate(inflater, container, false);
        sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sp.edit();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        imagesList = new ArrayList<>();
//        documentId = sp.getString("documentId","null");

        // TODO: تاريخ الانضمام
//        firebaseUser.getMetadata().getCreationTimestamp();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        imagesList.clear();
        firebaseFirestore.collection("forms").document(firebaseUser.getPhoneNumber())
                .collection("userForm")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            List<String> images = (List<String>) document.get("images");
                            if (!images.isEmpty()) {
                                firstImageUrl = images.get(0); // الحصول على الصورة الأولى
                                Log.d("imageUrlBusniessModels:",firstImageUrl);
                                imagesList.add(firstImageUrl);
                                adapter = new ImageModelAdapter(imagesList, getContext(), new clickListener() {
                                    @Override
                                    public void click(int pos) {
                                        Toast.makeText(getContext(), "position"+pos, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), DetailsModelsActivity.class);
                                        intent.putExtra("pos",pos);
                                        intent.putExtra("documentId",document.getId());
                                        startActivity(intent);
                                    }
                                });
                                RecyclerView fragRv = getActivity().findViewById(R.id.FragRV);
                                fragRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                                fragRv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(getContext(), "wait", Toast.LENGTH_SHORT).show();
                            }
//                            for (String imageUrl : images) {
////                                Picasso.get().load(imageUrl).into(imageView);
//                                Toast.makeText(getContext(), "imageUrl: "+imageUrl, Toast.LENGTH_SHORT).show();
//                                Log.d("imageUrlBusniessModels:",imageUrl);
////                                Toast.makeText(getContext(), "imageDone", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                });
    }
}