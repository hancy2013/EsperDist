package dist.esper.core.worker.pubsub;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import dist.esper.util.Logger2;


public class ProcessingScheduler2 {
	static Logger2 log=Logger2.getLogger(ProcessingScheduler2.class);
	String workerId;
	boolean isSync=false;
	ThreadPoolExecutor pool;
	public int numThreads=1;
	
	public ProcessingScheduler2(String workerId){
		this(workerId, 1);
	}
	public ProcessingScheduler2(String workerId, int numThreads){
		this.workerId = workerId;
		this.numThreads = numThreads;
		pool=(ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
		log.info("Worker %s start ProcessingScheduler2 with numThreads=%d", workerId, numThreads);
	}
	
	public int getQueueSize(){
		return pool.getQueue().size();
	}
	
	public int getNumberThreads(){
		return numThreads;
	}
	
	public Runnable newProcessingRunnable(long subscriberId, String streamName, String[] elementNames, 
			String internalEventTypeName, Object[] events, ISubscriberObserver proc){
		return new ProcessingTask(subscriberId, streamName, elementNames, internalEventTypeName, events, proc);
	}
	
	public void sumbit(long subscriberId, String streamName, String[] elementNames, 
			String internalEventTypeName, Object[] events, ISubscriberObserver proc){
		Runnable task=newProcessingRunnable(subscriberId, streamName, elementNames, internalEventTypeName, events, proc);
		if(isSync){
			task.run();
		}
		else{
			pool.submit(task);
		}
	}
	
	class ProcessingTask implements Runnable{
		long subscriberId;
		String streamName;
		String[] elementNames; 
		String eventTypeName; 
		Object[] events;
		ISubscriberObserver proc;
		
		public ProcessingTask(long subscriberId, String streamName, String[] elementNames,
				String eventTypeName, Object[] events, ISubscriberObserver proc) {
			super();
			this.subscriberId = subscriberId;
			this.streamName = streamName;
			this.elementNames = elementNames;
			this.eventTypeName = eventTypeName;
			this.events = events;
			this.proc = proc;
		}

		@Override
		public void run() {
			proc.updateSubscriberObserver(subscriberId, streamName, elementNames, eventTypeName, events);
		}
	}
}