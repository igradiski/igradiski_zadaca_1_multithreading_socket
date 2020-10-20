package org.foi.nwtis.igradiski.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KorisnikAviona {

    /**
     * Main metoda za korisnika
     * 
     * @param args - prima se komanda koja ce se slati na server
     * komande su -kraj,ispis, dodajDretve i Poletio za avion
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        String sintaksa = "(-k [a-z|A-Z|0-9|_|-]{3,10})?( -l [a-z|A-Z|0-9|!|#|-]{3,10})?( -s (?:[0-9]{1,3}.){3}[0-9]{1,3} | -s [a-z|A-Z|0-9|_|-|.]{3,50} )?(-p ([0-9]{1,5}))?( --kraj| --dodajDretve (([1-9]|1[0-9]|20))| --uzletio [a-z|A-Z|0-9|\"|#|-]{0,1}AerodromPolazište: [a-z|A-Z|0-9|_|-]{3,50}, AerodromOdredište: [a-z|A-Z|0-9|_|-]{3,50}, Avion: [a-z|A-Z|0-9|_|-]{1,50}, trajanjeLeta: [0-9]+[a-z|A-Z|0-9|\"|#|-]{0,1}| --ispis [a-z|A-Z|0-9|_|-]{1,50}) ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
        } else {
            System.out.println("Ne odgovara!");
            return;
        }
        String komanda = ProvjeriParametre(m);
        boolean serverRadi = ProvjeriServer(m.group(5));
        String adresa = m.group(3).substring(4, m.group(3).length() - 1);
        if (serverRadi) {
            PosaljiZahtjevServeru(adresa, m.group(5), komanda);
        }
    }
    /**
     * Funkcija koja provjerava koja je komanda pristigla kroz argumente
     * @param m - komanda koja je dohvacena uz matcher
     * @return  komanda
     */
    public static String ProvjeriParametre(Matcher m) {
        String pocetniParametar = m.group(6);
        String komanda = "";
        if (pocetniParametar.contains(" --kraj")) {
            komanda = ObradiParametreOpcijeKraj(m);
        }
        if (pocetniParametar.contains(" --dodajDretve")) {
            komanda = ObradiParametreOpcijeDodajDretve(m);
        }
        if (pocetniParametar.contains(" --uzletio")) {
            komanda = ObradiParametreOpcijeUzletio(m);
        }
        if (pocetniParametar.contains(" --ispis")) {
            komanda = ObradiParametreOpcijeIspis(m);
        }
        return komanda;
    }

    /**
     * Funkcija koja provjerava parametre komande kraj
     * @param m - komanda koja je dohvacena uz matcher
     * @return komanda
     */
    public static String ObradiParametreOpcijeKraj(Matcher m) {
        String korisnik = m.group(1).substring(3);
        String lozinka = m.group(2).substring(4);
        String komanda = "KORISNIK " + korisnik + ";" + " LOZINKA " + lozinka + ";" + " KRAJ;";
        return komanda;
    }
    /**
     * Funkcija koja provjerava parametre komande Dodaj dretve
     * @param m - komanda koja je dohvacena uz matcher
     * @return komanda
     */
    public static String ObradiParametreOpcijeDodajDretve(Matcher m) {
        String korisnik = m.group(1).substring(3);
        String lozinka = m.group(2).substring(4);
        int brojDretvi = Integer.parseInt(m.group(6).substring(15));
        String komanda = "KORISNIK " + korisnik + ";" + " LOZINKA " + lozinka + ";" + " DODAJ " + brojDretvi + ";";
        return komanda;
    }
    
    /**
     * Funkcija koja provjerava parametre komande ispis
     * @param m - komanda koja je dohvacena uz matcher
     * @return  vraca komandu
     */
    public static String ObradiParametreOpcijeIspis(Matcher m) {
        String korisnik = m.group(1).substring(3);
        String lozinka = m.group(2).substring(4);
        String ispis = m.group(6).substring(9);
        String komanda = "KORISNIK " + korisnik + ";" + " LOZINKA " + lozinka + ";" + " ISPIS " + ispis + ";";
        return komanda;
    }
    /**
     * Funkcija koja provjerava parametre komande Uzletio za avion
     * @param m - komanda koja je dohvacena uz matcher
     * @return komanda
     */
    public static String ObradiParametreOpcijeUzletio(Matcher m) {
        String korisnik = m.group(1).substring(3);
        String lozinka = m.group(2).substring(4);
        String detaljiAviona = m.group(6).substring(10);
        String[] dijeloviNaredbe = detaljiAviona.split(",");
        String polaziste = dijeloviNaredbe[0].substring(20);
        String odrediste = dijeloviNaredbe[1].substring(20);
        String avion = dijeloviNaredbe[2].substring(8);
        String trajanjeLeta = dijeloviNaredbe[3].substring(15);
        String komanda = "KORISNIK " + korisnik + ";" + " LOZINKA " + lozinka 
        + "; " + "UZLETIO " + avion + "; "
        + PripremiKomandu("POLAZIŠTE ") + polaziste + "; " + PripremiKomandu("ODREDIŠTE ") 
        + odrediste + "; " + "TRAJANJE " + trajanjeLeta + ";";
        return komanda;
    }
    /**
     * Funkcija koja provjerava radi li server
     * @param portServera port servera za provjeru
     * @return true server radi , false server ne radi
     */
    public static boolean ProvjeriServer(String portServera) {
        int port = Integer.parseInt(portServera);
        try (var ignored = new ServerSocket(port)) {
            System.out.println("Server trenutno ne radi");
            return false;
        } catch (IOException e) {
            return true;
        }
    }
    
    /**
     * Funkcija koja priprema komandu za slanje u utf-8 formatu
     * @param komanda komanda koja se sprema za slanje
     * @return komanda u utf-8
     */
    public static String PripremiKomandu(String komanda) {
        byte[] b = komanda.getBytes(Charset.forName("UTF-8"));
        String komandaSlanje = "";
        try {
            komandaSlanje = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KorisnikAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return komandaSlanje;
    }
    /**
     * Funkcija koja salje komandu na server aviona
     * @param adresa adresa servera npr. localhost ili 192.186.1.11
     * @param port port na kojemu server slusa
     * @param komanda komanda koja se salje na server
     */
    public static void PosaljiZahtjevServeru(String adresa, String port, String komanda) {
        try {
            Socket veza = new Socket(adresa, Integer.parseInt(port));
            InputStream inps = veza.getInputStream();
            OutputStream outs = veza.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outs);
            osw.write(PripremiKomandu(komanda));
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
            System.out.println(tekst);
            veza.shutdownInput();
            veza.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

