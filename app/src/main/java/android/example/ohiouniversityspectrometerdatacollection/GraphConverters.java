package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.persistence.room.TypeConverter;

import com.github.mikephil.charting.data.LineData;

import java.util.Date;

public class GraphConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null :new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String spectraAndWavelengthsToString(SpectraAndWavelengths spectraAndWavelengths) {
        if (spectraAndWavelengths == null) {
            return null;
        } else {
            return spectraAndWavelengths.dataToString();
        }
    }

    @TypeConverter
    public static SpectraAndWavelengths fromString(String value) {
        if (value == null) {
            return null;
        } else {
            return new SpectraAndWavelengths(value);
        }
    }

}
