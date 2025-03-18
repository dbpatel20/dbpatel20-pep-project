package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import java.util.List;

public class MessageService {

    /**
     * Retrieves all messages from the database.
     * 
     * @return a list of all messages
     */
    public List<Message> getAllMessages() {
        return MessageDAO.getAllMessages();
    }

    /**
     * Creates a new message after validating its content and user existence.
     * 
     * @param message the message object to be created
     * @return the newly created message object
     * @throws IllegalArgumentException if message text is blank, too long, or the user does not exist
     */
    public Message createMessage(Message message) {
        if(message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()){
            throw new IllegalArgumentException("Message text cannot be blank");
        }
        if(message.getMessage_text().length() > 255){
            throw new IllegalArgumentException("Message text too long");
        }
        Account account = AccountDAO.getAccountById(message.getPosted_by());
        if(account == null){
            throw new IllegalArgumentException("User not in DB");
        }
        return MessageDAO.createMessage(message);
    }

    /**
     * Deletes a message by its ID.
     * 
     * @param messageId the ID of the message to delete
     * @return the deleted message object if successful, otherwise null
     */
    public Message deleteMessage(int messageId) {
        return MessageDAO.deleteMessage(messageId);
    }

    /**
     * Retrieves all messages posted by a specific user.
     * 
     * @param userId the ID of the user whose messages are to be retrieved
     * @return a list of messages posted by the specified user
     */
    public List<Message> getMessagesByUserId(int userId) {
        return MessageDAO.getMessagesByUserId(userId);
    }

    /**
     * Updates the text of a specific message.
     * 
     * @param messageId the ID of the message to be updated
     * @param newText the new message text
     * @return the updated message object if successful, otherwise null
     */
    public Message updateMessageText(int messageId, String newText) {
        return MessageDAO.updateMessageText(messageId, newText);
    }
}
