package domain;

import java.util.List;
import java.util.Map;

public class Orders {
    private String orderId, userId, status, orderTime, name, phoneNumber, address;
    private long totalPrice;
    private long ship;
    private long AllTotalPrice;
    private Map<String, CartItem> cartItems;


    public Orders(String orderId, String userId, String status, String orderTime, String name, String phoneNumber, String address, long totalPrice, long ship, long allTotalPrice, Map<String, CartItem> cartItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.orderTime = orderTime;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.totalPrice = totalPrice;
        this.ship = ship;
        AllTotalPrice = allTotalPrice;
        this.cartItems = cartItems;
    }

    public Orders() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<String, CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<String, CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public long getShip() {
        return ship;
    }

    public void setShip(long ship) {
        this.ship = ship;
    }

    public long getAllTotalPrice() {
        return AllTotalPrice;
    }

    public void setAllTotalPrice(long allTotalPrice) {
        AllTotalPrice = allTotalPrice;
    }
}
