
import java.sql.*;


public class Database {
	private String URL = "jdbc:oracle:thin:@localhost:1522:ORACLE";
	private String username = "hr";
	private String password = "hr";
	private Connection con = null;
	private Statement stat = null;
	private PreparedStatement prstat = null;
	
	private Database() {
		if(con == null) {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection(URL,username,password);
				stat = con.createStatement();
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private static Database database = null;
	
	public static Database getDatabase() {
		if(database == null) {
			database = new Database();
		}
		return database;
	}
	
	public Connection getConnection() {
		return this.con;
	}
	
	public Statement getstatment() {
		return this.stat;
	}
	
	public PreparedStatement getPreparedS(String query) {
		try {
				prstat = con.prepareStatement(query);
			} catch (SQLException e) {
				System.out.println(e);
			}
		return this.prstat;
	}
	
	public void closeDatabase() {
		try {
			con.close();
			stat.close();
			prstat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
