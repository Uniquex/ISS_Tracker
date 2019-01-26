package uk.co.uclan.wvitz.iss.DT;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Observation extends SugarRecord {
    @Unique long id;
    long timestamp;
    String longitude;
    String latitude;
    String note;

    @Ignore
    List<Image> images;

    public Observation() {

    }

    public Observation(long timestamp, String longitude, String latitude, String note) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.note = note;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLonString() {
        return "Lon: " + this.getLongitude();
    }

    public String getLatString() {
        return "Lat: " + this.getLatitude();
    }

    public String getTimestampFormatted() {

        Date date = new Date(this.timestamp);
        Calendar now = Calendar.getInstance();
        Calendar compare = Calendar.getInstance();
        compare.setTimeInMillis(this.timestamp);
        DateFormat f;
        if((now.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) && ((now.get(Calendar.DAY_OF_YEAR) == compare.get(Calendar.DAY_OF_YEAR)))) {
            f = new SimpleDateFormat("HH:mm:ss");
        } else {
            f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        }
        return f.format(date);
    }

    public List<byte[]> getImagesFromContext() {
        List<Image> images = Select.from(Image.class).where(Condition.prop("observation").eq(this)).list();
        List<byte[]> list = new ArrayList<>();
        for(int x = 0; x < images.size(); x++) {
            list.add(images.get(x).getImage());
        }

        return list;
    }
}
