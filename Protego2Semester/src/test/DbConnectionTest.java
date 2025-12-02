package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.Connection;

import database.DBConnection;

public class DbConnectionTest {

	
	@Test
	public void testGetConnection() {

		try {
			Connection c = DBConnection.getInstance().getConnection();
			assertNotNull(c);
		} catch (Exception e) {
			fail("issues with dbconnection");
		}
	}

}
