package LAB3;

import java.sql.SQLException;
import java.util.List;

public interface AccountDAO {

    boolean CreateTable() throws SQLException;

    Account registerAccount(Account account) throws SQLException;

    // Used by Main: creates a new account with balance 0
    boolean registerAccount(String accountId, String customerId, String type) throws SQLException;

    Account findById(String id) throws SQLException;

    List<Account> findAll() throws SQLException;

    List<Account> findByCustomer(String customerId) throws SQLException;

    boolean updateAccount(Account account) throws SQLException;

    boolean deleteAccount(String id) throws SQLException;

    // Deletes accounts with balance 0, no transactions, older than N days
    int deleteInactiveAccounts(int days) throws SQLException;
}
