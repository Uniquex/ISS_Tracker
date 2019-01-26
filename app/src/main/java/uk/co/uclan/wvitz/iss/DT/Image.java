package uk.co.uclan.wvitz.iss.DT;

import com.orm.SugarRecord;

public class Image extends SugarRecord {
    Observation observation;
    byte[] image;

    public Image() { }

    public Image(Observation observation, byte[] image) {
        this.observation = observation;
        this.image = image;
    }

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
