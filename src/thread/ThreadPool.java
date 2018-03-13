package thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadPool<Job extends Runnable> {
	//工作对列最大数
	private int workNum = 10;
	//等待对列数
	private int waitingQueueNum = 5;
	
	//等待队列
	private final LinkedList<Job> jobs = new LinkedList<Job>();
	//工作对列
	private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
	
	
	
	public ThreadPool(int workNum) {
		if(workNum > this.workNum) 
			this.workNum = workNum;
		initThreadPool(this.workNum);
	}
	
	public ThreadPool() {
		initThreadPool(this.workNum);
	}



	private void initThreadPool(int workNum) {
		// TODO Auto-generated method stub
		for(int i=0; i<workNum; i++) {
			Worker worker = new Worker();
			workers.add(worker);
			Thread thread = new Thread(worker);
			thread.start();
		}
	}




	class Worker implements Runnable {
		private volatile Boolean running = true;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(running) {
				Job job = null;
				synchronized (jobs) {
					if(jobs.isEmpty()) {
						try {
							jobs.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							Thread.currentThread().interrupt();
							return;
						}
					}
					job = jobs.removeFirst();
				}
				if(job != null) {
					job.run();
				}
			}
		}
		// 终止该线程
		public void shutdown() {
			running = false;
		}
		
	}
}
