package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.widget.GridLayout;

import com.github.mikephil.charting.data.LineData;

import java.util.Date;


@Entity(tableName = "graph_table")
public class SavedGraph {


    @NonNull
    @ColumnInfo
    private SpectraAndWavelengths mLineData;

    @ColumnInfo(name = "spectrum_name")
    private String mName;

    @PrimaryKey
    @ColumnInfo(name = "spectrum_date")
    private Date mDate;

    public SavedGraph(@NonNull SpectraAndWavelengths lineData, String name, Date date) {
        this.mLineData = lineData;
        this.mName = name;
        this.mDate = date;
    }

    public SpectraAndWavelengths getLineData() {
        return this.mLineData;
    }

    public String getName() {
        return this.mName;
    }

    //public Integer getId() { return this.id; }

    public Date getDate() {
        return this.mDate;
    }

}
