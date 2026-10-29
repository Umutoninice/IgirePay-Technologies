package LAB3;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public boolean CreateTable() throws SQLException {
        String createSql = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_id   VARCHAR(50) PRIMARY KEY,
                customer_id  VARCHAR(50) NOT NULL,
                account_type VARCHAR(255) NOT NULL,
                balance      DECIMAL(20,5) NOT NULL DEFAULT 0,
                created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
            )
            """;

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createSql);
            return true;
        }
    }

    @Override
    public Account registerAccount(Account account) throws SQLException {
        String checkSql = "SELECT 1 FROM customer WHERE customer_id = ?";
        String insertSql = """
            INSERT INTO accounts (account_id, account_type, balance, created_at, customer_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection()) {

            // 1. Make sure the customer exists
            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setString(1, account.getCustomerId());
                try (ResultSet result = check.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Customer ID " + account.getCustomerId()
                                + " does not exist in the customer table.");
                    }
                }
            }

            // 2. Insert the account
            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                statement.setString(1, account.getAccountId());
                statement.setString(2, account.getAccountType());
                statement.setDouble(3, account.getBalance());
                statement.setObject(4, account.getCreatedAt() != null
                        ? account.getCreatedAt() : LocalDateTime.now());
                statement.setString(5, account.getCustomerId());
                statement.executeUpdate();
            }
        }
        return account;
    }

    @Override
    public boolean registerAccount(String accountId, String customerId, String type) throws SQLException {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId(customerId);
        account.setAccountType(type);
        account.setBalance(0);
        account.setCreatedAt(LocalDateTime.now());

        registerAccount(account);   // reuses the method above (customer check + insert)
        return true;
    }

    @Override
    public Account findById(String id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<Account> findByCustomer(String customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at";
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {          // while = read ALL rows
                    accounts.add(mapRow(resultSet));
                }
            }
        }
        return accounts;
    }

    @Override
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY created_at";
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                accounts.add(mapRow(resultSet));
            }
        }
        return accounts;
    }

    @Override
    public boolean updateAccount(Account account) throws SQLException {
        String sql = """
            UPDATE accounts
            SET account_type = ?, balance = ?, customer_id = ?
            WHERE account_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getAccountType());
            statement.setDouble(2, account.getBalance());
            statement.setString(3, account.getCustomerId());
            statement.setString(4, account.getAccountId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Account " + account.getAccountId() + " is not found");
                return false;
            }
            System.out.println("Account " + account.getAccountId() + " updated");
            return true;
        }
    }

    @Override
    public boolean deleteAccount(String id) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public int deleteInactiveAccounts(int days) throws SQLException {
        String sql = """
            DELETE FROM accounts a
            WHERE a.balance = 0
              AND a.created_at < CURRENT_TIMESTAMP - (? * INTERVAL '1 day')
              AND NOT EXISTS (SELECT 1 FROM transactions t WHERE t.account_id = a.account_id)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, days);
            return statement.executeUpdate();
        }
    }

    // Turns one database row into an Account object
    private Account mapRow(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setAccountId(resultSet.getString("account_id"));
        account.setAccountType(resultSet.getString("account_type"));
        account.setBalance(resultSet.getDouble("balance"));
        account.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        account.setCustomerId(resultSet.getString("customer_id"));
        return account;
    }

    // Quick test: prints every account
    public static void main(String[] args) throws SQLException {
        AccountDAOImpl accountDAO = new AccountDAOImpl();
        accountDAO.findAll().forEach(System.out::println);
    }
}
