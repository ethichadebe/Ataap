package com.example.atapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtViewModel extends AndroidViewModel {
    private static final String TAG = "ArtViewModel";
    private ArtRepo repo;
    private LiveData<Art> artLiveData;
    private MediatorLiveData<Art> art;

    public ArtViewModel(@NonNull Application application) {
        super(application);

        repo = new ArtRepo(application);
        artLiveData = repo.getArt();
        art = new MediatorLiveData<>();
    }

    public MediatorLiveData<Art> getArtObserver() {
        return art;
    }

    /**
     * Insert art piece to local room database
     * @param art retried from the remote database
     */
    public void insert(Art art) {
        repo.insert(art);
    }

    /**
     * remove art piece from local room database
     */
    public void delete() {
        repo.delete();
    }

    /**
     * get art piece from the remote database
     */
    public void makeAPICall() {
        APIService apiService = RetrofitInstance.getRetrofitClient().create(APIService.class);
        Call<Art> call = apiService.getArt();
        call.enqueue(new Callback<Art>() {
            @Override
            public void onResponse(@NonNull Call<Art> call, @NonNull Response<Art> response) {
                if (!response.isSuccessful()) {
                    Log.d("APICall: response error", String.valueOf(response.code()));

                }
                Log.d(TAG, "onResponse: "+response.body());
                Art art = response.body();
                delete();

                insert(art);
                assert art != null;
                Log.d("APICall: Random Word", art.getRandom_word());
                Log.d("APICall: Title", art.getTitle());
                Log.d("APICall: Artist", art.getArtist());
                Log.d("APICall: Description", art.getDescription());
                Log.d("APICall: Image", art.getImage());
                Log.d("APICall: Vibrant colour", art.getVibrant());
                Log.d("APICall: Muted colours", art.getMuted());


            }

            @Override
            public void onFailure(@NonNull Call<Art> call, @NonNull Throwable t) {
                Log.d("APICall: error", t.getMessage());
                art.postValue(null);
                makeAPICall();
            }
        });
    }

    public LiveData<Art> getArt() {
        return artLiveData;
    }
}
