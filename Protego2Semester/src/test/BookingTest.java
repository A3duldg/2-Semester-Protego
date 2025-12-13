package test;

import java.util.concurrent.*;
import database.DataAccessException;
import database.EmployeeDB;
import controller.EmployeeController;
import model.Employee;
import model.Shift;


 // Simple concurrency test: attempts to book the same shift concurrently from multiple threads.

public class BookingTest {
    public static void main(String[] args) throws Exception {
        //  Configure before run
        final int[] employeeIds = { 3 , 4 }; 
        final int shiftId = 20;
        // ----------------------------------

        final int threads = employeeIds.length;
        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        final EmployeeController employeeController;
        try {
            employeeController = new EmployeeController(new EmployeeDB());
        } catch (DataAccessException e) {
            System.err.println("Failed to init EmployeeController/DB: " + e.getMessage());
            e.printStackTrace();
            exec.shutdownNow();
            return;
        }

        System.out.println("Test configuration: threads=" + threads + ", shiftId=" + shiftId);

        for (int i = 0; i < threads; i++) {
            final int empId = employeeIds[i];
            exec.submit(() -> {
                try {
                    ready.countDown();
                    start.await(); // wait until all threads are ready

                   
                    // uses our shift constructor (start,end,guardAmount,location,availability,shiftId)
                    Shift shift = new Shift(0, 0, 0, "", true, shiftId);

                    // gets employee
                    Employee emp = employeeController.getEmployeeId(empId);
                    if (emp == null) {
                        System.out.println("Thread emp " + empId + ": Employee not found in DB - aborting");
                        return;
                    }
                    final String tname = Thread.currentThread().getName() + "-emp" + empId;
                    
                    System.out.println("[" + tname + "] attempting booking at " + System.currentTimeMillis());
                    long t0 = System.nanoTime();
                    try {
                    	employeeController.connectShiftToEmployee(emp, shift);
                        long tookMs = (System.nanoTime() - t0) / 1_000_000;
                        System.out.println("[" + tname + "] SUCCESS booked shift " + shiftId + " (took " + tookMs + " ms)");
                  
                    } catch (DataAccessException dae) {
                        long tookMs = (System.nanoTime() - t0) / 1_000_000;
                        System.out.println("[" + tname + "] FAIL booking: " + dae.getMessage() + " (took " + tookMs + " ms)");
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted: emp " + empId);
                } catch (Exception e) {
                    System.out.println("ERROR for emp " + empId + ": " + e.getClass().getName() + " " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
        }

        // Wait until all threads are ready, then release them at once
        ready.await();
        System.out.println("All threads ready. Starting concurrent booking attempts...");
        start.countDown();

        // Wait for all tasks to finish
        done.await();
        exec.shutdownNow();
        System.out.println("Test finished.");
    }
}
