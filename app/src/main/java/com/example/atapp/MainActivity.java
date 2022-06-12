package com.example.atapp;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArtViewModel artViewModel;
    private BottomSheetBehavior mBehavior;
    private View bottomSheet;
    private TextView tvTitle, tvDescription, tvSmartifyLink, tvArtist;
    private ImageView ivArt;
    private CoordinatorLayout clFirstBackground;

    private LottieAnimationView animationView, lavLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        clFirstBackground = findViewById(R.id.clFirstBackground);

        ivArt = findViewById(R.id.ivArt);
        tvArtist = findViewById(R.id.tvArtist);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvSmartifyLink = findViewById(R.id.tvSmartifyLink);
        lavLoader = findViewById(R.id.lavLoader);

        bottomSheet = findViewById(R.id.rlBottomSheet);

        animationView = findViewById(R.id.animationView);
        animationView.setMinAndMaxProgress(0.0f, 0.5f);

        animationView.playAnimation();

        mBehavior = BottomSheetBehavior.from(bottomSheet);
        mBehavior.setPeekHeight(screenHeight / 5, true);

        artViewModel = new ViewModelProvider(this).get(ArtViewModel.class);

        artViewModel.makeAPICall();

        artViewModel.getArt().observe(this, new Observer<Art>() {
            @Override
            public void onChanged(Art art) {
                if (art != null) {
                    clFirstBackground.setBackgroundColor(Color.rgb(extractColors(art.getMuted())[0],
                            extractColors(art.getMuted())[1],
                            extractColors(art.getMuted())[2]));
                    bottomSheet.setBackgroundColor(Color.rgb(extractColors(art.getMuted())[0],
                            extractColors(art.getMuted())[1],
                            extractColors(art.getMuted())[2]));
                    animationView.addValueCallback(
                            new KeyPath("**"),
                            LottieProperty.COLOR_FILTER,
                            new SimpleLottieValueCallback<ColorFilter>() {
                                @Override
                                public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                                    return new PorterDuffColorFilter(Color.rgb(extractColors(art.getVibrant())[0],
                                            extractColors(art.getVibrant())[1],
                                            extractColors(art.getVibrant())[2]), PorterDuff.Mode.SRC_ATOP);
                                }
                            }
                    );

                    lavLoader.setVisibility(View.VISIBLE);
                    Glide
                            .with(getApplicationContext())
                            .load(art.getImage())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    lavLoader.setVisibility(View.GONE);
                                    tvTitle.setTextColor(Color.rgb(extractColors(art.getVibrant())[0],
                                            extractColors(art.getVibrant())[1],
                                            extractColors(art.getVibrant())[2]));
                                    tvArtist.setTextColor(Color.rgb(extractColors(art.getVibrant())[0],
                                            extractColors(art.getVibrant())[1],
                                            extractColors(art.getVibrant())[2]));
                                    tvDescription.setTextColor(Color.rgb(extractColors(art.getVibrant())[0],
                                            extractColors(art.getVibrant())[1],
                                            extractColors(art.getVibrant())[2]));
                                    tvSmartifyLink.setTextColor(Color.rgb(extractColors(art.getVibrant())[0],
                                            extractColors(art.getVibrant())[1],
                                            extractColors(art.getVibrant())[2]));

                                    tvTitle.setText(art.getTitle());
                                    tvArtist.setText(art.getArtist() + "\n\n\n\n");
                                    tvDescription.setText(art.getDescription());
                                    tvSmartifyLink.setText("Read more on SMARTIFY.");
                                    return false;
                                }
                            })
                            .override(2000, 2000)
                            .into(ivArt)
                            .getSize((width, height) -> {
                                //before you load image LOG height and width that u actually got?
                                Log.d(TAG, "onResourceReady: Image: " + art.getTitle() + " Height: " + height + " Width: " + width);
                            });

                    mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                animationView.setMinAndMaxProgress(0.5f, 1.0f);
                            } else {
                                animationView.setMinAndMaxProgress(0.0f, 0.5f);
                            }
                            animationView.playAnimation();
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                        }
                    });

                    animationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            } else if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        }
                    });
                }
            }
        });
    }

    private int[] extractColors(String rgb) {
        int[] colorRGB = new int[3];

        String[] rgbValues = rgb.split(",");

        colorRGB[0] = (int) Double.parseDouble(rgbValues[0]);
        colorRGB[1] = (int) Double.parseDouble(rgbValues[1]);
        colorRGB[2] = (int) Double.parseDouble(rgbValues[2]);

        return colorRGB;
    }


    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

