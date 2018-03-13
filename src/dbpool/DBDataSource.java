package dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class DBDataSource {

	// ��ǰ��Ծ������
	private AtomicInteger currentActive = new AtomicInteger(0);

	// �������ӳ�
	private Vector<Connection> freePools = new Vector<Connection>();

	private DBConfig config;
	// ���ڹ������߳�
	private Vector<DBPoolEntry> workPools = new Vector<>();

	private final static DBDataSource dataSource = new DBDataSource();

	// ʵ�ֵ���
	public static DBDataSource getDataSource() {
		return dataSource;
	}

	private DBDataSource() {
		super();
		// TODO Auto-generated constructor stub
		init();
		checkConnection();
	}

	// ��ʼ�����ӳ�
	private void init() {
		// TODO Auto-generated method stub
		config = new DBConfig();

		try {
			// ��������
			Class.forName(config.getDriver());
			// ��ʼ�����������
			for (int i = 0; i < config.getInitSize(); i++) {
				Connection connection = createConnection();
				freePools.add(connection);
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ��������
	private Connection createConnection() {
		// TODO Auto-generated method stub
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentActive.incrementAndGet(); // ��Ծ���Ӽ�1

		return connection;
	}

	// ��ȡ����
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

		// �õ��������ӳ�ʼ����ʱ�䣬Ϊ�˳�ʱ
		long userStartTime = System.currentTimeMillis();
		DBPoolEntry dbPoolEntry = new DBPoolEntry(connection, userStartTime);
		workPools.add(dbPoolEntry);
		return connection;

	}

	// �ͷ�����
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

	// ��������
	public void checkConnection() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("���--->"+currentActive);
				System.out.println("���ж�����--->" + freePools.size());
				System.out.println("����������--->" + workPools.size());
				System.out.println();
				for (DBPoolEntry dbPoolEntry : workPools) {
					long userStartTime = dbPoolEntry.getUserStartTime();
					long currentTime = System.currentTimeMillis();
					if (currentTime - userStartTime >= config.getTimeout()) {
						try {
							// ���ӳ�ʱ���ر�����
							dbPoolEntry.getConnection().close();
							// �ӹ�����������ɾ��
							workPools.remove(dbPoolEntry);
							// ��Ծ���Ӽ�1
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
