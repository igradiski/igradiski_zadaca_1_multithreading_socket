package org.foi.nwtis.igradiski.zadaca_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.concurrent.locks.ReentrantLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.igradiski.konfiguracije.Konfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;

public class ServerAviona {
    static boolean primajKlijente= true;
    public static ArrayList<Avion> kolekcijaAviona = new ArrayList<>();
    static ArrayList<ServisAviona> servisneDretve = new ArrayList<>();
    static ArrayList<ZahtjevAviona> korisnickeDretve = new ArrayList<>();
    static int brojAktivnihDretvi=0;
    static int maxBrojDretvi=0;
    public static File datoteka;
    static ThreadGroup GrupaDretviSD = null;
    static ThreadGroup GrupaDretviKD = new ThreadGroup("igradiski_KD");
    static ReentrantLock lock;
    
    /**
     * Main funkcija koja pali server aviona
     * @param args - datoteka,broj dretvi serera
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws ParseException 
     */
    public static void main(String[] args) throws NemaKonfiguracije, NeispravnaKonfiguracija, FileNotFoundException, IOException, InterruptedException, ClassNotFoundException, ParseException {
        String sintaksa = "([a-z|A-Z|0-9|!|#|-|_]{0,1000}.((?:txt|xml|bin|json)))( --brojDretvi ([0-9]*$))";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
        } else {
            System.out.println("Ne odgovara!");
            return;
        }
        lock = new ReentrantLock(true);
        pozoviPrekid();
        PreuzimanjeKonfiguracije(m);
    }

    public static void setPrimajKlijente(boolean primajKlijente) {
        ServerAviona.primajKlijente = primajKlijente;
    }

    
    public static ArrayList<ServisAviona> getServisneDretve() {
        return servisneDretve;
    }

    public static ArrayList<ZahtjevAviona> getKorisnickeDretve() {
        return korisnickeDretve;
    }
    
    /**
     * funkcija koja poziva prekid na ctrl+c
     */
    public static void pozoviPrekid() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                lock.lock();
                System.out.println("Zavrsetak rada!");
                PrekiniRadSvihDretvaSustava();
                DohvatiServisnuDretvu();
                lock.unlock();
            }
        });
    }
    /**
     * Funkcija koja prekida rad svih dretvi koje trenutno
     * rade u sustavu te ih briše iz liste aktivnih dretvi
     */
    public static void PrekiniRadSvihDretvaSustava() {
        for (ZahtjevAviona dretva : korisnickeDretve) {
            dretva.interrupt();
        }
        korisnickeDretve.clear();
    }
    /**
     * funckija koj agasi program kada se pozove --kraj kod korisnika
     */
    public static void Ugasi() {
        System.exit(0);
    }
    /**
     * FUnckija koja dohvaća servisnu dretvu i gasi istu
     */
    public static void DohvatiServisnuDretvu() {
        for (ServisAviona servis : servisneDretve) {
            servis.Radi = false;
            servis.interrupt();
        }
    }
    public static File getDatoteka() {
        return datoteka;
    }

    public static  void setDatoteka(File datoteka) {
        ServerAviona.datoteka=datoteka;
    }
    
    /**
     * Dohvaća konfiguraciju iz datoteke te provjerava postoji li,
     * ako postoji provjerava port(ako je zauzet gasi  app) 
     * i postojanje datoteka,
     * ako ne postoji pise da ne postoji datoteka
     * @param m is matchera se dohvaca ime datoteke
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws ParseException 
     */
    public static void PreuzimanjeKonfiguracije(Matcher m) throws NemaKonfiguracije, NeispravnaKonfiguracija, FileNotFoundException, IOException, InterruptedException, ClassNotFoundException, ParseException {
        maxBrojDretvi = Integer.parseInt(m.group(4));
        File f = new File(m.group(1));
        if (f.exists()) {
            ServerAviona.setDatoteka(f);
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(f.getName());
            int port = Integer.parseInt(konfig.dajPostavku("port.avioni"));
            boolean portSlobodan = ProvjeraZauzetostiPorta(port);
            if (portSlobodan) {
                ProvjeriPostojanjeDatoteka(konfig);
            } else {
                System.out.println("Port je zauzet !");
                return;
            }
        } else {
            System.out.println("Ne postoji datoteoka!");
            return;
        }
    }
    /**
     * Provjerava zauzetost porta za server
     * @param port port koji se provjerava
     * @return true ako je slobodan, false ako je zauzet
     */
    public static boolean ProvjeraZauzetostiPorta(int port) {
        boolean portFree;
        try (var ignored = new ServerSocket(port)) {
            portFree = true;
            return portFree;
        } catch (IOException e) {
            portFree = false;
            return portFree;
        }
    }
    /**
     * Provjerava postojanje datoteka koje su pobrojane u konfig datoteci
     * @param konfig konfiguracija dobivena orekko main funkcije
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws ParseException 
     */
    public static void ProvjeriPostojanjeDatoteka(Konfiguracija konfig) throws FileNotFoundException, IOException, InterruptedException, ClassNotFoundException, ParseException {
        String datotekaKorisnika = konfig.dajPostavku("datoteka.korisnika");
        String datotekaAerodrom = konfig.dajPostavku("datoteka.aerodromi");
        String datotekaAvioni = konfig.dajPostavku("datoteka.avioni");
        File korisnici = new File(datotekaKorisnika);
        File aerodromi = new File(datotekaAerodrom);
        if (korisnici.exists() && aerodromi.exists()) {
            UcitajKorisnikeIzDatoteke(korisnici);
            UcitajAerodromeIzDatoteke(aerodromi);
            boolean PostojiAvioniBin = ProvjeriPostojanjeDatoteke(datotekaAvioni);
            if (PostojiAvioniBin) {
                IspisiAvioneIzDatoteke();
            }
        } else {
            System.out.println("Datoteke ne postoje ili su u krivom formatu!");
        }
        UspostaviPocetnoStanje(konfig.dajPostavku("interval.pohrane.aviona"), datotekaAvioni, konfig);
    }
    /**
     * Funkcija koja na temlju parametara uspostavlja pocetno stanje servera
     * @param intervalPohrane - interval pohrane kod serijalizacije
     * @param datotekaAvioni - datoteka u kojoj su popisani avionu
     * @param konfig - konfiguracija dobivena orekko main funkcije
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void UspostaviPocetnoStanje(String intervalPohrane, String datotekaAvioni, Konfiguracija konfig) throws IOException, InterruptedException {
        GrupaDretviSD = new ThreadGroup("igradiski_SD");
        ServisAviona dretva = new ServisAviona(intervalPohrane, datotekaAvioni, GrupaDretviSD, GrupaDretviSD.getName(), GrupaDretviSD.activeCount());
        servisneDretve.add(dretva);
        if (dretva.getState().equals(Thread.State.WAITING) || !dretva.isAlive()) {
            dretva.start();
        }
        int brojCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
        int port = Integer.parseInt(konfig.dajPostavku("port.avioni"));
        ServerSocket server = new ServerSocket(port, brojCekaca);
        PokreniServerZaZahtjeveKorisnika(konfig, server);
    }
    /**
     * Kreiranje korisničke dretve kada pristigne zahtjev korisnika
     */
    public static void KreirajDretvuZaKorisnike() {
        ZahtjevAviona dretva
                = new ZahtjevAviona(GrupaDretviKD,
                        GrupaDretviKD.getName(),
                        korisnickeDretve.size() + 1);
        korisnickeDretve.add(dretva);
    }
    /**
     * Provjerava da li ima slobodnih dretvi za korisnika
     * @param veza veza koja je proslijedena kod spajanje korisnika
     * @throws IOException 
     */
    public static void ProvjeriKolicinuDretvi(Socket veza) throws IOException {
        ObrisiDretveKojeNisuKoristene();
        if (korisnickeDretve.size() < maxBrojDretvi) {
            KreirajDretvuZaKorisnike();
        }
    }
    /**
     * Provjera da li je dosla naredba za gasenje sustava
     * 
     */
    public static void ProvjeriGasenjeSustava() {
        if (!primajKlijente) {
            lock.lock();
            System.out.println("Zavrsetak rada!");
            PrekiniRadSvihDretvaSustava();
            DohvatiServisnuDretvu();
            lock.unlock();
        }
    }
    /**
     * Pokreće se server za primanje zahtjeva korisnika sa danim parametrima
     * @param konfig konfiguracija koja je dosla iz main funckije
     * @param server server socket 
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void PokreniServerZaZahtjeveKorisnika(Konfiguracija konfig, ServerSocket server) throws IOException, InterruptedException {
        while (primajKlijente) {
            ProvjeriGasenjeSustava();
            Socket veza = server.accept();
            ProvjeriKolicinuDretvi(veza);
            boolean slobodno = ProvjeriRaspolozivostDretvi();
            if (slobodno) {
                ZahtjevAviona slobodnaDretva = PronadiSlobodnuDretvu(veza);
                AktivirajDretvu(veza, slobodnaDretva);
            } else {
                OutputStream outs = veza.getOutputStream();
                outs.write("ERROR 01; Nema slobodnih dretvi;".getBytes());
                outs.flush();
                Thread.sleep(100);
                veza.shutdownOutput();
                veza.shutdownInput();
                veza.close();
            }
        }
    }
    /**
     * Funkcija koja oslobađa i briše dretve koje su završile komunikaciju sa
     * korisnikom
     */
    public static void ObrisiDretveKojeNisuKoristene() {
        ArrayList<ZahtjevAviona> korisnickeDretveBrisanje = new ArrayList<>();
        for (ZahtjevAviona dretva : korisnickeDretve) {
            if (dretva.getState().equals(Thread.State.NEW)
                    || dretva.getState().equals(Thread.State.RUNNABLE)) {
                if (!dretva.lock.isLocked()) {
                    korisnickeDretveBrisanje.add(dretva);
                }
            }
        }
        for (ZahtjevAviona dretvaBrisi : korisnickeDretveBrisanje) {
            dretvaBrisi.interrupt();
            korisnickeDretve.remove(dretvaBrisi);
        }
    }
    /**
     * Aktivacija dretve na način da joj se da nova veza s korinsikom
     * @param veza veza s kojom komunicira s korisnikom
     * @param slobodna slobodna dretva
     */
    public static void AktivirajDretvu(Socket veza, ZahtjevAviona slobodna) {
        if (slobodna.getState().equals(Thread.State.NEW)) {
            slobodna.veza = veza;
            slobodna.start();
        }
        if (slobodna.getState().equals(Thread.State.RUNNABLE)) {
            slobodna.veza = veza;
        }
    }
    /**
     * Trazenje slobodne dretve za korisnika
     * @param veza veza prema korisniku
     * @return slobodna dretva
     */
    public static ZahtjevAviona PronadiSlobodnuDretvu(Socket veza) {
        ZahtjevAviona dretvaStart = null;
        for (ZahtjevAviona dretva : korisnickeDretve) {
            if (!dretva.lock.isLocked()) {
                dretvaStart = dretva;
                break;
            }
        }
        return dretvaStart;
    }
    /**
     * Provjerava raspolozivost dretvi u sustavu
     * @return slobodna dretva
     */
    public static boolean ProvjeriRaspolozivostDretvi() {
        boolean slobodna = false;
        for (ZahtjevAviona dretva : korisnickeDretve) {
            if (dretva.lock.isLocked()) {
                slobodna = false;
            } else {
                slobodna = true;
            }
        }
        return slobodna;
    }
    
    /**
     * Funkcija sluzi za ucitavanje aviona u memoriju sustava
     * @param avioniSvi lista svih aviona
     * @throws ParseException 
     */
    public static void UcitajAvioneUMemoriju(ArrayList<String> avioniSvi) throws ParseException {
        for (String avion : avioniSvi) {
            String[] podaci = avion.split(";");
            String let = podaci[0].substring(4);
            String polaziste = podaci[1].substring(10);
            String odrediste = podaci[2].substring(10);
            String poletio = IspraviFormatDatuma(podaci[3].substring(12));
            String sletio = IspraviFormatDatuma(podaci[4].substring(11));
            Avion a = new Avion(let, poletio, sletio, polaziste, odrediste);
            kolekcijaAviona.add(a);
        }
    }
    /**
     * Funkcija za ispravljanje datuma kod ispisa
     * @param datum datum koji se ispravlja
     * @return ispravljeni datum
     * @throws ParseException 
     */
    public static String IspraviFormatDatuma(String datum) throws ParseException {
        SimpleDateFormat kae = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = kae.parse(datum);
        return kae.format(date);
    }
    /**
     * Funkcija koja ispisuje avione koji su u datoteci na ekran te poziva ucitavanje u memoriju
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws ParseException 
     */
    public static void IspisiAvioneIzDatoteke() throws IOException, ClassNotFoundException, ParseException {
        ObjectInputStream ois;
        ArrayList<String> poljeAviona = new ArrayList<String>();
        try {
            ois = new ObjectInputStream(new FileInputStream("avioni.bin"));
            poljeAviona = (ArrayList<String>) (ois.readObject());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("======================================ISPIS AVIONA IZ DATOTEKE=====================");
        for (String avion : poljeAviona) {
            IspisiPodatkeAviona(avion);
            //System.out.println(avion);
        }
        System.out.println("===================================================================================");
        UcitajAvioneUMemoriju(poljeAviona);
    }
    public static void IspisiPodatkeAviona(String avion) throws ParseException{
        String[] podaci = avion.split(";");
        String let = podaci[0].substring(4);
        String polaziste = podaci[1].substring(10);
        String odrediste = podaci[2].substring(10);
        String nazivPolazista = DohvatiNazivAerodrom(podaci[1].substring(10));
        String nazivOdredista = DohvatiNazivAerodrom(podaci[2].substring(10));
        String poletio = IspraviFormatDatuma(podaci[3].substring(12));
        String sletio = IspraviFormatDatuma(podaci[4].substring(11));
        System.out.println("LET: "+ let+" POLAZISTE: "+nazivPolazista+
              " ODREDISTE: "+nazivOdredista+
              " POLETIO: "+poletio+" SLETIO: "+sletio);
    }
    public static String DohvatiNazivAerodrom(String kratica){
        String trazeniIcao='"'+kratica+'"';
        String naziv = "";
        for(Aerodrom a : Aerodrom.AerodromiList){
            if(a.getICAO().equals(trazeniIcao)){
                naziv = a.getNaziv();
            }   
        }
        return naziv;
    }
    /**
     * Ucitava aerodrome koji su spremljeni u datoteci aerodromi.csv
     * @param aerodromi Datoteka aerodroma
     * @throws FileNotFoundException 
     */
    public static void UcitajAerodromeIzDatoteke(File aerodromi) throws FileNotFoundException {
        FileReader fr = new FileReader(aerodromi);
        BufferedReader bf = new BufferedReader(fr);
        String red;
        try {
            while ((red = bf.readLine()) != null) {
                String[] podaci = red.split(",");
                try {
                    int duzinaStringa = podaci[4].length();
                    String icao = podaci[0];
                    String naziv = podaci[1];
                    String drzava = podaci[2];
                    String sirinaString = podaci[3].substring(1);
                    String duzinaString = podaci[4].substring(0, duzinaStringa - 1);
                    Double sirina = Double.parseDouble(sirinaString);
                    Double duzina = Double.parseDouble(duzinaString);
                    Aerodrom a = new Aerodrom(icao, naziv, drzava, sirina, duzina);
                } catch (Exception ex) {
                    
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Ucitava korisnike iz datoteke korisnici.xml
     * @param korisnici datoteka korisnici.xml
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void UcitajKorisnikeIzDatoteke(File korisnici) throws FileNotFoundException, IOException {
        ArrayList<String> korisniciString = new ArrayList<>();
        FileReader fr = new FileReader(korisnici);
        BufferedReader bf = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line = bf.readLine();
        while (line != null) {
            if (line.contains("key=")) {
                korisniciString.add(line);
            }
            line = bf.readLine();
        }
        DohvatiUsernamePassword(korisniciString);
    }
    /**
     * Dohvaća username i password korisnika te ga sprema u memoriju
     * @param korisniciString lista stringova formata username;password
     */
    public static void DohvatiUsernamePassword(ArrayList<String> korisniciString) {
        String[] polje;
        for (String korisnik : korisniciString) {
            polje = korisnik.split(">");
            String korisnickoIme = polje[0].substring(12, polje[0].length() - 1);
            String lozinka = polje[1].substring(0, polje[1].length() - 7);
            Korisnik k = new Korisnik(korisnickoIme, lozinka);
        }
    }
    /**
     * Provjerava postoji li dana datoteka
     * @param datotekaAvioni datoteka za provjeru
     * @return true postoji , false ne postoji datoteka
     */
    public static boolean ProvjeriPostojanjeDatoteke(String datotekaAvioni) {
        File f = new File(datotekaAvioni);
        if (f.exists()) {
            if (f.length() > 0) {
                return true;
            }
        } else {
            System.out.println("Datoteka ne postoji!");
            return false;
        }
        return false;
    }
}
