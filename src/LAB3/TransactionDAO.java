package LAB3;

import java.sql.SQLException;
import java.util.List;

public interface TransactionDAO {

    boolean CreateTable() throws SQLException;

    // All transactions of one account (newest first)
    List<Transaction> findByAccount(String accountId) throws SQLException;

    List<Transaction> findAll() throws SQLException;
}
