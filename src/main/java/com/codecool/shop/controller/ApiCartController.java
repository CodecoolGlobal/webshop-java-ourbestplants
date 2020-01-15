package com.codecool.shop.controller;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.ShoppingCartDao;
import com.codecool.shop.dao.implementation.JDBC.ProductDaoJDBC;
import com.codecool.shop.dao.implementation.Mem.ProductDaoMem;
import com.codecool.shop.dao.implementation.Mem.ShoppingCartDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ShoppingCart;
import com.google.gson.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

@WebServlet(urlPatterns = {"/api/cart"})
public class ApiCartController extends CartController {
    private ProductDao productDao = ProductDaoJDBC.getInstance();
    private static final String ID_NAME = "product_id";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuilder buffer = getStringBuilder(req);
        int productId = getProductId(buffer);
        Product product = productDao.find(productId);

        addProductToSession(req, product);
    }

    private void addProductToSession(HttpServletRequest req, Product product) {
        setupShoppingCart(req);
        shoppingCart.add(product);
    }

    private StringBuilder getStringBuilder(HttpServletRequest req) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer;
    }

    private int getProductId(StringBuilder buffer) {

        String data = buffer.toString();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(data);
        String productId = "invalid";

        if (jsonElement.isJsonObject()) {
            JsonObject partialProduct = jsonElement.getAsJsonObject();
            if (partialProduct.has(ID_NAME)) {
                productId = partialProduct.get(ID_NAME).getAsString();
            } else {
                // TODO client error
            }
        } else {
            // TODO client error
        }
        // TODO: try/catch  (client error)
        return Integer.parseInt(productId);
    }
}

