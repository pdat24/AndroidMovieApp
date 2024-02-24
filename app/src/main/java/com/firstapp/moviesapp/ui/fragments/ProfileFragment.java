package com.firstapp.moviesapp.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.ui.activities.SignInActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnSignOut = view.findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener((v) -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext()).create();
            dialog.setTitle("Sign out");
            dialog.setMessage("Are you sure want to sign out?");
            dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(requireActivity(), SignInActivity.class));
                        requireActivity().finish();
                    }
                });
            dialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }
}