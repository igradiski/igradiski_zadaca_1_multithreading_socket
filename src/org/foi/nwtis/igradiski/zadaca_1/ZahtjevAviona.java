/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.zadaca_1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
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
import java.util.concurrent.locks.ReentrantLock;


public class ZahtjevAviona extends Thread {
    
    ReentrantLock lock;
    boolean kraj = false;
    Socket veza;
    ThreadGroup grupa;
    int broj;
    static boolean tocnostZaprimljeneKomande =false;
    static String odgovorServera="";
    
    /**
     * Konstruktor dretve
     * @param GrupaDretvi grupa kojoj pripada
     * @param name naziv dretve
     * @param broj redni broj dretve
     */
    ZahtjevAviona(ThreadGroup GrupaDretvi, String name,int broj) {
       super(GrupaDretvi.getName()+broj);
       this.veza= veza;
       this.broj=broj;
       lock = new ReentrantLock(true);
    }

    public synchronized Socket getVeza() {
        return veza;
    }
  
    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }
     @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        
    }
    /**
     * Funkcija sluzi za prihvacanje komande od korisnika
     */
    public synchronized void obradiZahtjev() {
        try {
            InputStream inps = this.veza.getInputStream();
            OutputStream outs = this.veza.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outs);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                output.write(i);
            }
            byte outputByteova[] = output.toByteArray();
            String vrijednost = new String(outputByteova, "UTF-8");
            System.out.println(vrijednost);
            ObradiZahtjevKlijenta(vrijednost);
            osw.write(odgovorServera);
            osw.flush();
            veza.shutdownOutput();
            veza.shutdownInput();
            veza.close();
        } catch (IOException ex) {
        }
    }
    
    @Override
    public synchronized void run() {
        while (!kraj) {
            synchronized (this) {
                if (veza != null) {
                    lock.lock();
                    obradiZahtjev();
                    veza = null;
                }
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }
    /**
     * Poziva se provjera parametara komande
     * @param komanda  komanda koja se provjerava
     */
    public synchronized static void ObradiZahtjevKlijenta(String komanda){
        ProvjeriParametre(komanda);
        
    }
    /**
     * Provjera danih parametara u komandi
     * @param komanda komanda koja se provjerava
     */
     public synchronized static void ProvjeriParametre(String komanda) {
        if (komanda.contains("KRAJ")) {
            if(ProvjeriIspravnostKomandeKraj(komanda)){
                ArrayList<String>korisnik = ObradiParametreOpcijeKraj(komanda);
                ProvjeriPostojanjeKorisnika(korisnik);
            }
        }
        if (komanda.contains("DODAJ")) {
            if(ProvjeriIspravnostKomandeDodaj(komanda)){
                ArrayList<String>korisnik = ObradiParametreOpcijeDodaj(komanda);
                ProvjeriPostojanjeKorisnika(korisnik);
            }
        }
        if (komanda.contains("UZLETIO")) {
            if(ProvjeriIspravnostKomandeUzletio(komanda)){
                ArrayList<String>korisnik = ObradiParametreOpcijeUzletio(komanda);
                ProvjeriPostojanjeKorisnika(korisnik);
            }
        }
        if (komanda.contains("ISPIS")) {
            if(ProvjeriIspravnostKomandeIspis(komanda)){
                ArrayList<String>korisnik = ObradiParametreOpcijeIspis(komanda);
                ProvjeriPostojanjeKorisnika(korisnik);
            }
        }
    }
     /**
      * Provjera ako je komanda uzletio
      * @param komanda komanda za provjeru
      * @return true tocna, false ne tocna komanda
      */
    public synchronized static boolean ProvjeriIspravnostKomandeUzletio(String komanda) {
        boolean tocnostKomande = false;
        //System.out.println(komanda);
        String sintaksa = "(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )"
                + "(UZLETIO [a-z|A-Z|0-9|_|-|!|#|*]{3,50}; )(POLAZIŠTE [a-z|A-Z|0-9|_|-|!|#|*]{3,50}; )"
                + "(ODREDIŠTE [a-z|A-Z|0-9|_|-|!|#|*]{3,50}; )(TRAJANJE [0-9]{1,10};)";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(komanda);
        tocnostKomande = m.matches();
        if (!tocnostKomande) {
            odgovorServera = "ERROR 02; Sintaksa zahtjeva nije ispravna;";
        }
        return tocnostKomande;
    }
    /**
     * Funckija preuzima parametre i vraca ih u obliku liste
     * @param komanda sa podacima
     * @return  lista podataka
     */
    public synchronized static ArrayList<String> ObradiParametreOpcijeUzletio(String komanda){
        ArrayList<String> lista = new ArrayList<>();
        String[] podaciKorisnika = komanda.split(";");
        String korIme=podaciKorisnika[0].substring(9);
        String lozinka=podaciKorisnika[1].substring(9);
        String komandaTrim = komanda.substring(38);
        String uzletio=podaciKorisnika[2].substring(9);
        String polaziste=podaciKorisnika[3].substring(11);
        String odrediste=podaciKorisnika[4].substring(11);
        String trajanje =podaciKorisnika[5].substring(10);
        lista.add(korIme);
        lista.add(lozinka);
        lista.add(komandaTrim);
        lista.add(uzletio);
        lista.add(polaziste);
        lista.add(odrediste);
        lista.add(trajanje);
        return lista;
    }
     
    /**
     * Funkcija preuzima parametre iz komande i vraća ih u obliku liste
     * @param komanda koja sadrzi podatke
     * @return lista podataka
     */
    public synchronized static ArrayList<String> ObradiParametreOpcijeIspis(String komanda) {
        ArrayList<String> lista = new ArrayList<>();
        String[] podaciKorisnika = komanda.split(";");
        String korIme = podaciKorisnika[0].substring(9);
        String lozinka = podaciKorisnika[1].substring(9);
        String naredba = podaciKorisnika[2].substring(1);
        lista.add(korIme);
        lista.add(lozinka);
        lista.add(naredba);
        return lista;
    }
    /**
     * Provjerava tocnost komande ipis
     * @param komanda komanda za ispis
     * @return true ako je ispravna, false ako nije ispravna
     */
    public synchronized static boolean ProvjeriIspravnostKomandeIspis(String komanda){
        boolean tocnostKomande = false;
        String sintaksa ="(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(ISPIS [a-z|A-Z|0-9|_|-|!|#|*|-]{1,50};)";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(komanda);
        tocnostKomande=m.matches();
        if(!tocnostKomande)
            odgovorServera="ERROR 02; Sintaksa zahtjeva nije ispravna;";
        return tocnostKomande;
    }
    /**
     * Funkcija provjara ispravnost komande kraj
     * @param komanda komanda kraj
     * @return true komanda tocna, false kriva komanda
     */
    public synchronized static boolean ProvjeriIspravnostKomandeDodaj(String komanda) {
        boolean tocnostKomande = false;
        String sintaksa = "(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*|-]{1,50}; )(DODAJ [0-9]{1,10};)";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(komanda);
        tocnostKomande = m.matches();
        if (!tocnostKomande) {
            odgovorServera = "ERROR 02; Sintaksa zahtjeva nije ispravna;";
        }
        return tocnostKomande;
    }
    /**
     * Funkcija obraduje parametre komande dodaj
     * @param komanda komanda dodaj
     * @return  lista podataka iz komande dodaj
     */
    public synchronized static ArrayList<String> ObradiParametreOpcijeDodaj(String komanda) {
        ArrayList<String> lista = new ArrayList<>();
        String[] podaciKorisnika = komanda.split(";");
        String korIme = podaciKorisnika[0].substring(9);
        String lozinka = podaciKorisnika[1].substring(9);
        String naredba = podaciKorisnika[2].substring(1);
        lista.add(korIme);
        lista.add(lozinka);
        lista.add(naredba);
        return lista;
    }
    /**
     * Funkcija provjerava postojanje korisnika 
     * @param korisnikData podaci o danom korisniku
     */
    public synchronized static void ProvjeriPostojanjeKorisnika(ArrayList<String> korisnikData) {
        String username = korisnikData.get(0);
        String password = korisnikData.get(1);
        String naredba = korisnikData.get(2);
        boolean korisnikPostoji = false;
        for (Korisnik k : Korisnik.KorisniciList) {
            if (k.getUsername().equals(username) && k.getPassword().equals(password)) {
                korisnikPostoji = true;
            }
        }
        if (korisnikPostoji) {
            ObradiNaredbu(naredba);
        } else {
            odgovorServera = "ERROR 03; Korisnik nije legitiman(krivi username ili password);";
        }
    }
    /**
     * Poziva se ako postoji korisnik i ako su legitimne komande
     * te pali jednu od 4 komande
     * @param komanda salje se komanda za obradu
     */
    public synchronized static void ObradiNaredbu(String komanda) {
        if (komanda.contains("KRAJ")) {
            UgasiSve(komanda);
            ServerAviona.Ugasi();
        }
        if (komanda.contains("DODAJ")) {
            PovecajMaksimalniBrojDretvi(komanda);
        }
        if (komanda.contains("UZLETIO")) {
            OdradiKomanduUzletio(komanda);
        }
        if (komanda.contains("ISPIS")) {
            OdradiKomanduIspis(komanda);
        }
    }
    /**
     * Funkcija koja gasi cijelu aplikaciju
     * @param komanda  komanda za gasenje
     */
    public synchronized static void UgasiSve(String komanda) {
        ServerAviona.setPrimajKlijente(false);
        odgovorServera = "OK;";
        System.out.println("Poslan odgovor!");
    }
    /**
     * Funkcija koja ukljucuje komandu Uzletio
     * @param komanda komanda koja ce se izvršiti
     */
    public synchronized static void OdradiKomanduUzletio(String komanda) {
        String[] podaci = komanda.split(";");
        String avion = podaci[0].substring(8);
        String polaziste = podaci[1].substring(11);
        String odrediste = podaci[2].substring(11);
        int trajanjeMin = Integer.parseInt(podaci[3].substring(10));
        ProvjeriPostojanjeParametara(avion, polaziste, odrediste, trajanjeMin);
    }
    /**
     * Funkcija koja provjerava da li postoje parametri koji su poslani, a to provjerava
     * u kolekciji aerodroma
     * @param avion avion za koji se vrsi kontrola
     * @param polaziste aerodrom polazita
     * @param odrediste aerodrom odredista
     * @param trajanje  trajanje leta
     */
    public synchronized static void ProvjeriPostojanjeParametara(String avion, String polaziste, String odrediste, int trajanje) {
        boolean postojanjeAerodromaPolaziste = ProvjeriPostojanjeAerodroma(polaziste);
        boolean postojanjeAerodromaOdrediste = ProvjeriPostojanjeAerodroma(odrediste);
        if (postojanjeAerodromaOdrediste && postojanjeAerodromaPolaziste) {
            CitajPodatkeAviona(avion, polaziste, odrediste, trajanje);
        }
    }
    /**
     * Funkcija koja dohvaca avion prema imenu
     * @param avion ime aviona
     * @return  objekt avion ako postoji
     */
    public synchronized static Avion DohvatiAvionPoImenu(String avion) {
        Avion avionZaSimulator = null;
        for (Avion a : ServerAviona.kolekcijaAviona) {
            if (a.let.equals(avion)) {
                avionZaSimulator = a;
            }
        }
        return avionZaSimulator;
    }
    
    /**
     * Funckija koja cita podatke o avionu
     * @param avion ime aviona
     * @param polaziste polaziste aviona
     * @param odrediste odrediste aviona
     * @param trajanje  trajanje leta aivona
     */
    public synchronized static void CitajPodatkeAviona(String avion, String polaziste, String odrediste, int trajanje) {
        if (PostojiAvion(avion)) {
            if (IspravniPodaciAviona(avion, polaziste, odrediste)) {
                Avion a = DohvatiAvionPoImenu(avion);
                PosaljiKomanduSimulatoruLeta(a);
            } else {
                odgovorServera = "ERROR 14; Ne odgovoraju polaziste i odrediste aviona!;";
            }
        } else {
            DodajAvionUKolekciju(avion, polaziste, odrediste, trajanje);
        }
    }
    /**
     * Funkcija koja salje komandu na simulator leta
     * @param a objekt aviona od kojeg se uzimaju podaci za komandu
     */
    public synchronized static void PosaljiKomanduSimulatoruLeta(Avion a) {
        String komanda;
        File f = ServerAviona.getDatoteka();
        SimpleDateFormat kae = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        komanda = "LET " + a.let + "; POLIJETANJE " + kae.format(a.polijetanje) + "; SLIJETANJE " + kae.format(a.slijetanje) + ";";
        Konfiguracija konfig;
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(f.getName());
            int port = Integer.parseInt(konfig.dajPostavku("port.simulator"));
            PosaljiKomanduServeruSimulatoraLeta(port, komanda, "let");
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ZahtjevAviona.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(ZahtjevAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Funkcija koja vraca ispravnost podataka aviona
     * @param avion ime aviona
     * @param polaziste polaziste aviona
     * @param odrediste odrediste aviona
     * @return  true ispravni podaci, false krivi podaci
     */
    public synchronized static boolean IspravniPodaciAviona(String avion, String polaziste, String odrediste) {
        boolean podaciIspravni = false;
        Avion avionZaProvjeru = null;
        for (Avion a : ServerAviona.kolekcijaAviona) {
            if (a.let.equals(avion)) {
                avionZaProvjeru = a;
            }
        }
        if (avionZaProvjeru.polaziste.equals(polaziste) && avionZaProvjeru.odrediste.equals(odrediste)) {
            podaciIspravni = true;
        }
        return podaciIspravni;
    }
    /**
     * Isrpavlja format datuma za ispis
     * @param datum za ispraviti
     * @return ispravljeni datum
     * @throws ParseException 
     */
    public synchronized static String IspraviFormatDatuma(String datum) throws ParseException {
        SimpleDateFormat kae = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = kae.parse(datum);
        return kae.format(date);
    }
    /**
     * Dodavanje aviona u kolekciju aviona
     * @param avion avion ime
     * @param polaziste polaziste aviona
     * @param odrediste odrediste aviona
     * @param trajanje trajanje leta
     */
    public synchronized static void DodajAvionUKolekciju(String avion, String polaziste, String odrediste, int trajanje) {
        SimpleDateFormat formatDatuma = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        int trajanjeMilis = trajanje * 1000;
        try {
            Date polijetanje = new Date(System.currentTimeMillis());
            Date slijetanje = new Date(System.currentTimeMillis() + trajanjeMilis);
            String vrijemePolijetanja = formatDatuma.format(polijetanje);
            String vrijemeSlijetanja = formatDatuma.format(slijetanje);
            Avion a = new Avion(avion, vrijemePolijetanja, vrijemeSlijetanja, polaziste, odrediste);
            ServerAviona.kolekcijaAviona.add(a);
            PosaljiKomanduSimulatoruLeta(a);
        } catch (ParseException ex) {
            Logger.getLogger(ZahtjevAviona.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * Provjera postojanja aviona 
     * @param avion naziv aviona
     * @return  true postoji, false ne postoji
     */
    public synchronized static boolean PostojiAvion(String avion) {
        boolean postoji = false;
        for (Avion a : ServerAviona.kolekcijaAviona) {
            if (a.let.equals(avion)) {
                postoji = true;
                break;
            }
        }
        return postoji;
    }
    /**
     * Funkcija provjerava postoji li aerodrom 
     * @param aerodrom ime aerodroma
     * @return true postoji aerodrom , false ne postoji aerodrom
     */
    public synchronized static boolean ProvjeriPostojanjeAerodroma(String aerodrom) {
        boolean postoji = false;
        for (Aerodrom a : Aerodrom.AerodromiList) {
            if (a.getICAO().equals('"' + aerodrom + '"')) {
                postoji = true;
                break;
            }
        }
        if (!postoji) {
            odgovorServera = "ERROR 13; Aerodrom ne postoji!;";
        }
        return postoji;
    }
    
    /**
     * Funkcija koja odradi funkciju ispis
     * @param komanda  komanda ispis
     */
    public synchronized static void OdradiKomanduIspis(String komanda) {
        String nazivAviona = komanda.substring(6);
        Avion a = DohvatiPodatkeOAvionu(nazivAviona);
        if (a == null) {
            odgovorServera = "ERROR 16; Ne postoji taj avion!;";
        } else {
            IspisiDohvaceniAvion(a);
            odgovorServera = "Postoji avion";
            PripremiKomanduServeruSimulatoraLeta(a);
        }
    }
    /**
     * Priprema komande i format komande za simulator leta
     * @param a objekt avion
     */
    public synchronized static void PripremiKomanduServeruSimulatoraLeta(Avion a) {
        String naredbaServeru = "POZICIJA: " + a.let + ";";
        File f = ServerAviona.getDatoteka();
        Konfiguracija konfig;
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(f.getName());
            int port = Integer.parseInt(konfig.dajPostavku("port.simulator"));
            PosaljiKomanduServeruSimulatoraLeta(port, naredbaServeru, "pozicija");
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ZahtjevAviona.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(ZahtjevAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Funkcija za slanje komande na server simulatora leta
     * @param port port na koji se salje
     * @param komanda komanda koja se salje
     * @param tipNaredbe tip naredbe koji će se obraditi
     */
    public synchronized static void PosaljiKomanduServeruSimulatoraLeta(int port, String komanda, String tipNaredbe) {
        try {
            Socket veza = new Socket("localhost", port);
            InputStream inps = veza.getInputStream();
            OutputStream outs = veza.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outs);
            osw.write(komanda);
            osw.flush();
            veza.shutdownOutput();
            StringBuilder tekst = new StringBuilder();
            while (true) {
                int i = inps.read();
                if (i == -1) {
                    break;
                }
                tekst.append((char) i);
            }
            ObradiOdgovorZaKorisnika(tekst.toString(), tipNaredbe, komanda);
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /**
     * Funkcija koja obraduje odogovr koji ce se proslijediti korisniku
     * @param poruka poruka za korisnika
     * @param tip tip poruke 
     * @param komanda  komanda za korisnika
     */
     public synchronized static void ObradiOdgovorZaKorisnika(String poruka, String tip, String komanda) {
        if (tip.equals("let")) {
            if (poruka.contains("OK;")) {
                VratiUdaljenostKorisniku(komanda);
            } else {
                String odgovor = poruka.substring(9);
                System.out.println("Avion jos leti");
                odgovorServera = "ERROR 15; " + odgovor + ";";
            }
        } else {
            if (poruka.contains("OK;")) {
                odgovorServera = poruka;
            } else {
                String odgovor = poruka.substring(9);
                odgovorServera = "ERROR 16; " + odgovor + ";";
            }
        }
    }
     /**
      * Funkcija koja sluzi za formatiranje odgovora koji se vraća korisniku,
      * tocnije vraca udaljenost od polazista do odredista korisniku
      * @param komanda komanda s udaljenošću
      */
    public synchronized static void VratiUdaljenostKorisniku(String komanda) {
        String[] letPodaci = komanda.split(";");
        String nazivAviona = letPodaci[0].substring(4);
        Avion a = DohvatiAvionPoImenu(nazivAviona);
        Aerodrom AerodromPolaziste = DohvatiAerodromPoImenu(a.polaziste);
        Aerodrom AerodromOdrediste = DohvatiAerodromPoImenu(a.odrediste);
        Koordinata polaziste = new Koordinata(AerodromPolaziste.getGeoSirina(), AerodromPolaziste.getGeoDuzina());
        Koordinata odrediste = new Koordinata(AerodromOdrediste.getGeoSirina(), AerodromOdrediste.getGeoDuzina());
        int udaljenost = Koordinata.izracunajUdaljenost(polaziste, odrediste);
        odgovorServera = "OK; UDALJENOST " + udaljenost + ";";
    }
    /**
     * Dohvacanje aerodrom po imenu
     * @param icao ime aerodroma
     * @return  vraca aerodrom objekt
     */
    public synchronized static Aerodrom DohvatiAerodromPoImenu(String icao) {
        Aerodrom a = null;
        String icaoZaPretragu = '"' + icao + '"';
        for (Aerodrom aerodrom : Aerodrom.AerodromiList) {
            if (aerodrom.getICAO().equals(icaoZaPretragu)) {
                a = aerodrom;
                break;
            }
        }
        return a;
    }
    /**
     * Funkcija za ispis dohvaćenog aviona
     * @param a  objekt aviona
     */
    public synchronized static void IspisiDohvaceniAvion(Avion a) {
        String avionData = "LET " + a.let + ";POLAZIŠTE " + a.polaziste + ";ODREDIŠTE " + a.odrediste + ";POLIJETANJE " + a.getPolijetanje() + ";SLIJETANJE " + a.getSlijetanje() + ";";
        System.out.println("====================================================");
        System.out.println(avionData);
        System.out.println("====================================================");
    }
    /**
     * Dohvaćanje podataka o avionu
     * @param naziv objekta avion
     * @return objekt avion
     */
    public synchronized static Avion DohvatiPodatkeOAvionu(String naziv) {
        Avion avion = null;
        for (Avion a : ServerAviona.kolekcijaAviona) {
            if (a.let.equals(naziv)) {
                avion = a;
            }
        }
        return avion;
    }
    /**
     * Funkcija koja povećava maksimalni broj dretvi servera
     * @param komanda komanda za povećanje broja dretvi
     */
    public synchronized static void PovecajMaksimalniBrojDretvi(String komanda) {
        try {
            int broj = Integer.parseInt(komanda.substring(6));
            ServerAviona.maxBrojDretvi += broj;
            odgovorServera = "OK;";
        } catch (Exception ex) {
            odgovorServera = "ERROR 12; Broj dretvi se ne može povećati!";
        }

    }
    /**
     * Funkcija provjerava ispravnost komande kraj za gasenje servera
     * @param komanda komanda kraj
     * @return true tocna komanda, false kriva komanda
     */
    public synchronized static boolean ProvjeriIspravnostKomandeKraj(String komanda) {
        boolean tocnostKomande = false;
        String sintaksa = "(KORISNIK [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(LOZINKA [a-z|A-Z|0-9|_|-|!|#|*]{1,50}; )(KRAJ;)";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(komanda);
        tocnostKomande = m.matches();
        if (!tocnostKomande) {
            odgovorServera = "ERROR 02; Sintaksa zahtjeva nije ispravna;";
        }
        return tocnostKomande;
    }
    /**
     * Funkcija koja obraduje parametre komande kraj
     * @param komanda komanda kraj
     * 
     * @return vraća listu parametara
     */
    public synchronized static ArrayList<String> ObradiParametreOpcijeKraj(String komanda) {
        ArrayList<String> lista = new ArrayList<>();
        String[] podaciKorisnika = komanda.split(";");
        String korIme = podaciKorisnika[0].substring(9);
        String lozinka = podaciKorisnika[1].substring(9);
        String naredba = podaciKorisnika[2].substring(1);
        lista.add(korIme);
        lista.add(lozinka);
        lista.add(naredba);
        return lista;
    }
}
