package com.oceanview.test;

import com.oceanview.test.dao.UserDAOTest;
import com.oceanview.test.service.ReservationServiceTest;
import com.oceanview.test.util.ValidationUtilTest;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test Suite Runner
 * Execute all tests and generate report
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("OCEAN VIEW RESORT - TEST SUITE");
        System.out.println("=================================================\n");
        
        Class<?>[] testClasses = {
            ValidationUtilTest.class,
            UserDAOTest.class,
            ReservationServiceTest.class
        };
        
        int totalTests = 0;
        int totalFailures = 0;
        long totalTime = 0;
        
        for (Class<?> testClass : testClasses) {
            System.out.println("\nRunning: " + testClass.getSimpleName());
            System.out.println("-------------------------------------------------");
            
            Result result = JUnitCore.runClasses(testClass);
            
            totalTests += result.getRunCount();
            totalFailures += result.getFailureCount();
            totalTime += result.getRunTime();
            
            if (result.wasSuccessful()) {
                System.out.println("✓ All tests passed!");
            } else {
                System.out.println("✗ Some tests failed:");
                for (Failure failure : result.getFailures()) {
                    System.out.println("  - " + failure.toString());
                }
            }
            
            System.out.println("Tests run: " + result.getRunCount());
            System.out.println("Failures: " + result.getFailureCount());
            System.out.println("Time: " + result.getRunTime() + "ms");
        }
        
        System.out.println("\n=================================================");
        System.out.println("TEST SUMMARY");
        System.out.println("=================================================");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + (totalTests - totalFailures));
        System.out.println("Failed: " + totalFailures);
        System.out.println("Total Time: " + totalTime + "ms");
        System.out.println("Success Rate: " + 
            String.format("%.2f%%", ((totalTests - totalFailures) * 100.0 / totalTests)));
        System.out.println("=================================================");
        
        System.exit(totalFailures > 0 ? 1 : 0);
    }
}
