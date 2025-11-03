# Lab 6: Thread Synchronization

#### Learning Objectives
- Understand multithreading and threading libraries in Java
- Implement threads using `Runnable` interface
- Master thread synchronization with locks (`synchronized`) and conditions (`wait()`/`notifyAll()`)

#### Prerequisites
- Basic Java programming knowledge
- Understanding of classes and interfaces
- Familiarity with ArrayLists

#### Introduction

Concurrency is everywhere in modern computing. Every time you stream a video, the player produces frames while displaying them simultaneously. When you download a file, one thread fetches data while another writes it to disk. Understanding how to coordinate multiple threads accessing shared resources is fundamental to building robust applications.

This lab explores thread synchronization through the classic Producer-Consumer problem. You'll implement a bounded buffer where producer threads generate data and consumer threads process it. The challenge isn't the data processing - it's coordinating multiple threads so they don't step on each other's toes.

Think of it like a small bakery. Bakers (producers) make bread and put it on a limited shelf space (buffer). Customers (consumers) take bread from the shelf. If the shelf is full, bakers must wait. If it's empty, customers must wait. Without proper coordination, chaos ensues - bread gets dropped, customers grab the same loaf, inventory becomes incorrect.

#### What You'll Implement

Complete 2 TODO tasks in one file:

1. TODO 1: Implement the `Producer` class - generates 10 numbers and adds them to the buffer
2. TODO 2: Implement the `Consumer` class - retrieves 10 numbers from the buffer and displays them

The `SharedBuffer` and `main()` method are fully provided.

#### Lab File Structure

ProducerConsumerLab.java - The only file you need to complete

- `SharedBuffer` class - Fully provided (handles synchronization)
- `Producer` class - TODO 1 (you implement this)
- `Consumer` class - TODO 2 (you implement this)
- `main()` method - Fully provided (creates and runs threads)

#### Project Setup

1. Download ProducerConsumerLab.java
2. Complete the 2 TODOs
3. Compile: `javac ProducerConsumerLab.java`
4. Run: `java ProducerConsumerLab`

#### The Methods You'll Implement

#### Producer Class

The Producer class represents a thread that generates data. In this lab, it produces 10 sequential numbers (0 through 9) and adds them to the shared buffer.

Your Producer class must:
- Implement the `Runnable` interface
- Have a constructor that accepts a `SharedBuffer` parameter
- Implement the `run()` method with a try-catch block for `InterruptedException`
- Use a loop to produce 10 numbers
- Call `buffer.produce(i)` for each number
- Print a completion message when finished

#### Consumer Class

The Consumer class represents a thread that processes data. In this lab, it retrieves 10 numbers from the shared buffer.

Your Consumer class must:
- Implement the `Runnable` interface
- Have a constructor that accepts a `SharedBuffer` parameter
- Implement the `run()` method with a try-catch block for `InterruptedException`
- Use a loop to consume 10 numbers
- Call `buffer.consume()` for each number
- Print a completion message when finished

#### Expected Output

```
Buffer created with capacity: 5

[Producer] Produced: 0 | Buffer: [0]
[Producer] Produced: 1 | Buffer: [0, 1]
[Consumer] Consumed: 0 | Buffer: [1]
[Producer] Produced: 2 | Buffer: [1, 2]
[Producer] Produced: 3 | Buffer: [1, 2, 3]
[Consumer] Consumed: 1 | Buffer: [2, 3]
[Producer] Produced: 4 | Buffer: [2, 3, 4]
[Producer] Produced: 5 | Buffer: [2, 3, 4, 5]
[Producer] Produced: 6 | Buffer: [2, 3, 4, 5, 6]
[Producer] Buffer FULL - waiting...
[Consumer] Consumed: 2 | Buffer: [3, 4, 5, 6]
[Producer] Produced: 7 | Buffer: [3, 4, 5, 6, 7]
[Producer] Buffer FULL - waiting...
[Consumer] Consumed: 3 | Buffer: [4, 5, 6, 7]
[Producer] Produced: 8 | Buffer: [4, 5, 6, 7, 8]
[Producer] Buffer FULL - waiting...
[Consumer] Consumed: 4 | Buffer: [5, 6, 7, 8]
[Producer] Produced: 9 | Buffer: [5, 6, 7, 8, 9]
[Producer] finished producing 10 items
[Consumer] Consumed: 5 | Buffer: [6, 7, 8, 9]
[Consumer] Consumed: 6 | Buffer: [7, 8, 9]
[Consumer] Consumed: 7 | Buffer: [8, 9]
[Consumer] Consumed: 8 | Buffer: [9]
[Consumer] Consumed: 9 | Buffer: []
[Consumer] finished consuming 10 items

All threads completed successfully!
```

#### Understanding Key Concepts

#### The synchronized Keyword

The `synchronized` keyword ensures that only one thread can execute a method at a time. When you mark a method as `synchronized`, Java automatically locks the object, preventing other threads from entering any synchronized method on that same object.

Think of `synchronized` as a velvet rope at a nightclub. Only one person (thread) gets through at a time. Everyone else waits in line.

```java
public synchronized void produce(int value) {
    // Only one thread can be here at a time
    // All other threads wait outside
}
```

#### The wait() Method

When a thread calls `wait()`, two things happen: it releases the lock and goes to sleep until someone wakes it up. This is crucial because if `wait()` didn't release the lock, the system would deadlock - everyone would be stuck waiting forever.

You must always use `wait()` in a `while` loop, not an `if` statement. When a thread wakes up, it must re-check the condition because another thread might have changed things while it was asleep.

```java
while (buffer.size() >= capacity) {
    wait(); // Release lock and sleep
}
// When we wake up, we automatically re-acquire the lock
```

#### The notifyAll() Method

When you call `notifyAll()`, you wake up all threads that are waiting on this object. They don't immediately run - they go back to competing for the lock. One will win, re-acquire the lock, and continue from where it called `wait()`.

We use `notifyAll()` instead of `notify()` because `notify()` wakes up just one random thread. This can cause problems - you might wake up a producer when you need to wake up a consumer. Using `notifyAll()` is safer: wake everyone up and let them figure out who should run.

```java
buffer.add(value);
notifyAll(); // Wake up all waiting threads
```

#### Handling InterruptedException

When using `wait()`, you must handle `InterruptedException`. This exception occurs when a thread is interrupted while waiting. You should always catch and handle it appropriately.

```java
try {
    buffer.produce(i);
} catch (InterruptedException e) {
    System.out.println("Thread interrupted");
}
```

#### Code Template

```java
import java.util.ArrayList;

public class ProducerConsumerLab {
    
    /**
     * SharedBuffer - Thread-safe bounded buffer (FULLY PROVIDED)
     */
    static class SharedBuffer {
        private ArrayList<Integer> buffer;
        private int capacity;
        
        public SharedBuffer(int capacity) {
            this.buffer = new ArrayList<>();
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
    
    /**
     * TODO 1: Implement Producer class
     * 
     * Create a class that implements Runnable and:
     * 1. Has a SharedBuffer field and constructor parameter
     * 2. In run() method:
     *    - Use try-catch for InterruptedException
     *    - Loop 10 times (i from 0 to 9)
     *    - Call buffer.produce(i) each iteration
     *    - Print "[Producer] finished producing 10 items" when done
     *    - In catch block, print "[Producer] was interrupted"
     */
    static class Producer implements Runnable {
        // TODO 1: Implement Producer class here
        
    }
    
    /**
     * TODO 2: Implement Consumer class
     * 
     * Create a class that implements Runnable and:
     * 1. Has a SharedBuffer field and constructor parameter
     * 2. In run() method:
     *    - Use try-catch for InterruptedException
     *    - Loop 10 times
     *    - Call buffer.consume() each iteration
     *    - Print "[Consumer] finished consuming 10 items" when done
     *    - In catch block, print "[Consumer] was interrupted"
     */
    static class Consumer implements Runnable {
        // TODO 2: Implement Consumer class here
        
    }
    
    /**
     * Main method (FULLY PROVIDED)
     */
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5);
        
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        System.out.println();
        producerThread.start();
        consumerThread.start();
        
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
            return;
        }
        
        System.out.println("\nAll threads completed successfully!");
    }
}
```

#### Implementation Guide

#### TODO 1: Producer Class

```java
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
            System.out.println("[Producer] was interrupted");
        }
    }
}
```

#### TODO 2: Consumer Class

```java
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
            System.out.println("[Consumer] was interrupted");
        }
    }
}
```

#### Common Mistakes to Avoid

#### Using if Instead of while

Using `if` instead of `while` for wait conditions is the most common mistake. When a thread wakes from `wait()`, it must re-check the condition. Using `if` means it only checks once, leading to subtle bugs where threads proceed when they shouldn't.

```java
// WRONG - don't do this
if (buffer.isEmpty()) {
    wait();
}

// CORRECT
while (buffer.isEmpty()) {
    wait();
}
```

#### Forgetting to Handle InterruptedException

Not handling `InterruptedException` properly can mask problems. When a thread is interrupted, it's usually for a good reason (shutdown, error, etc.). Always catch and handle it appropriately with a try-catch block.

#### Not Understanding Thread Lifecycle

Remember that calling `start()` on a thread runs the `run()` method in a new thread. If you call `run()` directly, it executes in the current thread, defeating the purpose of multithreading.

#### Analysis Questions

After completing the lab, think about these questions:

1. What would happen if you used `notify()` instead of `notifyAll()`? Why might this cause problems?

notifyAll() is used because it wakes up all waiting threads. If we only used notify(), there's a chance we might wake up the wrong thread. For example, the program could wake another producer when the buffer is already full. Then the consumer would stay asleep and the program could get stuck.

2. Why must `wait()` be called inside a `while` loop rather than an `if` statement? What problem does this solve?

wait() should be in a while loop because a thread might wake up even when the condition is not ready. The loop checks the condition again before continuing. This prevents mistakes and makes the program safe from unexpected wake ups.

3. What would happen if the `produce()` method wasn't synchronized? Could two producers add items at the same time?

If these methods are not synchronized more than one thread could change the buffer at the same time. This can cause errors, like taking from an empty buffer or adding too many items. Synchronization protects the shared data so only one thread changes it at a time.

4. What happens if you forget to call `notifyAll()` in the `produce()` method? How would this affect the consumer thread?

If notifyAll() is missing some threads might stay waiting forever. Even when the buffer changes, they would not wake up. This makes the program freeze and never finish.

5. Why does `wait()` release the lock? What would happen if it didn't?

wait() releases the lock so another thread can use the shared buffer. That way the other thread can produce or consume and change the condition. If wait() did not release the lock then there would be no progress that could be made, and the program could get stuck.

#### What You're Really Learning

The number-adding in this lab is just busy work to give your threads something to do. The real learning is understanding how to coordinate multiple threads accessing shared resources.

This is the foundation of concurrent programming. Every time you write a server that handles multiple clients, you're using these patterns. Every time you build a data pipeline that processes information in stages, you're using producer-consumer. Every time you optimize a program to use multiple cores, you need synchronization.

The specific problem - producers making numbers, consumers reading them - doesn't matter. What matters is learning how `synchronized`, `wait()`, and `notifyAll()` work together to prevent chaos when multiple threads access shared data.

These patterns appear everywhere: GUI event queues, thread pools, task schedulers, message brokers, streaming data processors. Master them here with simple integers, and you can apply them to any concurrent system.

#### Compilation and Execution

```bash
javac ProducerConsumerLab.java
java ProducerConsumerLab
```

#### Submission Requirements

After completing your work:

```bash
git add .
git commit -m "completed lab 4 - thread synchronization"
git push origin main
```

Include:
- Completed `Producer` and `Consumer` classes
- Screenshot showing successful execution with proper synchronization
- Verify that all 10 items are produced and consumed
- Ensure threads complete without deadlocking
