/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.iti.charging;

import db.*;
import db.dao.*;
import rating.RatingEngine;
import rating.RatedCDR;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

/**
 *
 * @author XPRISTO
 */
public class Charging {
   
    private static final RatingEngine ratingEngine = new RatingEngine();
    private static final CDRDao cdrDao = new CDRDao();
    private static final RatedCDRDao ratedCDRDao = new RatedCDRDao();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {

         System.out.println("=================================");
        System.out.println("  CDR Rating System v1.0");
        System.out.println("=================================");
        
        try {
            // Test database connection
            System.out.println("Testing database connection...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✓ Database connection successful!");
            conn.close();
            
            // Create required tables
            setupDatabase();
            
            // Show main menu
            showMainMenu();
            
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("Application completed.");
    }
    
    private static void setupDatabase() throws SQLException {
        System.out.println("\nSetting up database tables...");
        ratedCDRDao.createRatedCDRTable();
        System.out.println("✓ Database setup completed!");
    }
    
    private static void showMainMenu() throws SQLException {
        while (true) {
            System.out.println("\n=== CDR Rating System Menu ===");
            System.out.println("1. Insert Sample CDR Data");
            System.out.println("2. Process CDRs (Rating)");
            System.out.println("3. View Rating Statistics");
            System.out.println("4. View All Rated CDRs");
            System.out.println("5. Clear All Rated CDRs");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    insertSampleCDRData();
                    break;
                case 2:
                    processCDRs();
                    break;
                case 3:
                    viewRatingStatistics();
                    break;
                case 4:
                    viewAllRatedCDRs();
                    break;
                case 5:
                    clearAllRatedCDRs();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void insertSampleCDRData() {
        System.out.println("\nInserting sample CDR data...");
        try {
            cdrDao.insertSampleCDRs();
            System.out.println("✓ Sample CDR data inserted successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Error inserting sample data: " + e.getMessage());
        }
    }
    
    private static void processCDRs() {
        System.out.println("\nStarting CDR rating process...");
        try {
            ratingEngine.processUnratedCDRs();
            System.out.println("✓ CDR rating process completed!");
        } catch (SQLException e) {
            System.err.println("❌ Error during CDR rating: " + e.getMessage());
        }
    }
    
    private static void viewRatingStatistics() {
        System.out.println("\nGenerating rating statistics...");
        try {
            ratingEngine.printRatingStatistics();
        } catch (SQLException e) {
            System.err.println("❌ Error generating statistics: " + e.getMessage());
        }
    }
    
    private static void viewAllRatedCDRs() {
        System.out.println("\nFetching all rated CDRs...");
        try {
            List<RatedCDR> ratedCDRs = ratedCDRDao.getAllRatedCDRs();
            
            if (ratedCDRs.isEmpty()) {
                System.out.println("No rated CDRs found.");
                return;
            }
            
            System.out.println("\n=== Rated CDRs ===");
            System.out.printf("%-5s %-15s %-15s %-10s %-10s %-20s %-10s%n",
                    "ID", "Dial A", "Dial B", "Service", "Volume", "Start Time", "Total");
            System.out.println("--------------------------------------------------------------------------------");
            
            for (RatedCDR cdr : ratedCDRs) {
                System.out.printf("%-5d %-15s %-15s %-10s %-10d %-20s $%-9.2f%n",
                        cdr.getId(),
                        cdr.getDialA(),
                        cdr.getDialB() != null ? cdr.getDialB() : "N/A",
                        cdr.getServiceType(),
                        cdr.getVolume(),
                        cdr.getStartTime(),
                        cdr.getTotal().doubleValue());
            }
            
            System.out.println("\nTotal records: " + ratedCDRs.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching rated CDRs: " + e.getMessage());
        }
    }
    
    private static void clearAllRatedCDRs() {
        System.out.println("\nAre you sure you want to clear all rated CDRs? This action cannot be undone.");
        System.out.print("Enter 'YES' to confirm: ");
        
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equals("YES")) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        try {
            ratedCDRDao.clearAllRatedCDRs();
            System.out.println("✓ All rated CDRs have been cleared.");
        } catch (SQLException e) {
            System.err.println("❌ Error clearing rated CDRs: " + e.getMessage());
        }
    }
    
    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
