package LAB3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImp implements TransactionDAO {

    @Override
    public boolean CreateTable() throws SQLException {
        String createSql = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id   VARCHAR(200) PRIMARY KEY,
                account_id       VARCHAR(50)  NOT NULL,
                reference_id     VARCHAR(100) NOT NULL,
                transaction_type VARCHAR(50)  NOT NULL,
                amount           DECIMAL(20,4) NOT NULL,
                created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (account_id) REFERENCES accounts(account_id)
            )
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(createSql)) {
            statement.executeUpdate();
            return true;
        }
    }

    @Override
    public List<Transaction> findByAccount(String accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapRow(resultSet));
                }
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapRow(resultSet));
            }
        }
        return transactions;
    }

    // Turns one database row into a Transaction object
    private Transaction mapRow(ResultSet resultSet) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(resultSet.getString("transaction_id"));
        t.setAccountId(resultSet.getString("account_id"));
        t.setReferenceId(resultSet.getString("reference_id"));
        t.setTransactionType(resultSet.getString("transaction_type"));
        t.setAmount(resultSet.getDouble("amount"));
        t.setTimestamp(resultSet.getObject("created_at", LocalDateTime.class));
        return t;
    }

    // Quick test: prints every transaction
    public static void main(String[] args) throws SQLException {
        TransactionDAOImp transactionDAO = new TransactionDAOImp();
        transactionDAO.findAll().forEach(System.out::println);
    }
}
