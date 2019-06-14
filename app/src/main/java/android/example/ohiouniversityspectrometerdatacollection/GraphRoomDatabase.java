package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = {SavedGraph.class}, version = 1, exportSchema = false)
@TypeConverters({GraphConverters.class})
public abstract class GraphRoomDatabase extends RoomDatabase {
    public abstract GraphDao graphDao();

    private static GraphRoomDatabase INSTANCE;

    public static GraphRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GraphRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GraphRoomDatabase.class, "graph_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
