package com.oceanview.test;

import com.oceanview.test.dao.*;
import com.oceanview.test.service.*;
import com.oceanview.test.util.*;
import com.oceanview.test.model.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Complete Test Suite Runner
 * Executes all tests and generates comprehensive report
 */
public class CompleteTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   OCEAN VIEW RESORT - COMPREHENSIVE TEST SUITE");
        System.out.println("=======================================================");
        System.out.println("Test Execution Started: " + 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("=======================================================\n");
        
        Class<?>[] testClasses = {
            // Utility Tests
            ValidationUtilTest.class,
            PasswordUtilTest.class,
            
            // Model Tests
            ModelValidationTest.class,
            
            // DAO Tests
            UserDAOTest.class,
            GuestDAOTest.class,
            
            // Service Tests
            ReservationServiceTest.class
        };
        
        int totalTests = 0;
        int totalFailures = 0;
        int totalSkipped = 0;
        long totalTime = 0;
        
        StringBuilder detailedReport = new StringBuilder();
        
        for (Class<?> testClass : testClasses) {
            System.out.println("\n┌─────────────────────────────────────────────────────┐");
            System.out.println("│ Running: " + String.format("%-43s", testClass.getSimpleName()) + " │");
            System.out.println("└─────────────────────────────────────────────────────┘");
            
            Result result = JUnitCore.runClasses(testClass);
            
            totalTests += result.getRunCount();
            totalFailures += result.getFailureCount();
            totalSkipped += result.getIgnoreCount();
            totalTime += result.getRunTime();
            
            // Print results
            if (result.wasSuccessful()) {
                System.out.println("✓ Status: ALL TESTS PASSED");
            } else {
                System.out.println("✗ Status: SOME TESTS FAILED");
                for (Failure failure : result.getFailures()) {
                    System.out.println("  ✗ " + failure.getTestHeader());
                    System.out.println("    " + failure.getMessage());
                }
            }
            
            System.out.println("  Tests Run: " + result.getRunCount());
            System.out.println("  Passed: " + (result.getRunCount() - result.getFailureCount()));
            System.out.println("  Failed: " + result.getFailureCount());
            System.out.println("  Ignored: " + result.getIgnoreCount());
            System.out.println("  Time: " + result.getRunTime() + "ms");
            
            // Build detailed report
            detailedReport.append(String.format("%-30s | %4d | %4d | %4d | %6dms%n",
                testClass.getSimpleName(),
                result.getRunCount(),
                (result.getRunCount() - result.getFailureCount()),
                result.getFailureCount(),
                result.getRunTime()));
        }
        
        // Print summary
        System.out.println("\n\n=======================================================");
        System.out.println("                    TEST SUMMARY");
        System.out.println("=======================================================");
        System.out.println(String.format("Total Test Classes:      %d", testClasses.length));
        System.out.println(String.format("Total Tests Run:         %d", totalTests));
        System.out.println(String.format("Tests Passed:            %d", (totalTests - totalFailures)));
        System.out.println(String.format("Tests Failed:            %d", totalFailures));
        System.out.println(String.format("Tests Skipped:           %d", totalSkipped));
        System.out.println(String.format("Total Execution Time:    %dms (%.2fs)", totalTime, totalTime / 1000.0));
        
        double successRate = totalTests > 0 ? ((totalTests - totalFailures) * 100.0 / totalTests) : 0;
        System.out.println(String.format("Success Rate:            %.2f%%", successRate));
        
        System.out.println("=======================================================");
        
        // Print detailed breakdown
        System.out.println("\n                DETAILED BREAKDOWN");
        System.out.println("=======================================================");
        System.out.println(String.format("%-30s | %4s | %4s | %4s | %8s",
            "Test Class", "Run", "Pass", "Fail", "Time"));
        System.out.println("-------------------------------------------------------");
        System.out.print(detailedReport.toString());
        System.out.println("=======================================================");
        
        // Final result
        System.out.println("\n=======================================================");
        if (totalFailures == 0) {
            System.out.println("           ✓✓✓ ALL TESTS PASSED ✓✓✓");
            System.out.println("    System is ready for deployment!");
        } else {
            System.out.println("           ✗✗✗ SOME TESTS FAILED ✗✗✗");
            System.out.println("    Please fix failing tests before deployment!");
        }
        System.out.println("=======================================================");
        System.out.println("Test Execution Completed: " + 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("=======================================================\n");
        
        // Exit with appropriate code
        System.exit(totalFailures > 0 ? 1 : 0);
    }
}
