package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    /**
     * Authenticates a user by verifying the provided account credentials.
     * 
     * @param account the account object containing username and password
     * @return the authenticated account if credentials are valid, otherwise null
     */
    public Account login(Account account) {
        return AccountDAO.loginAccount(account);
    }

    /**
     * Retrieves an account by its username.
     * 
     * @param username the username to search for
     * @return the account object if found, otherwise null
     */
    public Account getAccountByUsername(String username) {
        return AccountDAO.getAccountByUsername(username);
    }

    /**
     * Registers a new account after validating its username and password.
     * 
     * @param account the account object containing username and password
     * @return the newly registered account object
     * @throws IllegalArgumentException if the username is blank, the password is too short, or the username already exists
     */
    public Account registerUser(Account account) {
        if (account.username == null || account.username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (account.password == null || account.password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        Account existing = AccountDAO.getAccountByUsername(account.username);
        if (existing != null) {
             throw new IllegalArgumentException("Duplicate username");
        }
        return AccountDAO.createAccount(account);
    }
}
