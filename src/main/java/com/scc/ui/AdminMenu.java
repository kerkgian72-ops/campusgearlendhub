package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.user;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void showAdminMenu(user adminUser) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========================================");
            System.out.println("      CAMPUS GEARLEND HUB - ADMIN       ");
            System.out.println("      Welcome, " + adminUser.name() + "!");
            System.out.println("========================================");
            System.out.println("1. Manage Equipment & Inventory");
            System.out.println("2. View All Borrow Requests");
            System.out.println("3. Manage Users / Accounts");
            System.out.println("4. View System Reports");
            System.out.println("5. Update User Role & Status");
            System.out.println("6. Logout");

            int choice = readIntInput("Choose an option: ");

            switch (choice) {
                case 1 -> handleManageEquipment();
                case 2 -> handleViewBorrowRequests();
                case 3 -> handleManageUsers(adminUser);
                case 4 -> handleViewReports();
                case 5 -> handleUpdateUserRoleAndStatus(adminUser);
                case 6 -> {
                    System.out.println("\nLogging out from Admin session...");
                    loggedIn = false;
                }
                default -> System.out.println("\n[!] Invalid choice. Please select between 1 and 6.");
            }
        }
    }

    private static void handleManageEquipment() {
        System.out.println("\n--- Manage Equipment Inventory ---");
        System.out.println("[1] Add New Equipment");
        System.out.println("[2] Update Equipment Status");
        System.out.println("[3] Delete Equipment");
        System.out.println("[4] Back to Admin Menu");
        readIntInput("Select action: ");
        System.out.println("\n[Info] Equipment management module coming soon.");
    }

    private static void handleViewBorrowRequests() {
        System.out.println("\n--- Pending & Active Borrow Requests ---");
        System.out.println("[Info] No active borrow requests to display right now.");
    }

    private static void handleManageUsers(user currentAdmin) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- User Account Management ---");
            System.out.println("[1] View All Registered Users");
            System.out.println("[2] Approve Pending Accounts");
            System.out.println("[3] Back to Admin Menu");

            int action = readIntInput("Select action: ");

            switch (action) {
                case 1 -> {
                    List<user> allUsers = userDao.getAllUsers();
                    renderUserTable(allUsers, "ALL REGISTERED SYSTEM USERS");
                }
                case 2 -> {
                    List<user> pendingUsers = userDao.getPendingUsers();
                    renderUserTable(pendingUsers, "PENDING ACCOUNT REGISTRATIONS");
                    if (!pendingUsers.isEmpty()) {
                        int targetId = readIntInput("Enter User ID to APPROVE (Enter 0 to cancel): ");
                        if (targetId != 0) {
                            boolean success = userDao.approveUser(targetId);
                            if (success) {
                                System.out.println("Success: User ID " + targetId + " status updated to APPROVED.");
                            } else {
                                System.out.println("[!] Failed to update user status.");
                            }
                        } else {
                            System.out.println("Approval action cancelled.");
                        }
                    }
                }
                case 3 -> managing = false;
                default -> System.out.println("\n[!] Invalid action. Choose 1-3.");
            }
        }
    }

    // Input Validation Wrapper: prevents terminal crashes from non-numeric text
    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return value;
            } else {
                String invalidInput = scanner.next(); // consume invalid token
                System.out.println("[!] Invalid numeric input (" + invalidInput + "). Please enter a valid number.");
            }
        }
    }

    // Input Validation Wrapper: ensures role is strictly valid
    private static String readValidRole() {
        while (true) {
            System.out.print("Enter new role (STUDENT, TEACHER, ADMIN): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("STUDENT") || role.equals("TEACHER") || role.equals("ADMIN")) {
                return role;
            }
            System.out.println("[!] Invalid role. Allowed values: STUDENT, TEACHER, ADMIN.");
        }
    }

    // Input Validation Wrapper: ensures status is strictly valid
    private static String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("[!] Invalid status. Allowed values: APPROVED, PENDING.");
        }
    }

    private static void handleUpdateUserRoleAndStatus(user currentAdmin) {
        List<user> allUsers = userDao.getAllUsers();
        renderUserTable(allUsers, "ALL REGISTERED SYSTEM USERS");

        int targetId = readIntInput("Enter Target User ID to update (Enter 0 to cancel): ");
        if (targetId == 0) {
            System.out.println("Update action cancelled.");
            return;
        }

        // Self-Edit Security Restriction
        if (targetId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        // Verify ID exists
        boolean exists = allUsers.stream().anyMatch(u -> u.id() == targetId);
        if (!exists) {
            System.out.println("[!] Error: User ID " + targetId + " does not exist.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        boolean success = userDao.updateUserRoleAndStatus(targetId, newRole, newStatus);
        if (success) {
            System.out.println("Success: User ID " + targetId + " updated to Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("[!] Failed to update user record in database.");
        }
    }

    public static void renderUserTable(List<user> users, String title) {
        System.out.println("\n===========================================================================");
        System.out.println(" " + title);
        System.out.println("===========================================================================");
        if (users.isEmpty()) {
            System.out.println(" No records found.");
            System.out.println("===========================================================================");
            return;
        }

        System.out.printf("%-5s | %-20s | %-20s | %-12s | %-10s\n", "ID", "Name", "Email", "Role", "Status");
        System.out.println("---------------------------------------------------------------------------");
        for (user u : users) {
            System.out.printf("%-5d | %-20s | %-20s | %-12s | %-10s\n",
                    u.id(),
                    u.name(),
                    u.email(),
                    u.role(),
                    u.status()
            );
        }
        System.out.println("===========================================================================");
    }

    private static void handleViewReports() {
        System.out.println("\n--- System Reports & Logs ---");
        System.out.println("[Info] Report generation module coming soon.");
    }
}