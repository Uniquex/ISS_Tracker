package uk.co.uclan.wvitz.iss.DT;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Observation extends SugarRecord implements Parcelable {
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

    public Observation(Parcel in){

        String[] data = new String[5];
        in.readStringArray(data);

        // the order needs to be the same as in writeToParcel() method
        this.id = Integer.parseInt(data[0]);
        this.longitude = data[1];
        this.latitude = data[2];
        this.note = data[3];
        this.timestamp = Long.parseLong(data[4]);
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

    public long getIdentifier() {
        return id;
    }

    public String getLonString() {
        DecimalFormat df = new DecimalFormat("#.####");
        return "Lon: " + df.format(Float.valueOf(this.getLongitude()));
    }

    public String getLonFormatted() {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(Float.valueOf(this.getLongitude()));
    }
    public String getLatFormatted() {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(Float.valueOf(this.getLatitude()));
    }

    public String getLatString() {
        DecimalFormat df = new DecimalFormat("#.####");
        return "Lon: " + df.format(Float.valueOf(this.getLatitude()));
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
        List<byte[]> list = new ArrayList<>();
        try {
            List<Image> images = Select.from(Image.class).where(Condition.prop("observation").eq(this)).list();
            for (int x = 0; x < images.size(); x++) {
                list.add(images.get(x).getImage());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Image> getImagesCFromContext() {
        return Select.from(Image.class).where(Condition.prop("observation").eq(this)).list();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(this.id),
                this.longitude,
                this.latitude,
                this.note,
                String.valueOf(this.timestamp)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Observation createFromParcel(Parcel in) {
            return new Observation(in);
        }

        public Observation[] newArray(int size) {
            return new Observation[size];
        }
    };
}
