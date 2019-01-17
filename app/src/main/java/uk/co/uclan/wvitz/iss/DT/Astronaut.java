package uk.co.uclan.wvitz.iss.DT;

public class Astronaut {
    private String firstname;
    private String lastname;
    private String craft;

    public Astronaut(String firstname, String lastname, String craft) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.craft = craft;
    }
    public Astronaut(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCraft() {
        return craft;
    }

    public void setCraft(String craft) {
        this.craft = craft;
    }
}
