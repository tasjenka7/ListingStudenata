
public class Nastavnik {
	
	private String jmbg;
	private String ime;
	private String prezime;
	private String roditelj;
	private String datum;
	private int staz;
	private String email;
	private String telefon;
	private String oblast;
	
	Nastavnik(String jmbg, String ime, String prezime, String roditelj, String datum, 
			  int staz, String telefon, String email, String oblast)
	{
		this.jmbg = jmbg;
		this.ime = ime;
		this.prezime = prezime;
		this.roditelj = roditelj;
		this.staz = staz;
		this.datum = datum;
		this.oblast = oblast;
		this.email = email;
		this.telefon = telefon;
	}
	
	public String getJmbg() {
		return jmbg;
	}
	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}
	public String getIme() {
		return ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getPrezime() {
		return prezime;
	}
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
	public String getRoditelj() {
		return roditelj;
	}
	public void setRoditelj(String roditelj) {
		this.roditelj = roditelj;
	}
	public int getStaz() {
		return staz;
	}
	public void setStaz(int staz) {
		this.staz = staz;
	}
	public String getDatum() {
		return datum;
	}
	public void setDatum(String datum) {
		this.datum = datum;
	}
	public String getOblast() {
		return oblast;
	}
	public void setOblast(String oblast) {
		this.oblast = oblast;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	
	public String toString() {
		return jmbg + " " + ime + " " + prezime + " " + roditelj + " " +
			   datum + " " + staz + " " + email + " " + telefon + " " + oblast;
	}
	
}
