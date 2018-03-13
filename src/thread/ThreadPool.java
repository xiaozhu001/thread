package thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadPool<Job extends Runnable> {
	//�������������
	private int workNum = 10;
	//�ȴ�������
	private int waitingQueueNum = 5;
	
	//�ȴ�����
	private final LinkedList<Job> jobs = new LinkedList<Job>();
	//��������
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
		// ��ֹ���߳�
		public void shutdown() {
			running = false;
		}
		
	}
}
