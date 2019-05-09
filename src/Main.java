import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;



public class Main extends JFrame {
	
	JPanel panUnesi = new JPanel();
	JPanel panAzuriraj = new JPanel();
	
	// Mapa za polja u panelu 1 koji je za unos novog
	Map<String, JTextField> mapUnesi = new HashMap();
	// Mapa za polja u panelu 2 koji je za azuriranje postojeceg
	Map<String, JTextField> mapAzuriraj = new HashMap();
	// Mapa sa poljima za novi panel koji se otvara za selektovanog nastavnika
	Map<String, JTextField> mapNovi = new HashMap();
	
	
	
	
	// Select box za odabir nastavnika
	JComboBox<String> nastavnici = new JComboBox<>();
	
	// Glavni panel u koji se smestaju preostali paneli
	JTabbedPane tabPane = new JTabbedPane();
	
	
	ArrayList<Nastavnik> nizNastavnici = new ArrayList<Nastavnik>();
	
	Connection con;
	final String dbUrl = "jdbc:db2://localhost:50001/vstud";
	final String unesiSql = "INSERT INTO nastavnici VALUES(?,?,?,?,?,?,?,?,?)";
	
	
	static {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Main() {
		
		super("Menadzer Nastavnika v1.0");
		
		initPanel1();
		initPanel2();
		
		tabPane.add("Dodaj profesora", panUnesi);
		tabPane.add("Azuriraj profesora", panAzuriraj);
		
		
		add(tabPane);
		
		try {
			con = DriverManager.getConnection(dbUrl, "student", "abcdef");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initPanel1() {
		
		JButton btnUnesiUnesi = new JButton("Unesi");
		JButton btnUnesiOcisti = new JButton("Ocisti");
		
		panUnesi.setLayout(new GridBagLayout());
		panUnesi.setBorder(new EmptyBorder(10, 20, 10, 10));
		GridBagConstraints gbc = new GridBagConstraints();
		
		mapUnesi.put("jmbg", new JTextField());
		mapUnesi.put("ime", new JTextField());
		mapUnesi.put("prezime", new JTextField());
		mapUnesi.put("roditelj", new JTextField());
		mapUnesi.put("datum", new JTextField());
		mapUnesi.put("staz", new JTextField());
		mapUnesi.put("email", new JTextField());
		mapUnesi.put("telefon", new JTextField());
		mapUnesi.put("oblast", new JTextField());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		
		panUnesi.add(new JLabel("JMBG: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Ime: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Prezime: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Roditelj: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Datum stupanja: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Staz: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Email: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Telefon: "), gbc);
		gbc.gridy++;
		
		panUnesi.add(new JLabel("Oblast interesovanja: "), gbc);
		gbc.gridy++;
		
		// Logika upisivanja novog nastavnika u bazu
		
		btnUnesiUnesi.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				PreparedStatement ps;
				String greska = "";
				java.sql.Date datum = null;
				try {
					// Kreiramo datum objekat
					// Oduzimamo ove vrednosti jer datum ne valja
					String[] parts = mapUnesi.get("datum").getText().split("[.]");
					try {
						datum = new java.sql.Date(Integer.parseInt(parts[2])-1900,
												  Integer.parseInt(parts[1])-1,
												  Integer.parseInt(parts[0]));
					} catch (Exception e1) {}
					
					
					ps = con.prepareStatement(unesiSql);
					ps.setString(1, mapUnesi.get("jmbg").getText());
					ps.setString(2, mapUnesi.get("ime").getText());
					ps.setString(3, mapUnesi.get("prezime").getText());
					ps.setString(4, mapUnesi.get("roditelj").getText());
					ps.setDate(5, datum);
					ps.setString(6, mapUnesi.get("staz").getText());
					ps.setString(7, mapUnesi.get("email").getText());
					ps.setString(8, mapUnesi.get("telefon").getText());
					ps.setString(9, mapUnesi.get("oblast").getText());
					
					
					greska = proveriPolja(mapUnesi.get("jmbg").getText(),
												 mapUnesi.get("ime").getText(),
												 mapUnesi.get("prezime").getText(),
												 mapUnesi.get("roditelj").getText(),
												 datum,
												 mapUnesi.get("staz").getText(),
												 mapUnesi.get("email").getText(),
												 mapUnesi.get("telefon").getText(),
												 mapUnesi.get("oblast").getText());
					
					if (!greska.equals("")) {
						System.out.println(greska);
					} else {
						try {
							ps.executeUpdate();
						} catch (SQLException ex) {
							if (ex.getErrorCode() == -911 || ex.getErrorCode() == -913) {
								obradiGresku();
							}
						}
						
						// Cistimo polja nakon izvrsenog unosa
						for (Map.Entry<String, JTextField> entry : mapUnesi.entrySet()) {
							entry.getValue().setText("");
						}
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
		});
		panUnesi.add(btnUnesiUnesi, gbc);
		
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 4;
		gbc.weighty = 1;
		
		
		panUnesi.add(mapUnesi.get("jmbg"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("ime"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("prezime"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("roditelj"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("datum"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("staz"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("email"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("telefon"), gbc);
		gbc.gridy++;
		
		panUnesi.add(mapUnesi.get("oblast"), gbc);
		gbc.gridy++;
		
		btnUnesiOcisti.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (Map.Entry<String, JTextField> entry : mapUnesi.entrySet()) {
					entry.getValue().setText("");
				}
			}
			
		});
		
		panUnesi.add(btnUnesiOcisti, gbc);
	}
	
	private void initPanel2() {
		
		JButton btnTrazi = new JButton("Pretraga");
		
		panAzuriraj.setLayout(new BorderLayout());
		panAzuriraj.setBorder(new EmptyBorder(10, 20, 10, 10));
		mapAzuriraj.clear(); // cistimo mapu da bismo inicijalizovali ponovo panel
		
		JPanel top = new JPanel(new GridBagLayout());
		top.setPreferredSize(new Dimension(640, 100));
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		mapAzuriraj.put("jmbg", new JTextField());
		mapAzuriraj.put("ime", new JTextField());
		mapAzuriraj.put("prezime", new JTextField());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		
		top.add(new JLabel("JMBG: "), gbc);
		gbc.gridy++;
		
		top.add(new JLabel("Ime: "), gbc);
		gbc.gridy++;
		
		top.add(new JLabel("Prezime: "), gbc);
		gbc.gridy++;
		
		
		btnTrazi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nastavnici.removeAllItems();
				nizNastavnici.clear();
				nizNastavnici.add(null); // Da prvi selektovan bude null
				// Da se ne bi odmah otvaralo prvo ime u novom panelu
				PreparedStatement ps;
				String sql = "";
				try {
					if (!mapAzuriraj.get("jmbg").getText().equals("")) {
						sql = "SELECT jmbg, ime, prezime, roditelj, stupio_datum, "+
							  " staz, email, telefon, oblastint " +
							  "FROM nastavnici " +
							  "WHERE jmbg = ?";
						ps = con.prepareStatement(sql);
						ps.setString(1, mapAzuriraj.get("jmbg").getText());
					} else {
						sql = "SELECT jmbg, ime, prezime, roditelj, stupio_datum, "+
							  " staz, email, telefon, oblastint " +
							  "FROM nastavnici " +
							  "WHERE (ime = ? and prezime = ?) OR ime = ? or prezime = ?";
						ps = con.prepareStatement(sql);
						ps.setString(1, mapAzuriraj.get("ime").getText());
						ps.setString(2, mapAzuriraj.get("prezime").getText());
						ps.setString(3, mapAzuriraj.get("ime").getText());
						ps.setString(4, mapAzuriraj.get("prezime").getText());
					} 
					
					ResultSet rs = ps.executeQuery();
					
					while(rs.next()) {
						Nastavnik n = new Nastavnik(rs.getString("jmbg").trim(), rs.getString("ime").trim(), rs.getString("prezime").trim(),
													rs.getString("roditelj").trim(), rs.getDate("stupio_datum").toString().trim(),
													rs.getInt("staz"), rs.getString("telefon").trim(),
													rs.getString("email").trim(), rs.getString("oblastint").trim());
						
						nizNastavnici.add(n);
					}
					if (nizNastavnici.size() == 0) {
						nastavnici.addItem("Nema nastavnika");
						return;
					}
				} catch (SQLException ex) {
					if (ex.getErrorCode() == -911 || ex.getErrorCode() == -913) {
						obradiGresku();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				nastavnici.addItem((nizNastavnici.size()-1) + " nastavnik(a) ");
				for (Nastavnik n : nizNastavnici) {
					if (n != null) {
						nastavnici.addItem(n.getIme()+" "+n.getPrezime());
					}
				}
			}
			
		});
		
		top.add(btnTrazi, gbc);
		
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 3;
		gbc.weighty = 1;
		
		top.add(mapAzuriraj.get("jmbg"), gbc);
		gbc.gridy++;
		
		top.add(mapAzuriraj.get("ime"), gbc);
		gbc.gridy++;
		
		top.add(mapAzuriraj.get("prezime"), gbc);
		gbc.gridy++;
		
		
		
		nastavnici.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int state = e.getStateChange();
				
				if (state == ItemEvent.SELECTED) {
					
					try {
						
						Nastavnik n = nizNastavnici.get(nastavnici.getSelectedIndex());
						// automatski se selektuje prvi, sto smo podesili da bude null
						// te se tako odmah izlazi iz ovoga
						if (n == null)
							return;
						JPanel novi = getNewPanel(n);
						tabPane.add(n.getIme()+" "+n.getPrezime(), novi);
					} catch (Exception ex) {
						
					}
				}
			}
			
		});
		
		top.add(nastavnici, gbc);
		
		panAzuriraj.add(top, BorderLayout.NORTH);
		
	}
	
	private JPanel getNewPanel(final Nastavnik n) {
		JPanel novi = new JPanel();
		JButton btnAzuriraj = new JButton("Azuriraj");
		final JLabel lblGreska = new JLabel();
		lblGreska.setForeground(Color.RED);
		
		novi.setLayout(new GridBagLayout());
		mapNovi.clear();
		GridBagConstraints gbc = new GridBagConstraints();
		
		JTextField jmbg = new JTextField(n.getJmbg());
		jmbg.setEditable(false);
		
		mapNovi.put("jmbg", jmbg);
		mapNovi.put("ime", new JTextField(n.getIme()));
		mapNovi.put("prezime", new JTextField(n.getPrezime()));
		mapNovi.put("roditelj", new JTextField(n.getRoditelj()));
		mapNovi.put("datum", new JTextField(n.getDatum()));
		mapNovi.put("staz", new JTextField(""+n.getStaz()));
		mapNovi.put("email", new JTextField(n.getEmail()));
		mapNovi.put("telefon", new JTextField(n.getTelefon()));
		mapNovi.put("oblast", new JTextField(n.getOblast()));
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		
		novi.add(new JLabel("JMBG: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Ime: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Prezime: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Roditelj: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Datum stupanja: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Staz: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Email: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Telefon: "), gbc);
		gbc.gridy++;
		
		novi.add(new JLabel("Oblast interesovanja: "), gbc);
		gbc.gridy++;
		
		btnAzuriraj.addActionListener(new ActionListener() {

			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				java.sql.Date datum = null;
				try {
					String sql = "UPDATE nastavnici SET ime=?, " +
					"prezime=?, roditelj=?, stupio_datum=?, staz=?, " +
					"email=?, telefon=?, oblastint=? " +
					"WHERE jmbg=?";
					PreparedStatement ps = con.prepareStatement(sql);
					
					String[] parts = mapNovi.get("datum").getText().split("[-]");
					try {
						datum = new java.sql.Date(Integer.parseInt(parts[0])-1900,
																Integer.parseInt(parts[1])-1,
																Integer.parseInt(parts[2]));
					} catch (Exception e1) {}
					
					
					ps.setString(9, mapNovi.get("jmbg").getText());
					ps.setString(1, mapNovi.get("ime").getText());
					ps.setString(2, mapNovi.get("prezime").getText());
					ps.setString(3, mapNovi.get("roditelj").getText());
					ps.setDate(4, datum);
					ps.setString(5, mapNovi.get("staz").getText());
					ps.setString(6, mapNovi.get("email").getText());
					ps.setString(7, mapNovi.get("telefon").getText());
					ps.setString(8, mapNovi.get("oblast").getText());
					
					
					
					String greska = proveriPolja(mapNovi.get("jmbg").getText(),
												 mapNovi.get("ime").getText(),
												 mapNovi.get("prezime").getText(),
												 mapNovi.get("roditelj").getText(),
												 datum,
												 mapNovi.get("staz").getText(),
												 mapNovi.get("email").getText(),
												 mapNovi.get("telefon").getText(),
												 mapNovi.get("oblast").getText());
					
					if (!greska.equals("")) {
						lblGreska.setText(greska);
						System.out.println(greska);
					} else {
						ps.executeUpdate();
						ps.close();
						
						nizNastavnici.clear();
						nastavnici.removeAllItems();
						tabPane.remove(2);
					}
					
				} catch (SQLException ex) {
					if (ex.getErrorCode() == -911 || ex.getErrorCode() == -913)
						obradiGresku();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
		});
		
		novi.add(btnAzuriraj, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 4;
		gbc.weighty = 1;
		
		
		novi.add(mapNovi.get("jmbg"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("ime"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("prezime"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("roditelj"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("datum"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("staz"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("email"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("telefon"), gbc);
		gbc.gridy++;
		
		novi.add(mapNovi.get("oblast"), gbc);
		gbc.gridy++;
		
		novi.add(lblGreska, gbc);
		
		
		return novi;
	}
	@SuppressWarnings("deprecated")
	private String proveriPolja(String jmbg, String ime, String prezime, 
						 String roditelj, java.sql.Date datum, 
						 String staz, String email, String telefon, 
						 String oblast)
	{
		String info = "";
		
		if(jmbg.matches("[a-z]+") || jmbg.length() != 13 || jmbg.isEmpty())
	        info += "Pogresan unos jmbg\n";
	    if(ime.isEmpty() || ime.length() > 20)
	        info += "Pogresan unos ime \n";
	    if(prezime.isEmpty() || prezime.length() > 20)
	        info += "Pogresan unos prezime\n";
	    if(roditelj.isEmpty() || roditelj.length() > 20)
	        info += "Pogresan unos roditelj\n";
	    if(datum == null)
	        info+= "Pogredsan unos datum\n";
	    if(staz.matches("[a-z]+") || staz.isEmpty())
	        info+= "Pogresan unos staz\n";
	    if(email.isEmpty() || !email.matches("[a-z0-9]+@[a-z]*.[a-z]+") || email.length() > 50)
	        info += "Pogresan unos email\n";
	    if(telefon.matches("[a-z]+") || telefon.isEmpty() || telefon.length()>20)
	        info += "Pogresan unos telefon\n";
	    if(oblast.isEmpty() || oblast.length() > 50)
	        info += "Pogresan unos oblast\n";
	    
	    return info;
	}
	private static void obradiGresku() {
		System.out.println("Obradio sam gresku");
	}
	
	public static void main(String[] args) {
		Main app = new Main();
		app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		app.setSize(480, 320);
		app.setResizable(false);
		app.setVisible(true);
		
	}
}
