package Controller;

import Service.AccountService;
import Service.MessageService;
import java.util.List;
import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;
import DAO.MessageDAO;

public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);
        app.get("/accounts/{username}", this::getAccountHandler);
        
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{messageId}", this::getMessageByIdHandler);
        app.patch("/messages/{messageId}", this::updateMessageTextHandler);
        app.delete("/messages/{messageId}", this::deleteMessageHandler);
        app.get("/accounts/{userId}/messages", this::getAllMessagesForUserHandler);

        return app;
    }

    /**
     * Handles user login requests.
     */
    private void loginUserHandler(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            Account loggedInAccount = accountService.login(account);
            if (loggedInAccount != null) {
                context.status(200).json(loggedInAccount);
            } else {
                context.status(401).result("");
            }
        } catch (Exception e) {
            context.status(401).json("");
        }
    }

    /**
     * Handles user registration requests.
     */
    private void registerUserHandler(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            Account createdAccount = accountService.registerUser(account);
            context.status(200).json(createdAccount);
        } catch (IllegalArgumentException e) {
            context.status(400).result("");
        } catch (Exception e) {
            context.status(400).result("");
        }
    }

    /**
     * Handles creating a new message.
     */
    private void createMessageHandler(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            Message createdMessage = messageService.createMessage(message);
            context.status(200).json(createdMessage);
        } catch (Exception e) {
            context.status(400).json("");
        }
    }

    /**
     * Handles deleting a message by its ID.
     */
    private void deleteMessageHandler(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Message deletedMessage = messageService.deleteMessage(messageId);
            if (deletedMessage != null) {
                context.status(200).json(deletedMessage);
            } else {
                context.status(200).result("");
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message ID format");
        } catch (Exception e) {
            context.status(500).result("Internal server error");
        }
    }

    /**
     * Retrieves all messages in the system.
     */
    private void getAllMessagesHandler(Context context) {
        try {
            List<Message> messages = messageService.getAllMessages();
            context.json(messages);
        } catch (Exception e) {
            context.status(500).json("Error fetching messages.");
        }
    }

    /**
     * Retrieves all messages for a given user.
     */
    private void getAllMessagesForUserHandler(Context context) {
        try {
            int userId = Integer.parseInt(context.pathParam("userId"));
            List<Message> messages = messageService.getMessagesByUserId(userId);
            context.json(messages); 
        } catch (NumberFormatException e) {
            context.status(400).json("Invalid user ID format");
        } catch (Exception e) {
            e.printStackTrace();
            context.status(500).json("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Retrieves an account by username.
     */
    private void getAccountHandler(Context context) {
        try {
            String username = context.pathParam("username");
            Account account = accountService.getAccountByUsername(username);
            context.json(account);
        } catch (Exception e) {
            context.status(500).json("Error fetching account.");
        }
    }

    /**
     * Retrieves a message by its ID.
     */
    private void getMessageByIdHandler(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Message message = MessageDAO.getMessageById(messageId);
            if (message == null) {
                context.result("");
            } else {
                context.json(message);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message id");
        } catch (Exception e) {
            e.printStackTrace();
            context.status(500).result("Internal server error");
        }
    }

    /**
     * Updates the text of a message by its ID.
     */
    private void updateMessageTextHandler(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Map<String, String> body = context.bodyAsClass(Map.class);
            String newText = body.get("message_text");

            if (newText == null || newText.trim().isEmpty() || newText.length() >= 255) {
                context.status(400).result("");
                return;
            }

            Message updated = messageService.updateMessageText(messageId, newText);
            if (updated == null) {
                context.status(400).result("");
            } else {
                context.status(200).json(updated);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message id");
        } catch (Exception e) {
            e.printStackTrace();
            context.status(500).result("Internal server error");
        }
    }
}