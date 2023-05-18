package com.example.workersapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.workersapp.R;
import com.example.workersapp.databinding.DialogSliderImgBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SliderImgDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderImgDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SliderImgDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SliderImgDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static SliderImgDialog newInstance(String param1, String param2) {
        SliderImgDialog fragment = new SliderImgDialog();
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
        // Inflate the layout for this fragment
        DialogSliderImgBinding binding = DialogSliderImgBinding.inflate(inflater, container, false);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        // TODO: اضافة الصور من شاشة تفاصيل المشروع
        SlideModel slideModel1 = new SlideModel(R.drawable.user, ScaleTypes.FIT);
        SlideModel slideModel2 = new SlideModel(R.drawable.empty, ScaleTypes.FIT);
        SlideModel slideModel3 = new SlideModel(R.drawable.worker, ScaleTypes.FIT);
        SlideModel slideModel4 = new SlideModel(R.drawable.delete_image, ScaleTypes.FIT);
        slideModels.add(slideModel1);
        slideModels.add(slideModel2);
        slideModels.add(slideModel3);
        slideModels.add(slideModel4);

        binding.dialogSliderImg.setImageList(slideModels, ScaleTypes.CENTER_CROP);

        binding.dialogNumImg.setText((1) + "/" + (slideModels.size()));

        binding.dialogSliderImg.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                binding.dialogNumImg.setText((i + 1) + "/" + (slideModels.size()));
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}