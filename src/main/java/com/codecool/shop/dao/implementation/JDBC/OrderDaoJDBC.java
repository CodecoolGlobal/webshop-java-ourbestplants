package com.codecool.shop.dao.implementation.JDBC;

import com.codecool.shop.dao.LocationDao;
import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.dao.ShoppingCartDao;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.orderStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDaoJDBC extends JDBC implements OrderDao {

    private static OrderDaoJDBC instance = null;
    LocationDao locationDao = LocationDaoJDBC.getInstance();
    ShoppingCartDao shoppingCartDao = ShoppingCartJDBC.getInstance();

    private OrderDaoJDBC() {
    }

    public static OrderDaoJDBC getInstance() {
        if (instance == null) {
            instance = new OrderDaoJDBC();
        }
        return instance;
    }

    @Override
    public int add(Order order) {
        ResultSet resultSet = null;
        String query = "INSERT INTO user_order " +
                "(name, email, phone_number, billing_address_id, shipping_address_id, order_status) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, order.getName());
            statement.setString(2, order.getEmail());
            statement.setString(3, order.getPhone());
            statement.setInt(4, order.getBillingAddress().getId());
            statement.setInt(5, order.getShippingAddress().getId());
            statement.setString(6, order.getOrderStatus().toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public Order find(int id) {

        String query = "SELECT * FROM user_order WHERE id = ? ;";
        ResultSet resultSet = null;
        Order result = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Order(
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        locationDao.find(resultSet.getInt("billing_adress_id")),
                        locationDao.find(resultSet.getInt("shipping_adress_id")),
                        shoppingCartDao.findByOrderId(id),
                        orderStatus.valueOf(resultSet.getString("order_status")));
                result.setId(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public List<Order> getAll() {
        return null;
    }
}
