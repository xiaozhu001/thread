package dbpool;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class DBConfig {
	//驱动
	private String driver;
	//地址
	private String url;
	//用户名
	private String username;
	//密码
	private String password;
	//初始大小
	private int initSize = 5;
	//最大大小
	private int maxSize = 10;
	//连接池状态
	private boolean check = true;
	//等待时间
	private long timeout = 1500L;

	public int getInitSize() {
		return initSize;
	}

	public void setInitSize(int initSize) {
		this.initSize = initSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DBConfig() {
		super();
		// TODO Auto-generated constructor stub
		try {
			initDBConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initDBConfig() throws IOException, Exception {
		// TODO Auto-generated method stub
		Properties properties = new Properties();
		try {
			String filePath = this.getClass().getClassLoader().getResource("") + "jdbc.properties";
			int m = filePath.indexOf("/");// file:/<----点位到file:后面的反斜杠  
			filePath = filePath.substring(m + 1);
			InputStream inStream=new BufferedInputStream( new FileInputStream(filePath));  
            properties.load(inStream);
            
			for(Object object : properties.keySet()) {
				Field field = this.getClass().getDeclaredField(object.toString());
				Method method = this.getClass().getMethod(toUpper(object.toString()), field.getType());
				method.invoke(this, properties.get(object));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String toUpper(String string) {
		// TODO Auto-generated method stub
		char[] c = string.toCharArray();
		c[0] -= 32;
		return "set"+new String(c);
	}
}
