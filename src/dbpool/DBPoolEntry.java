package dbpool;

import java.sql.Connection;

public class DBPoolEntry {
	private Connection connection;
	
	private long userStartTime;


	public DBPoolEntry(Connection connection, long userStartTime) {
		super();
		this.connection = connection;
		this.userStartTime = userStartTime;
	}


	public Connection getConnection() {
		return connection;
	}


	public long getUserStartTime() {
		return userStartTime;
	}
	
	
}
