package dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class DBDataSource {

	// 当前活跃的连接
	private AtomicInteger currentActive = new AtomicInteger(0);

	// 空闲连接池
	private Vector<Connection> freePools = new Vector<Connection>();

	private DBConfig config;
	// 正在工作的线程
	private Vector<DBPoolEntry> workPools = new Vector<>();

	private final static DBDataSource dataSource = new DBDataSource();

	// 实现单例
	public static DBDataSource getDataSource() {
		return dataSource;
	}

	private DBDataSource() {
		super();
		// TODO Auto-generated constructor stub
		init();
		checkConnection();
	}

	// 初始化连接池
	private void init() {
		// TODO Auto-generated method stub
		config = new DBConfig();

		try {
			// 加载驱动
			Class.forName(config.getDriver());
			// 初始分配空闲连接
			for (int i = 0; i < config.getInitSize(); i++) {
				Connection connection = createConnection();
				freePools.add(connection);
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 创建连接
	private Connection createConnection() {
		// TODO Auto-generated method stub
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentActive.incrementAndGet(); // 活跃连接加1

		return connection;
	}

	// 获取连接
	public synchronized Connection getConnection() {
		Connection connection = null;
		if (freePools.size() > 0) {
			connection = freePools.get(0);
			freePools.remove(0);
		} else {
			if (currentActive.get() < config.getMaxSize()) {
				connection = createConnection();
			} else {
				try {
					wait(1000);
					return getConnection();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		}

		// 得到工作连接初始工作时间，为了超时
		long userStartTime = System.currentTimeMillis();
		DBPoolEntry dbPoolEntry = new DBPoolEntry(connection, userStartTime);
		workPools.add(dbPoolEntry);
		return connection;

	}

	// 释放连接
	public synchronized void releaseConnection(Connection connection) {
		try {
			if (!connection.isClosed()) {
				freePools.add(connection);
				for (DBPoolEntry dbPoolEntry : workPools) {
					if (dbPoolEntry.getConnection() == connection) {
						workPools.remove(dbPoolEntry);
						break;
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 销毁连接
	public void checkConnection() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("活动数--->"+currentActive);
				System.out.println("空闲对列数--->" + freePools.size());
				System.out.println("工作队列数--->" + workPools.size());
				System.out.println();
				for (DBPoolEntry dbPoolEntry : workPools) {
					long userStartTime = dbPoolEntry.getUserStartTime();
					long currentTime = System.currentTimeMillis();
					if (currentTime - userStartTime >= config.getTimeout()) {
						try {
							// 连接超时，关闭连接
							dbPoolEntry.getConnection().close();
							// 从工作对列里面删除
							workPools.remove(dbPoolEntry);
							// 活跃连接减1
							currentActive.decrementAndGet();
							break;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}, 50L, 1000L);
	}

}
