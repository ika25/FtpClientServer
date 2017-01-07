package ie.gmit.ctrl;

import java.util.concurrent.BlockingQueue;

import ie.gmit.util.Constants;
import ie.gmit.util.FileUtil;

/**
 * The Consumer class holds a shared queue and when ever there is any request
 * put into the queue then it reads that request and ask FileUtil to write the
 * request into that file
 */
public class Consumer implements Runnable {

	private final BlockingQueue<String> sharedQueue;

	/**
	 * This is parameterized construct to construct the Consumer
	 * 
	 * @param sharedQueue
	 *            to be shared by consumer and producer
	 */
	public Consumer(BlockingQueue<String> sharedQueue) {
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void run() {

		while (true) {
			try {
				String queueEntry = sharedQueue.take();

				// Queue is poisoned
				if (Constants.QUEUE_POISONER.equals(queueEntry)) {
					return;
				}

				FileUtil.writeFile(queueEntry);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
