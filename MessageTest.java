package com.mycompany.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // RESET STATIC ARRAYS BEFORE EVERY TEST //
    @BeforeEach
    public void resetStatics() {
        Message.MessageData.totalSent = 0;
        Message.MessageData.sentMessages.clear();
        Message.MessageData.storedMessages.clear();
        Message.MessageData.disregardMessages.clear();
        Message.MessageData.messageHashes.clear();
        Message.MessageData.messageIDs.clear();
    }

    // PART 1 - USERNAME TESTS //

    @Test
    public void testUsernameValid() {
        // kyl_1 HAS UNDERSCORE AND IS 5 CHARS - SHOULD PASS //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkUserName());
    }

    @Test
    public void testUsernameInvalid() {
        // NO UNDERSCORE AND MORE THAN 5 CHARS - SHOULD FAIL //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyle!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.checkUserName());
    }

    // PART 1 - PASSWORD TESTS //

    @Test
    public void testPasswordValid() {
        // Ch&&sec@ke99! HAS CAPITAL, NUMBER, SPECIAL - SHOULD PASS //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testPasswordInvalid() {
        // ALL LOWERCASE, NO NUMBER OR SPECIAL - SHOULD FAIL //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "password", "+27838968976");
        assertFalse(user.checkPasswordComplexity());
    }

    // PART 1 - CELL NUMBER TESTS //

    @Test
    public void testCellValid() {
        // +27838968976 MATCHES +27 FORMAT - SHOULD PASS //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCellInvalid() {
        // 08966553 HAS NO INTERNATIONAL CODE - SHOULD FAIL //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(user.checkCellPhoneNumber());
    }

    // PART 1 - LOGIN TESTS //

    @Test
    public void testLoginSuccess() {
        // CORRECT CREDENTIALS - SHOULD RETURN TRUE //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFail() {
        // WRONG PASSWORD - SHOULD RETURN FALSE //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.loginUser("kyl_1", "wrongpassword"));
    }

    @Test
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
        // WRONG CREDENTIALS - SHOULD RETURN FAILURE MESSAGE //
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(
            "Username or password incorrect, please try again.",
            user.returnLoginStatus("kyl_1", "wrongpassword")
        );
    }

    // PART 2 - MESSAGE LENGTH TESTS //

    @Test
    public void testMessageLengthValid() {
        // SHORT MESSAGE - SHOULD RETURN READY TO SEND //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthTooLong() {
        // 260 CHARS IS 10 OVER THE LIMIT - ERROR SHOULD MENTION 10 //
        String longMsg = "A".repeat(260);
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", longMsg);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters by 10"));
    }

    // PART 2 - RECIPIENT TESTS //

    @Test
    public void testRecipientValid() {
        // VALID +27 NUMBER - SHOULD RETURN SUCCESS //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientInvalid() {
        // NO INTERNATIONAL CODE - SHOULD RETURN FAILURE //
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive the payment?");
        assertTrue(msg.checkRecipientCell().contains("incorrectly formatted or does not contain an international code"));
    }

    // PART 2 - MESSAGE HASH TESTS //

    @Test
    public void testMessageHashUppercase() {
        // HASH MUST BE ALL UPPERCASE //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.getMessageHash();
        assertEquals(hash, hash.toUpperCase());
    }

    @Test
    public void testMessageHashFormat() {
        // HASH MUST CONTAIN THE CORRECT WORD COMBINATION //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.getMessageHash().contains(":1:HITONIGHT?"));
    }

    // PART 2 - SENT MESSAGE OPTION TESTS //

    @Test
    public void testSentMessageSend() {
        // CHOICE 1 - SHOULD RETURN SENT CONFIRMATION //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        // CHOICE 2 - SHOULD RETURN DELETE PROMPT //
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        // CHOICE 3 - SHOULD RETURN STORED CONFIRMATION //
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // PART 2 - TOTAL MESSAGES TEST //

    @Test
    public void testReturnTotalMessages() {
        // TWO MESSAGES SENT - TOTAL SHOULD BE 2 //
        Message.MessageData m1 = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        m1.sentMessage(1);
        Message.MessageData m2 = new Message.MessageData(2, "+27718693002", "Did you get the cake?");
        m2.sentMessage(1);
        assertEquals(2, Message.MessageData.returnTotalMessages());
    }

    // PART 3 TESTS //
    // USES THE 5 PREDEFINED TEST MESSAGES FROM THE ASSIGNMENT SPEC //

    // HELPER - SETS UP THE 5 TEST MESSAGES BEFORE EACH PART 3 TEST //
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

    @Test
    public void testSentMessagesArrayPopulated() {
        // SENT ARRAY SHOULD CONTAIN MSG1 AND MSG4 ONLY //
        setUpPart3TestData();
        boolean hasMsg1 = false, hasMsg4 = false;
        for (Message.MessageData m : Message.MessageData.sentMessages) {
            if (m.getMessageText().equals("Did you get the cake?")) hasMsg1 = true;
            if (m.getMessageText().equals("It is dinner time !"))   hasMsg4 = true;
        }
        assertTrue(hasMsg1 && hasMsg4);
    }

    @Test
    public void testGetLongestMessage() {
        // MSG2 IS THE LONGEST AT 60 CHARS - SHOULD BE RETURNED //
        setUpPart3TestData();
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            Message.MessageData.getLongestMessage()
        );
    }

    @Test
    public void testSearchByMessageID() {
        // SEARCH BY MSG4'S GENERATED ID - SHOULD RETURN MSG4'S TEXT //
        setUpPart3TestData();
        // MSG4 IS AT INDEX 1 IN sentMessages (MSG1 IS INDEX 0) //
        String msg4ID = Message.MessageData.sentMessages.get(1).getMessageID();
        String result = Message.MessageData.searchByMessageID(msg4ID);
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchByRecipient() {
        // +27838884567 HAS MSG2 AND MSG5 - BOTH SHOULD APPEAR //
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