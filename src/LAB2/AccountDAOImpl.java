
package LAB2;

import LAB1.Account;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public boolean CreateTable() throws SQLException {

        String createSql = """
            CREATE TABLE accounts (
                account_id VARCHAR(50) PRIMARY KEY,
                customer_id VARCHAR(5) NOT NULL,
                account_type VARCHAR(255) NOT NULL,
                balance DECIMAL(20,5) NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id)
                    REFERENCES customers(customer_id)
            )
            """;

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/IgirePayDb",
                "postgres",
                "Umutoninice&23");
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createSql);
            return true;
        }
    }

    @Override
    public Account registerAccount(Account account) throws SQLException {

        String checkSql = """
            SELECT 1
            FROM customers
            WHERE customer_id = ?
            """;

        String insertSql = """
            INSERT INTO accounts
                (account_id, account_type, balance, created_at, customer_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/IgirePayDb",
                "postgres",
                "Umutoninice&23")) {

            // Check whether the customer exists
            try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                check.setString(1, account.getCustomerId());

                try (ResultSet result = check.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException(
                                "Customer ID " + account.getCustomerId()
                                        + " does not exist in the customers table."
                        );
                    }
                }
            }

            // Insert the account
            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {

                statement.setString(1, account.getAccountId());
                statement.setString(2, account.getAccountType());
                statement.setDouble(3, account.getBalance());
                statement.setObject(4, account.getCreatedAt());
                statement.setString(5, account.getCustomerId());

                statement.executeUpdate();
            }
        }

        return account;
    }

    @Override
    public Account findById(String id) throws SQLException {
        String findAccountSql = "SELECT * FROM accounts WHERE account_id = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/IgirePayDb", "postgres", "Umutoninice&23");
             PreparedStatement statement = connection.prepareStatement(findAccountSql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Account account = new Account();
                    account.setAccountId(resultSet.getString("account_id"));
                    account.setAccountType(resultSet.getString("account_type"));
                    account.setBalance(resultSet.getDouble("balance"));
                    account.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                    account.setCustomerId(resultSet.getString("customer_id"));
                    return account;
                }
            }
        }
        return null;

    }

    @Override
    public List<Account> findAll() throws SQLException {
        String display = "select * from accounts";
        List<Account> accounts = new ArrayList<>();

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirePayDb", "postgres", "Umutoninice&23");
        PreparedStatement statement = connection.prepareStatement(display);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Account account = new Account();
            account.setAccountId(resultSet.getString("account_Id"));
            account.setAccountType(resultSet.getString("account_type"));
            account.setBalance(resultSet.getDouble("balance"));
            account.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
            account.setCustomerId(resultSet.getString("customer_id"));
            accounts.add(account);

            return List.of(account);
        }
        return null;
    }


        @Override
        public boolean updateAccount (Account account) throws SQLException {
            String updateSql = """
            update accounts
            set account_id = ?, account_type = ?, balance= ?,created_at =?
            where customer_Id = ?
            """;

            try (Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/IgirePayDb", "postgres", "Umutoninice&23");
                 PreparedStatement statement = connection.prepareStatement(updateSql)) {

                statement.setString(1, account.getAccountId());
                statement.setString(2, account.getAccountType());
                statement.setDouble(3, account.getBalance());
                statement.setObject(4, account.getCreatedAt());
                statement.setString(5, account.getCustomerId());

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated == 0) {
                    System.out.println("Account " + account.getAccountId() + " is not found");
                    return false;
                }

                System.out.println(" Account" + account.getAccountId() + " updated");
            }
                return true;
            }

        @Override
        public boolean deleteAccount (String id) throws SQLException {

            String deleteSql= "delete from accounts where account_id =?";

            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirePayDb", "postgres", "Umutoninice&23");
            PreparedStatement statement = connection.prepareStatement(deleteSql);

            statement.setString(1, id);
            statement.executeUpdate();

            return true;
        }

    public static void main(String[] args) throws SQLException {
        AccountDAOImpl account = new AccountDAOImpl();


         account.CreateTable();

        //account.registerAccount(new Account("1", "A001", "Saving", "1"));

//        public static void main (String[]args) throws SQLException {
//            AccountDAOImpl accountDAO = new AccountDAOImpl();

            // Already saved; uncomment only with a NEW account id
            // accountDAO.registerAccount(new Account("1", "A001", "Saving", "1"));

            //Account found = accountDAO.findById("1");
//            System.out.println(found != null ? "Found: " + found.getAccountId()
//                    + ", type " + found.getAccountType()
//                    + ", balance " + found.getBalance()
//                    + ", created " + found.getCreatedAt()
//                    : "No account found");

            //System.out.println(accountDAO.findAll());

//            System.out.println(accountDAO.updateAccount(
//                    new Account("1", "c4000", "wallet", 500000, LocalDateTime.of(2026, 10, 27, 9, 48), "1")));
        }
    }
