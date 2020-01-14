package com.mooc.ppjoke.ui.sofa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.mooc.ppjoke.R;

import us.bojie.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/sofa")
public class SofaFragment extends Fragment {
    private static final String TAG = "SofaFragment";

    private SofaViewModel mSofaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mSofaViewModel =
                ViewModelProviders.of(this).get(SofaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sofa, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        mSofaViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}