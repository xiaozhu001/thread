package thread;

public class ThreadTest extends Thread{
	volatile int i = 20;
	
	public void run() {
		// TODO Auto-generated method stub
		synchronized (this) {
			while(i > 0) {
				i--;
				System.out.println(i);
			}
		}
	}
	
	public static void main(String[] args) {
		Thread thread = new ThreadTest();
		new Thread(thread).start();
		new Thread(thread).start();
	}

}
