package de.uniulm.omi.monitoring;

import java.util.concurrent.BlockingQueue;

public abstract class QueueWorker<T> implements Runnable {

	private BlockingQueue<T> queue;

	public QueueWorker(BlockingQueue<T> queue) {
		this.queue = queue;
	}

	public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                T item = this.queue.take();
                this.consume(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
	}

	abstract protected void consume(T item);

}
