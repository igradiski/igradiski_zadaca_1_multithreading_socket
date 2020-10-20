package org.foi.nwtis.igradiski.zadaca_1;

import java.util.ArrayList;

public class Korisnik {

    public static ArrayList<Korisnik> KorisniciList = new ArrayList<>();
    private String Username;
    private String Password;

    /**
     * Konstruktor klase
     * @param Username - korisnicko ime
     * @param Password - lozinka
     */
    public Korisnik(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
        KorisniciList.add(this);
    }

    /**
     * Funkcija za ispis korisnika
     */
    public void Ispis() {
        System.out.println(this.Password + " " + this.Username);
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

}
