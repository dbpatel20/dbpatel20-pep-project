package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Retrieves all messages from the database.
     * 
     * @return a list of all messages
     */
    public static List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Message m = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Creates a new message entry in the database.
     * 
     * @param message the message object to be created
     * @return the created message object
     */
    public static Message createMessage(Message message) {
        int newId = getMaxMessageId() + 1;
        message.setMessage_id(newId);
        String sql = "INSERT INTO message (message_id, posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, newId);
             ps.setInt(2, message.getPosted_by());
             ps.setString(3, message.getMessage_text());
             ps.setLong(4, message.getTime_posted_epoch());
             ps.executeUpdate();
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return message;
    }

    /**
     * Retrieves the maximum message ID from the database.
     * 
     * @return the maximum message ID or 0 if no records are found
     */
    private static int getMaxMessageId() {
        String sql = "SELECT COALESCE(MAX(message_id), 0) AS maxId FROM message";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
             if(rs.next()){
                 return rs.getInt("maxId");
             }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deletes a message from the database by its ID.
     * 
     * @param messageId the ID of the message to be deleted
     * @return the deleted message object if found, otherwise null
     */
    public static Message deleteMessage(int messageId) {
        Message deleted = getMessageById(messageId);
        if (deleted != null) {
            String sql = "DELETE FROM message WHERE message_id = ?";
            try (Connection conn = ConnectionUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, messageId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deleted;
    }

    /**
     * Retrieves a message from the database by its ID.
     * 
     * @param messageId the ID of the message to retrieve
     * @return the message object if found, otherwise null
     */
    public static Message getMessageById(int messageId) {
        Message message = null;
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Retrieves all messages posted by a specific user.
     * 
     * @param userId the ID of the user whose messages are to be retrieved
     * @return a list of messages posted by the user
     */
    public static List<Message> getMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, userId);
             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     Message m = new Message(
                         rs.getInt("message_id"),
                         rs.getInt("posted_by"),
                         rs.getString("message_text"),
                         rs.getLong("time_posted_epoch")
                     );
                     messages.add(m);
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Updates the text content of a specific message.
     * 
     * @param messageId the ID of the message to be updated
     * @param newText the new message text
     * @return the updated message object if found, otherwise null
     */
    public static Message updateMessageText(int messageId, String newText) {
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, newText);
             ps.setInt(2, messageId);
             int rowsUpdated = ps.executeUpdate();
             if (rowsUpdated > 0) {
                 return getMessageById(messageId);
             }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return null;
    }
}
