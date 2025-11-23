package Database;
import java.sql.*;

public class DBConnection {
	   
	public static void main(String[] args) {
	        String hostname = "localhost";
	        String database = "dit_database_navn";
	        String login = "dit_brugernavn";
	        String password = "dit_password";
	        
	        String url = "jdbc:mysql://" + hostname + ":1408/" + database;
	        
	        try {
	            Connection con = DriverManager.getConnection(url, login, password);
	            System.out.println("Forbindelse til database succesfuld");
	        } catch (SQLException e) {
	            System.out.println("Fejl ved forbindelse: " + e.getMessage());
	        }
	    }
	}