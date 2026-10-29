package LAB3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImp implements CustomerDAO {

    @Override
    public boolean CreateTable() throws SQLException {
        String createSql = """
            CREATE TABLE IF NOT EXISTS customer (
                customer_id   VARCHAR(50) PRIMARY KEY,
                customer_name VARCHAR(255) NOT NULL,
                email         VARCHAR(255) NOT NULL,
                phone_number  VARCHAR(50)  NOT NULL
            )
            """;

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createSql);
            return true;
        }
    }

    @Override
    public Customer registerCustomer(Customer customer) throws SQLException {
        String sql = """
            INSERT INTO customer (customer_id, customer_name, email, phone_number)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, customer.getCustomerId());
            st.setString(2, customer.getCustomerName());
            st.setString(3, customer.getEmail());
            st.setString(4, customer.getPhoneNumber());
            st.executeUpdate();
        }
        return customer;
    }

    @Override
    public Customer findById(String id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";

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
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customer";
        List<Customer> allCustomers = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                allCustomers.add(mapRow(resultSet));
            }
        }
        return allCustomers;
    }

    @Override
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = """
            UPDATE customer
            SET customer_name = ?, email = ?, phone_number = ?
            WHERE customer_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhoneNumber());
            statement.setString(4, customer.getCustomerId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Customer " + customer.getCustomerId() + " is not found");
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean deleteCustomer(String id) throws SQLException {
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    // Turns one database row into a Customer object
    private Customer mapRow(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(resultSet.getString("customer_id"));
        customer.setCustomerName(resultSet.getString("customer_name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhoneNumber(resultSet.getString("phone_number"));
        return customer;
    }

    // Quick test: prints every customer
    public static void main(String[] args) throws SQLException {
        CustomerDAOImp customerDAO = new CustomerDAOImp();
        customerDAO.findAll().forEach(System.out::println);
    }
}
