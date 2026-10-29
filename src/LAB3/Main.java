package LAB3;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Exercise 3.1 - menu-driven console application. Run this class. */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerDAO customerDAO = new CustomerDAOImp();
    private static final AccountDAO accountDAO = new AccountDAOImpl();
    private static final TransactionDAO transactionDAO = new TransactionDAOImp();
    private static final ProcessedRequestDAO processedRequestDAO = new ProcessedRequestImpl();
    private static final Services wallet = new Services();

    public static void main(String[] args) {
        try {
            createTables();
        } catch (SQLException e) {
            System.out.println("Could not connect to the database: " + e.getMessage());
            return;
        }

        while (true) {
            printMenu();
            String choice = ask("Choose an option");

            try {
                switch (choice) {
                    // Customer management
                    case "1" -> registerCustomer();
                    case "2" -> updateCustomer();
                    case "3" -> viewCustomerAccounts();
                    // Account management
                    case "4" -> createAccount("WALLET");
                    case "5" -> createAccount("SAVING");
                    case "6" -> viewBalance();
                    case "7" -> deleteInactiveAccounts();
                    // Transaction management
                    case "8" -> deposit();
                    case "9" -> withdraw();
                    case "10" -> transfer();
                    case "11" -> viewHistory();
                    case "0" -> {
                        System.out.println("Thank you for using the system. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option, try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Creates the tables only if they do not exist yet (order matters: foreign keys)
    private static void createTables() throws SQLException {
        customerDAO.CreateTable();
        accountDAO.CreateTable();
        processedRequestDAO.CreateTable();
        transactionDAO.CreateTable();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("============ IgirePay ============");
        System.out.println("-- Customer Management --");
        System.out.println(" 1. Register customer");
        System.out.println(" 2. Update customer information");
        System.out.println(" 3. View customer accounts");
        System.out.println("-- Account Management --");
        System.out.println(" 4. Create wallet account");
        System.out.println(" 5. Create savings account");
        System.out.println(" 6. View account balance");
        System.out.println(" 7. Delete inactive accounts");
        System.out.println("-- Transaction Management --");
        System.out.println(" 8. Deposit money");
        System.out.println(" 9. Withdraw money");
        System.out.println("10. Transfer money");
        System.out.println("11. View transaction history");
        System.out.println(" 0. Exit");
    }

    // ===================== CUSTOMER MANAGEMENT =====================

    private static void registerCustomer() throws SQLException {
        String id = ask("Customer ID");
        if (customerDAO.findById(id) != null) {
            System.out.println("A customer with ID " + id + " already exists.");
            return;
        }
        Customer customer = new Customer(id, ask("Full name"), ask("Email"), ask("Phone number"));
        customerDAO.registerCustomer(customer);
        System.out.println("Customer registered: " + customer);
    }

    private static void updateCustomer() throws SQLException {
        Customer customer = customerDAO.findById(ask("Customer ID"));
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.println("Current: " + customer);
        System.out.println("Press Enter to keep the current value.");

        String name = ask("New full name");
        String email = ask("New email");
        String phone = ask("New phone number");
        if (!name.isEmpty())  customer.setCustomerName(name);
        if (!email.isEmpty()) customer.setEmail(email);
        if (!phone.isEmpty()) customer.setPhoneNumber(phone);

        customerDAO.updateCustomer(customer);
        System.out.println("Updated: " + customer);
    }

    private static void viewCustomerAccounts() throws SQLException {
        String id = ask("Customer ID");
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.println(customer);
        List<Account> accounts = accountDAO.findByCustomer(id);
        if (accounts.isEmpty()) {
            System.out.println("   (no accounts)");
            return;
        }
        for (Account account : accounts) {
            System.out.println("   " + account);
        }
    }

    // ===================== ACCOUNT MANAGEMENT =====================

    private static void createAccount(String type) throws SQLException {
        String customerId = ask("Customer ID");
        if (customerDAO.findById(customerId) == null) {
            System.out.println("Customer not found. Register the customer first.");
            return;
        }
        String accountId = ask("New account ID");
        if (accountDAO.findById(accountId) != null) {
            System.out.println("An account with ID " + accountId + " already exists.");
            return;
        }
        accountDAO.registerAccount(accountId, customerId, type);
        System.out.println(type + " account " + accountId + " created with balance 0 RWF.");
    }

    private static void viewBalance() throws SQLException {
        Account account = accountDAO.findById(ask("Account ID"));
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.println("Account " + account.getAccountId() + " (" + account.getAccountType()
                + ") balance: " + account.getBalance() + " RWF");
    }

    private static void deleteInactiveAccounts() throws SQLException {
        System.out.println("Deletes accounts with balance 0, no transactions, older than N days.");
        int days = askInt("N (days)");
        int deleted = accountDAO.deleteInactiveAccounts(days);
        System.out.println(deleted + " inactive account(s) deleted.");
    }

    // ===================== TRANSACTION MANAGEMENT =====================

    private static void deposit() throws SQLException {
        String accountId = ask("Account ID");
        double amount = askAmount();
        String reference = ask("Reference ID (e.g. DEP-001)");
        report(wallet.deposit(accountId, amount, reference), reference);
    }

    private static void withdraw() throws SQLException {
        String accountId = ask("Account ID");
        double amount = askAmount();
        String reference = ask("Reference ID (e.g. WD-001)");
        report(wallet.withdraw(accountId, amount, reference), reference);
    }

    private static void transfer() throws SQLException {
        String from = ask("From account ID");
        String to = ask("To account ID");
        double amount = askAmount();
        String reference = ask("Reference ID (e.g. TRF-001)");
        report(wallet.transfer(from, to, amount, reference), reference);
    }

    private static void viewHistory() throws SQLException {
        Account account = accountDAO.findById(ask("Account ID"));
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.println(account);
        List<Transaction> history = transactionDAO.findByAccount(account.getAccountId());
        if (history.isEmpty()) {
            System.out.println("   (no transactions yet)");
        }
        for (Transaction t : history) {
            System.out.println("   " + t);
        }
    }

    // Prevents duplicates: false means the reference id was already used
    private static void report(boolean processed, String reference) {
        if (processed) {
            System.out.println("Success. Reference: " + reference);
        } else {
            System.out.println("Duplicate request: reference " + reference
                    + " was already processed. Nothing was changed.");
        }
    }

    // ===================== INPUT HELPERS =====================

    private static String ask(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private static int askInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(ask(label));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double askAmount() {
        while (true) {
            try {
                return Double.parseDouble(ask("Amount (RWF)"));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, e.g. 5000");
            }
        }
    }
}
