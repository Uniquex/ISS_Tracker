package uk.co.uclan.wvitz.iss.DT;

import java.util.Date;

public class PassTime {
    String duration;
    String risetime;

    public PassTime(String duration, String risetime) {
        this.duration = duration;
        this.risetime = risetime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRisetime() {
        return risetime;
    }

    public void setRisetime(String risetime) {
        this.risetime = risetime;
    }
}
