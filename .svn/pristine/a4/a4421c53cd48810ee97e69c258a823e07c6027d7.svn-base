package org.foi.nwtis.igradiski.zadaca_1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Avion {

    String let;
    String polaziste;
    String odrediste;
    Date slijetanje;
    Date polijetanje;
    SimpleDateFormat formatDatuma = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    /**
     * Konstruktor klase avion
     * 
     * @param let - naziv leta
     * @param polijetanje - vrijeme polijetanja
     * @param slijetanje - vrijeme slijetanja
     * @param polaziste - polazni aerodrom
     * @param odrediste - silazni aerodrom
     * @throws ParseException 
     */
    public Avion(String let, String polijetanje, String slijetanje, String polaziste, String odrediste) throws ParseException {
        this.let = let;
        this.polaziste = polaziste;
        this.odrediste = odrediste;
        this.slijetanje = formatDatuma.parse(slijetanje);
        this.polijetanje = formatDatuma.parse(polijetanje);
    }

    public String getSlijetanje() {
        return formatDatuma.format(slijetanje);
    }

    public String getPolijetanje() {
        return formatDatuma.format(polijetanje);
    }

    /**
     *  Funkcija za ispravljanje formata datuma kod ispisa i spremanja
     * @param datum - datum za formatiranje
     * @return
     * @throws ParseException 
     */
    public static String IspraviFormatDatuma(String datum) throws ParseException {
        SimpleDateFormat kae = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = kae.parse(datum);
        return kae.format(date);
    }
}
