package com.example.atapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ArtDao {

    @Insert
    void insert(Art art);

    @Query("DELETE FROM art_gallery3")
    void delete();

    @Query("SELECT count(*) FROM (select 0 from art_gallery3 limit 1)")
    int isTableEmpty();

    @Query("SELECT * FROM art_gallery3")
    LiveData<Art> getArtPiece();
}
