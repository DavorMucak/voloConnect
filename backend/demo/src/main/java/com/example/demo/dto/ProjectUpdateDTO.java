package com.example.demo.dto;

public class ProjectUpdateDTO {
    private String imeProjekta;
    private String opisProjekta;
    private int brojLjudi;
    private String datumPoc;
    private String datumKraj;
    private boolean jeLiHitno;
    private String ownerId;

    // Getteri i setteri
    public String getImeProjekta() {
        return imeProjekta;
    }

    public void setImeProjekta(String imeProjekta) {
        this.imeProjekta = imeProjekta;
    }

    public String getOpisProjekta() {
        return opisProjekta;
    }

    public void setOpisProjekta(String opisProjekta) {
        this.opisProjekta = opisProjekta;
    }

    public int getBrojLjudi() {
        return brojLjudi;
    }

    public void setBrojLjudi(int brojLjudi) {
        this.brojLjudi = brojLjudi;
    }

    public String getDatumPoc() {
        return datumPoc;
    }

    public void setDatumPoc(String datumPoc) {
        this.datumPoc = datumPoc;
    }

    public String getDatumKraj() {
        return datumKraj;
    }

    public void setDatumKraj(String datumKraj) {
        this.datumKraj = datumKraj;
    }

    public boolean isJeLiHitno() {
        return jeLiHitno;
    }

    public void setJeLiHitno(boolean jeLiHitno) {
        this.jeLiHitno = jeLiHitno;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}

