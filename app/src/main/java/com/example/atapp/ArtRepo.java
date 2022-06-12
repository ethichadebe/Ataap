package com.example.atapp;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

public class ArtRepo {

    public static final String TAG = "ArtRepo";
    private ArtDao artDao;
    private LiveData<Art> art;

    public ArtRepo(Application application) {
        ArtDatabase database = ArtDatabase.getInstance(application);

        artDao = database.artDao();

        art = artDao.getArtPiece();

    }

    public void insert(Art art) {
        new InsertArtAsyncTask(artDao).execute(art);
    }

    public void delete() {
        new DeleteArtAsyncTask(artDao).execute();
    }

    public LiveData<Art> getArt() {
        return art;
    }

    private static class InsertArtAsyncTask extends AsyncTask<Art, Void, Void> {
        private ArtDao artDao;

        private InsertArtAsyncTask(ArtDao artDao) {
            this.artDao = artDao;
        }

        @Override
        protected Void doInBackground(Art... arts) {
            artDao.insert(arts[0]);
            Log.d(TAG, "doInBackground: " + arts[0].getTitle());
            return null;
        }
    }

    private static class DeleteArtAsyncTask extends AsyncTask<Art, Void, Void> {
        private ArtDao artDao;

        private DeleteArtAsyncTask(ArtDao artDao) {
            this.artDao = artDao;
        }

        @Override
        protected Void doInBackground(Art... arts) {
            artDao.delete();

            return null;
        }
    }

    private static class isEmptyArtAsyncTask extends AsyncTask<Art, Void, Void> {
        private ArtDao artDao;

        private isEmptyArtAsyncTask(ArtDao artDao) {
            this.artDao = artDao;
        }

        @Override
        protected Void doInBackground(Art... arts) {
            artDao.isTableEmpty();

            return null;
        }
    }
}
