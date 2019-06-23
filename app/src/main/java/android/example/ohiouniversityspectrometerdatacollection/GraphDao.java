package android.example.ohiouniversityspectrometerdatacollection;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;



import java.util.Date;
import java.util.List;



@Dao
public interface GraphDao {

    @Insert
    void insert(SavedGraph savedGraph);

    /*
    @Query("SELECT id FROM graph_table ORDER BY spectrum_date")
    LiveData<List<Integer>> getAllIds();
    */

    @Query("SELECT spectrum_name FROM graph_table ORDER BY spectrum_date DESC")
    LiveData<List<String>> getAllNames();

    @Query("SELECT spectrum_date FROM graph_table ORDER BY spectrum_date DESC")
    LiveData<List<Date>> getAllDates();

    @Query("SELECT * FROM graph_table WHERE spectrum_date = :date LIMIT 1")
    SavedGraph getGraphFromDate(Date date);

    /*
    @Query("SELECT * FROM graph_table WHERE id IS :entry")
    SavedGraph getGraphFromId(Integer entry);


    @Query("SELECT spectrum_name FROM graph_table WHERE id IS :entry")
    String getNameFromId(Integer entry);

    @Query("SELECT spectrum_date FROM graph_table WHERE id IS :entry")
    Date getDateFromId(Integer entry);
    */

    @Delete
    void delete(SavedGraph savedGraph);

}
