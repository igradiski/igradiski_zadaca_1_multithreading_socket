
package org.foi.nwtis.igradiski.zadaca_1;

import java.util.ArrayList;

public class Aerodrom {

    public static ArrayList<Aerodrom> AerodromiList = new ArrayList<>();
    private String ICAO;
    private String Naziv;
    private String Drzava;
    private double geoSirina;
    private double geoDuzina;

    /**
     * Konstruktor klase aerodrom
     * 
     * @param ICAO - je icao aerodroma
     * @param Naziv - naziv aerodroma
     * @param Drzava - naziv drzave u kojoj je aerodrom
     * @param geoSirina - geo sirina aerodroma
     * @param geoDuzina  -geo duzina aerodroma
     */
    public Aerodrom(String ICAO, String Naziv, String Drzava, double geoSirina, double geoDuzina) {
        this.ICAO = ICAO;
        this.Naziv = Naziv;
        this.Drzava = Drzava;
        this.geoSirina = geoSirina;
        this.geoDuzina = geoDuzina;
        AerodromiList.add(this);
    }
    /**
     * Klasa koja sluzi za ispis poadataka aerodroma
     */
    public void Ispis() {
        System.out.println(this.ICAO);
        System.out.println(this.Naziv);
        System.out.println(this.Drzava);
        System.out.println(this.geoSirina);
        System.out.println(this.geoDuzina);

    }
    

    public double getGeoSirina() {
        return geoSirina;
    }

    public double getGeoDuzina() {
        return geoDuzina;
    }
    

    public String getNaziv() {
        return Naziv;
    }

    public String getICAO() {
        return ICAO;
    }
}
