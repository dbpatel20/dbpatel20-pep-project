package DAO;

import Model.Account;
import Util.ConnectionUtil;
import java.sql.*;

public class AccountDAO {

    /**
     * Retrieves an account by its username.
     * 
     * @param username the username to search for
     * @return the account object if found, otherwise null
     */
    public static Account getAccountByUsername(String username) {
        Account account = null;
        String sql = "SELECT account_id, username, password FROM account WHERE username = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    /**
     * Validates user login credentials.
     * 
     * @param account the account object containing username and password
     * @return the stored account if credentials are valid, otherwise null
     */
    public static Account loginAccount(Account account) {
        Account storedAccount = getAccountByUsername(account.username);
        if (storedAccount != null && storedAccount.password.equals(account.password)) {
            return storedAccount;
        }
        return null;
    }

    /**
     * Creates a new account with an auto-incremented account ID.
     * 
     * @param account the account object to be created
     * @return the created account object with the assigned ID
     */
    public static Account createAccount(Account account) {
        int newId = getMaxAccountId() + 1;
        account.setAccount_id(newId);
        String sql = "INSERT INTO account (account_id, username, password) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setInt(1, newId);
             ps.setString(2, account.username);
             ps.setString(3, account.password);
             ps.executeUpdate();

        } catch (SQLException e) {
             e.printStackTrace();
        }
        return account;
    }

    /**
     * Retrieves the maximum account ID from the database.
     * 
     * @return the maximum account ID or 0 if no records are found
     */
    private static int getMaxAccountId() {
        String sql = "SELECT COALESCE(MAX(account_id), 0) as maxId FROM account";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("maxId");
            }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves an account by its ID.
     * 
     * @param account_id the ID of the account to retrieve
     * @return the account object if found, otherwise null
     */
    public static Account getAccountById(int account_id) {
        String sql = "SELECT account_id, username, password FROM account WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, account_id);
             try (ResultSet rs = ps.executeQuery()){
                 if(rs.next()){
                    return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                 }
             }
        } catch (SQLException e){
             e.printStackTrace();
        }
        return null;
    }
}