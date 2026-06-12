package com.mycompany.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    
    public void resetStatics() {
        Message.MessageData.totalSent = 0;
        Message.MessageData.sentMessages.clear();
        Message.MessageData.storedMessages.clear();
        Message.MessageData.disregardMessages.clear();
        Message.MessageData.messageHashes.clear();
        Message.MessageData.messageIDs.clear();
    }

    

    public void testUsernameValid() {
    
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkUserName());
    }

    
    public void testUsernameInvalid() {
        
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyle!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.checkUserName());
    }


    public void testPasswordValid() {
        
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkPasswordComplexity());
    }

    
    public void testPasswordInvalid() {
    
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "password", "+27838968976");
        assertFalse(user.checkPasswordComplexity());
    }

    public void testCellValid() {

        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkCellPhoneNumber());
    }


    public void testCellInvalid() {
    
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(user.checkCellPhoneNumber());
    }

    
    public void testLoginSuccess() {
        
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    
    public void testLoginFail() {
        
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.loginUser("kyl_1", "wrongpassword"));
    }

    public void testLoginStatusSuccess() {
        // CORRECT CREDENTIALS - SHOULD RETURN WELCOME MESSAGE //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(
            "Welcome Kyle Jackson, it is great to see you again!",
            user.returnLoginStatus("kyl_1", "Ch&&sec@ke99!")
        );
    }

    @Test
    public void testLoginStatusFail() {
        
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(
            "Username or password incorrect, please try again.",
            user.returnLoginStatus("kyl_1", "wrongpassword")
        );
    }


    public void testMessageLengthValid() {
    
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    public void testMessageLengthTooLong() {
        
        String longMsg = "A".repeat(260);
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", longMsg);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters by 10"));
    }
  
    public void testRecipientValid() {
        
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    public void testRecipientInvalid() {
        // NO INTERNATIONAL CODE - SHOULD RETURN FAILURE //
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive the payment?");
        assertTrue(msg.checkRecipientCell().contains("incorrectly formatted or does not contain an international code"));
    }

    public void testMessageHashUppercase() {
        
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.getMessageHash();
        assertEquals(hash, hash.toUpperCase());
    }
    
    
    public void testMessageHashFormat() {
        
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.getMessageHash().contains(":1:HITONIGHT?"));
    }


    public void testSentMessageSend() {
        
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    public void testSentMessageDisregard() {
    
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    public void testSentMessageStore() {
        
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    
    public void testReturnTotalMessages() {
        
        Message.MessageData m1 = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        m1.sentMessage(1);
        Message.MessageData m2 = new Message.MessageData(2, "+27718693002", "Did you get the cake?");
        m2.sentMessage(1);
        assertEquals(2, Message.MessageData.returnTotalMessages());
    }

    private void setUpPart3TestData() {
        Message.MessageData msg1 = new Message.MessageData(1, "+27834557896", "Did you get the cake?");
        msg1.sentMessage(1); // SENT //

        Message.MessageData msg2 = new Message.MessageData(2, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3); // STORED //

        Message.MessageData msg3 = new Message.MessageData(3, "+27834484567", "Yohoooo, I am at your gate.");
        msg3.sentMessage(2); // DISREGARDED //

        Message.MessageData msg4 = new Message.MessageData(4, "0838884567", "It is dinner time !");
        msg4.sentMessage(1); // SENT //

        Message.MessageData msg5 = new Message.MessageData(5, "+27838884567", "Ok, I am leaving without you.");
        msg5.sentMessage(3); // STORED //
    }

    
    public void testSentMessagesArrayPopulated() {
    
        setUpPart3TestData();
        boolean hasMsg1 = false, hasMsg4 = false;
        for (Message.MessageData m : Message.MessageData.sentMessages) {
            if (m.getMessageText().equals("Did you get the cake?")) hasMsg1 = true;
            if (m.getMessageText().equals("It is dinner time !"))   hasMsg4 = true;
        }
        assertTrue(hasMsg1 && hasMsg4);
    }

    
    public void testGetLongestMessage() {
        setUpPart3TestData();
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            Message.MessageData.getLongestMessage()
        );
    }

    
    public void testSearchByMessageID() {
        
        setUpPart3TestData();
        String msg4ID = Message.MessageData.sentMessages.get(1).getMessageID();
        String result = Message.MessageData.searchByMessageID(msg4ID);
        assertTrue(result.contains("It is dinner time !"));
    }

    public void testSearchByRecipient() {
        setUpPart3TestData();
        String result = Message.MessageData.searchByRecipient("+27838884567");
        assertTrue(
            result.contains("Where are you? You are late! I have asked you to be on time.")
            && result.contains("Ok, I am leaving without you.")
        );
    }

    @Test
    public void testDeleteByHash() {
        // DELETE MSG2 BY HASH - SHOULD RETURN SUCCESS WITH MSG2 TEXT //
        setUpPart3TestData();
        // MSG2 IS FIRST IN storedMessages (INDEX 0) //
        String msg2Hash = Message.MessageData.storedMessages.get(0).getMessageHash();
        String result   = Message.MessageData.deleteByHash(msg2Hash);
        assertTrue(
            result.contains("Where are you? You are late! I have asked you to be on time.")
            && result.contains("successfully deleted")
        );
    }

    @Test
    public void testDisplayReport() {
        // REPORT SHOULD LIST ALL SENT AND STORED MESSAGES WITH HASH AND RECIPIENT //
        setUpPart3TestData();
        String report = Message.MessageData.displayReport();
        assertTrue(
            report.contains("Did you get the cake?")
            && report.contains("It is dinner time !")
            && report.contains("Message Hash")
            && report.contains("Recipient")
        );
    }
}
