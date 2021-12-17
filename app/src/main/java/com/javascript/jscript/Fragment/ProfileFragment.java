package com.javascript.jscript.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.javascript.jscript.Activities.EditProfileActivity;
import com.javascript.jscript.Activities.GoogleSignInActivity;
import com.javascript.jscript.Activities.PremiumActivity;
import com.javascript.jscript.Config.UiConfig;
import com.javascript.jscript.Model.ProfileModel;
import com.javascript.jscript.Model.UserModel;
import com.javascript.jscript.R;
import com.javascript.jscript.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog dialog;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(getContext(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        //dialog
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Image Uploading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        //pro status
        if (UiConfig.PRO_VISIBILITY_STATUS_SHOW){
            binding.proBadge.setVisibility(View.GONE);
        }else {
            binding.proBadge.setVisibility(View.VISIBLE);
        }
        //promotion visibility
        if (UiConfig.PRO_VISIBILITY_STATUS_SHOW) {
            binding.promotion.setVisibility(View.VISIBLE);
        } else {
            binding.promotion.setVisibility(View.GONE);
        }

        /*check if user is sign in or sign out*/
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getActivity(), GoogleSignInActivity.class);
            startActivity(intent);
        } else {
            //google sign in data fetch with image
            database.getReference().child("UserData").child(Objects.requireNonNull(auth.getUid()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UserModel user = snapshot.getValue(UserModel.class);
                                assert user != null;
                                Picasso.get()
                                        .load(user.getProfile())
                                        .placeholder(R.drawable.ic_profile_default_image)
                                        .into(binding.profileImage);
                                binding.userName.setText(user.getUserName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            //cover and profile update images
            database.getReference().child("UserImages").child(Objects.requireNonNull(auth.getUid()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel user = snapshot.getValue(UserModel.class);
                                assert user != null;
                                Picasso.get()
                                        .load(user.getCoverPhoto())
                                        .placeholder(R.drawable.ic_placeholder_dark)
                                        .into(binding.coverPhoto);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            //other information data fetch
            //update profile data fetch
            database.getReference().child("UpdateProfile")
                    .child(Objects.requireNonNull(auth.getUid()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ProfileModel profile = snapshot.getValue(ProfileModel.class);
                                assert profile != null;
                                String profession = profile.getProfession();
                                String bio = profile.getUserBio();
                                String fb = profile.getFbLink();
                                String insta = profile.getInstaLink();
                                String github = profile.getGithubLink();
                                String linkedin = profile.getLinkedinLink();
                                String twitter = profile.getTwitterLink();

                                //set profession and bio
                                binding.profession.setText(profession);
                                binding.userBioText.setText(bio);
                                //insert link data
                                binding.linkFacebook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (fb.isEmpty()) {
                                            Toast.makeText(getActivity(), "Please update your profile first.", Toast.LENGTH_SHORT).show();
                                        } else if (fb.startsWith("https://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fb)));
                                        } else if (fb.startsWith("http://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fb)));
                                        } else {
                                            Toast.makeText(getActivity(), "Please insert valid input in your update profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });//facebook
                                binding.linkInstagram.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (insta.isEmpty()) {
                                            Toast.makeText(getActivity(), "Please update your profile first.", Toast.LENGTH_SHORT).show();
                                        } else if (insta.startsWith("https://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(insta)));
                                        } else if (insta.startsWith("http://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(insta)));
                                        } else {
                                            Toast.makeText(getActivity(), "Please insert valid input in your update profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });//instagram
                                binding.linkGithub.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (github.isEmpty()) {
                                            Toast.makeText(getActivity(), "Please update your profile first.", Toast.LENGTH_SHORT).show();
                                        } else if (github.startsWith("https://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github)));
                                        } else if (github.startsWith("http://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github)));
                                        } else {
                                            Toast.makeText(getActivity(), "Please insert valid input in your update profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });//github
                                binding.linkLinkedIn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (linkedin.isEmpty()) {
                                            Toast.makeText(getActivity(), "Please update your profile first.", Toast.LENGTH_SHORT).show();
                                        } else if (linkedin.startsWith("https://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin)));
                                        } else if (linkedin.startsWith("http://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin)));
                                        } else {
                                            Toast.makeText(getActivity(), "Please insert valid input in your update profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });//linkedin
                                binding.linkTwitter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (twitter.isEmpty()) {
                                            Toast.makeText(getActivity(), "Please update your profile first.", Toast.LENGTH_SHORT).show();
                                        } else if (twitter.startsWith("https://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter)));
                                        } else if (twitter.startsWith("http://")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter)));
                                        } else {
                                            Toast.makeText(getActivity(), "Please insert valid input.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });//twitter


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


        //upload cover
        binding.uploadCoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);

            }
        });
        //upload profile image
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 22);
            }
        });
        //edit profile
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        //promo start button
        binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PremiumActivity.class);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }//end onCreate

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            assert data != null;
            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.coverPhoto.setImageURI(uri);
                dialog.show();
                final StorageReference reference = storage.getReference().child("cover_photo")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                database.getReference()
                                        .child("UserImages")
                                        .child(Objects.requireNonNull(auth.getUid()))
                                        .child("coverPhoto")
                                        .setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        } else if (requestCode == 22) {
            assert data != null;
            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.profileImage.setImageURI(uri);
                dialog.show();
                final StorageReference reference = storage.getReference().child("profile_image")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                database.getReference()
                                        .child("UserData")
                                        .child(Objects.requireNonNull(auth.getUid()))
                                        .child("profile")
                                        .setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Wrong Image Upload", Toast.LENGTH_SHORT).show();
        }
    }


}