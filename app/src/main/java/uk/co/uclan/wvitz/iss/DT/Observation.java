package uk.co.uclan.wvitz.iss.DT;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Observation extends SugarRecord {
    @Unique long id;
    long timestamp;
    String longitude;
    String latitude;
    String note;
    ArrayList<byte[]> images;

    public Observation() {

    }

    public Observation(long timestamp, String longitude, String latitude, String note, ArrayList<byte[]> images) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.note = note;
        this.images = images;
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

    public ArrayList<byte[]> getImages() {
        return images;
    }

    public void setImages(ArrayList<byte[]> images) {
        this.images = images;
    }

    public String getLonString() {
        return "Lon: " + this.getLongitude();
    }

    public String getLatString() {
        return "Lat: " + this.getLatitude();
    }

    public String getTimestampFormated() {

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
}
