import java.util.ArrayList;

public class ProducerConsumerLab {

    static class SharedBuffer {
        private ArrayList<Integer> buffer = new ArrayList<>();
        private int capacity;

        public SharedBuffer(int capacity) {
            this.capacity = capacity;
            System.out.println("Buffer created with capacity: " + capacity);
        }

        public synchronized void produce(int value) throws InterruptedException {
            while (buffer.size() >= capacity) {
                System.out.println("[Producer] Buffer FULL - waiting...");
                wait();
            }
            buffer.add(value);
            System.out.println("[Producer] Produced: " + value + " | Buffer: " + buffer);
            notifyAll();
        }

        public synchronized int consume() throws InterruptedException {
            while (buffer.isEmpty()) {
                System.out.println("[Consumer] Buffer EMPTY - waiting...");
                wait();
            }
            int value = buffer.remove(0);
            System.out.println("[Consumer] Consumed: " + value + " | Buffer: " + buffer);
            notifyAll();
            return value;
        }
    }

    static class Producer implements Runnable {
        private SharedBuffer buffer;

        public Producer(SharedBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.produce(i);
                }
                System.out.println("[Producer] finished producing 10 items");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer implements Runnable {
        private SharedBuffer buffer;

        public Consumer(SharedBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.consume();
                }
                System.out.println("[Consumer] finished consuming 10 items");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5);

        Thread producerThread = new Thread(new Producer(buffer));
        Thread consumerThread = new Thread(new Consumer(buffer));

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nAll threads completed successfully!");
    }
}
