package dbpool;

import java.sql.Connection;
import java.util.Random;

public class DBConnectionPool {
	/*
	 * 数据源
	 */
	private static DBDataSource dbDataSource = DBDataSource.getDataSource();

	public static Connection getConnection() {
		return dbDataSource.getConnection();
	}

	public static void close(Connection connection) {
		dbDataSource.releaseConnection(connection);
	}

	public static void main(String[] args) {
		MyThread myThread = new MyThread();

		for (int j = 0; j < 1; j++) {

			for (int i = 0; i < 60; i++) {
				new Thread(myThread).start();
			}
			System.out.println("*----------*");
		}

	}

}

class MyThread implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Connection connection = DBConnectionPool.getConnection();
		try {
			//模拟工作
			Random random = new Random();
			int i = random.nextInt(10);

			Thread.sleep(i * 300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBConnectionPool.close(connection);
	}

}