package com.mycompany.message;



import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Message {

    // LOGIN CLASS //
    static class Login {

        // USER DETAILS //
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String cellPhoneNumber;

        // CONSTRUCTOR //
        public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
        }

        // USERNAME CHECK //
        public boolean checkUserName() {
            return username.contains("_") && username.length() <= 5;
        }

        // PASSWORD COMPLEXITY CHECK //
        public boolean checkPasswordComplexity() {
            if (password.length() < 8) return false;
            boolean hasCapital = false, hasNumber = false, hasSpecial = false;
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasCapital = true;
                else if (Character.isDigit(c)) hasNumber = true;
                else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            }
            return hasCapital && hasNumber && hasSpecial;
        }

        // CELL NUMBER CHECK //
        // Regex sourced and adapted from: https://www.baeldung.com/java-regex-validate-phone-numbers
        public boolean checkCellPhoneNumber() {
            return cellPhoneNumber.matches("^\\+27\\d{9}$");
        }

        // LOGIN CHECK //
        public boolean loginUser(String enteredUsername, String enteredPassword) {
            return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
        }

        // LOGIN STATUS MESSAGE //
        public String returnLoginStatus(String enteredUsername, String enteredPassword) {
            if (loginUser(enteredUsername, enteredPassword)) {
                return "Welcome " + firstName + " " + lastName + ", it is great to see you again!";
            } else {
                return "Username or password incorrect, please try again.";
            }
        }

        // GETTERS //
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    // MESSAGE DATA CLASS //
    static class MessageData {

        // MESSAGE FIELDS //
        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        private String flag;

        // SHARED ARRAYS - FILLED BY USER ACTIONS, NOT HARD-CODED //
        static int totalSent = 0;
        static ArrayList<MessageData> sentMessages      = new ArrayList<>();
        static ArrayList<MessageData> storedMessages    = new ArrayList<>();
        static ArrayList<MessageData> disregardMessages = new ArrayList<>();
        static ArrayList<String>      messageHashes     = new ArrayList<>();
        static ArrayList<String>      messageIDs        = new ArrayList<>();

        // CONSTRUCTOR - USED WHEN CREATING A NEW MESSAGE //
        public MessageData(int messageNumber, String recipient, String messageText) {
            this.messageNumber = messageNumber;
            this.recipient     = recipient;
            this.messageText   = messageText;
            this.messageID     = generateMessageID();
            this.messageHash   = createMessageHash();
        }

        // CONSTRUCTOR - USED ONLY WHEN LOADING FROM JSON 
        private MessageData(String messageID, String recipient, String messageText,
                            String messageHash, String flag, int messageNumber) {
            this.messageID     = messageID;
            this.recipient     = recipient;
            this.messageText   = messageText;
            this.messageHash   = messageHash;
            this.flag          = flag;
            this.messageNumber = messageNumber;
        }

        // GENERATE RANDOM 10-DIGIT MESSAGE ID //
        private String generateMessageID() {
            Random rand = new Random();
            long id = (long)(rand.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
            return String.valueOf(id);
        }

        // MESSAGE ID LENGTH CHECK 
        public boolean checkMessageID() {
            return messageID.length() <= 10;
        }

        // RECIPIENT CELL NUMBER CHECK 
        public String checkRecipientCell() {
            if (recipient.startsWith("+") && recipient.length() <= 13) {
                return "Cell phone number successfully captured.";
            } else {
                return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
            }
        }

        // BUILD MESSAGE HASH FROM ID PREFIX, MESSAGE NUMBER, FIRST AND LAST WORD 
        public String createMessageHash() {
            String[] words   = messageText.trim().split("\\s+");
            String firstWord = words[0];
            String lastWord  = words[words.length - 1];
            String idPrefix  = messageID.substring(0, Math.min(2, messageID.length()));
            return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        }

        // MESSAGE LENGTH CHECK - MAX 250 CHARACTERS 
        public String checkMessageLength() {
            if (messageText.length() <= 250) {
                return "Message ready to send.";
            } else {
                int over = messageText.length() - 250;
                return "Message exceeds 250 characters by " + over + "; please reduce the size.";
            }
        }

        // SEND, DISREGARD, OR STORE THE MESSAGE 
        public String sentMessage(int choice) {
            switch (choice) {
                case 1:
                    flag = "Sent";
                    totalSent++;
                    sentMessages.add(this);
                    messageHashes.add(messageHash);
                    messageIDs.add(messageID);
                    return "Message successfully sent.";
                case 2:
                    flag = "Disregarded";
                    disregardMessages.add(this);
                    return "Press 0 to delete the message.";
                case 3:
                    flag = "Stored";
                    storedMessages.add(this);
                    messageHashes.add(messageHash);
                    messageIDs.add(messageID);
                    return "Message successfully stored.";
                default:
                    return "Invalid option.";
            }
        }

        // PRINT ALL SENT MESSAGES 
        public static String printMessages() {
            if (sentMessages.isEmpty()) return "No messages sent.";
            StringBuilder sb = new StringBuilder();
            for (MessageData m : sentMessages) {
                sb.append("Message ID: ").append(m.messageID)
                  .append(" | Hash: ").append(m.messageHash)
                  .append(" | Recipient: ").append(m.recipient)
                  .append(" | Message: ").append(m.messageText)
                  .append("\n");
            }
            return sb.toString();
        }

        // RETURN TOTAL MESSAGES SENT
        public static int returnTotalMessages() {
            return totalSent;
        }

        // SAVE STORED MESSAGES TO JSON FILE 
        
        public static void storeMessage(String filename) {
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                MessageData m = storedMessages.get(i);
                json.append("  {\n");
                json.append("    \"messageID\": \"").append(m.messageID).append("\",\n");
                json.append("    \"messageHash\": \"").append(m.messageHash).append("\",\n");
                json.append("    \"recipient\": \"").append(m.recipient).append("\",\n");
                json.append("    \"message\": \"").append(m.messageText).append("\",\n");
                json.append("    \"flag\": \"").append(m.flag).append("\"\n");
                json.append("  }");
                if (i < storedMessages.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("]");
            try (FileWriter fw = new FileWriter(filename)) {
                fw.write(json.toString());
                System.out.println("Messages successfully stored to " + filename);
            } catch (IOException e) {
                System.out.println("Error writing JSON file: " + e.getMessage());
            }
        }

        // LOAD STORED MESSAGES FROM JSON AT SESSION START
        
        public static void loadStoredMessages(String filename) {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line.trim());
                }
                String json = content.toString().trim();
                if (json.length() < 2) return;

                // STRIP OUTER BRACKETS 
                json = json.substring(1, json.length() - 1).trim();
                if (json.isEmpty()) return;

                String[] objects = json.split("\\},\\s*\\{");
                int count = storedMessages.size() + 1;
                for (String obj : objects) {
                    obj = obj.replace("{", "").replace("}", "");
                    String msgID     = extractJsonValue(obj, "messageID");
                    String msgHash   = extractJsonValue(obj, "messageHash");
                    String recipient = extractJsonValue(obj, "recipient");
                    String message   = extractJsonValue(obj, "message");
                    String flag      = extractJsonValue(obj, "flag");
                    MessageData m    = new MessageData(msgID, recipient, message, msgHash, flag, count++);
                    storedMessages.add(m);
                    messageHashes.add(msgHash);
                    messageIDs.add(msgID);
                }
                System.out.println("Stored messages loaded from " + filename);
            } catch (Exception e) {
                // NO FILE YET - NORMAL ON FIRST RUN //
            }
        }

        // EXTRACT A VALUE FROM A JSON STRING BY KEY //
        private static String extractJsonValue(String obj, String key) {
            String search  = "\"" + key + "\"";
            int idx        = obj.indexOf(search);
            if (idx == -1) return "";
            int colonIdx   = obj.indexOf(":", idx + search.length());
            int startQuote = obj.indexOf("\"", colonIdx + 1);
            if (startQuote == -1) return "";
            int endQuote   = obj.indexOf("\"", startQuote + 1);
            if (endQuote == -1) return "";
            return obj.substring(startQuote + 1, endQuote);
        }

        // a. DISPLAY RECIPIENT AND MESSAGE FOR ALL STORED MESSAGES //
        public static String displayStoredSenderRecipient() {
            if (storedMessages.isEmpty()) return "No stored messages.";
            StringBuilder sb = new StringBuilder();
            for (MessageData m : storedMessages) {
                sb.append("Recipient: ").append(m.recipient)
                  .append(" | Message: ").append(m.messageText).append("\n");
            }
            return sb.toString().trim();
        }

        // b. FIND AND RETURN THE LONGEST MESSAGE ACROSS SENT AND STORED //
        public static String getLongestMessage() {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);
            if (all.isEmpty()) return "No messages available.";
            MessageData longest = all.get(0);
            for (MessageData m : all) {
                if (m.messageText.length() > longest.messageText.length()) {
                    longest = m;
                }
            }
            return longest.messageText;
        }

        // c. SEARCH BY MESSAGE ID AND RETURN RECIPIENT AND MESSAGE //
        public static String searchByMessageID(String id) {
            for (MessageData m : sentMessages) {
                if (m.messageID.equals(id)) {
                    return "Recipient: " + m.recipient + " | Message: " + m.messageText;
                }
            }
            for (MessageData m : storedMessages) {
                if (m.messageID.equals(id)) {
                    return "Recipient: " + m.recipient + " | Message: " + m.messageText;
                }
            }
            return "Message ID not found.";
        }

        // d. SEARCH ALL MESSAGES FOR A SPECIFIC RECIPIENT //
        public static String searchByRecipient(String recipient) {
            StringBuilder sb = new StringBuilder();
            for (MessageData m : sentMessages) {
                if (m.recipient.equals(recipient)) sb.append(m.messageText).append("\n");
            }
            for (MessageData m : storedMessages) {
                if (m.recipient.equals(recipient)) sb.append(m.messageText).append("\n");
            }
            return sb.length() > 0 ? sb.toString().trim() : "No messages found for this recipient.";
        }

        // e. DELETE A MESSAGE BY ITS HASH //
        public static String deleteByHash(String hash) {
            Iterator<MessageData> it = storedMessages.iterator();
            while (it.hasNext()) {
                MessageData m = it.next();
                if (m.messageHash.equals(hash)) {
                    String text = m.messageText;
                    it.remove();
                    messageHashes.remove(hash);
                    return "Message: \"" + text + "\" successfully deleted.";
                }
            }
            Iterator<MessageData> it2 = sentMessages.iterator();
            while (it2.hasNext()) {
                MessageData m = it2.next();
                if (m.messageHash.equals(hash)) {
                    String text = m.messageText;
                    it2.remove();
                    messageHashes.remove(hash);
                    return "Message: \"" + text + "\" successfully deleted.";
                }
            }
            return "Message hash not found.";
        }

        // f. DISPLAY FULL REPORT OF ALL SENT AND STORED MESSAGES //
        public static String displayReport() {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);
            if (all.isEmpty()) return "No messages to report.";
            StringBuilder sb = new StringBuilder();
            sb.append("MESSAGE REPORT\n");
            for (MessageData m : all) {
                sb.append("Message Hash : ").append(m.messageHash).append("\n");
                sb.append("Recipient    : ").append(m.recipient).append("\n");
                sb.append("Message      : ").append(m.messageText).append("\n\n");
            }
            return sb.toString().trim();
        }

        // GETTERS //
        public String getMessageID()   { return messageID; }
        public String getRecipient()   { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }
        public String getFlag()        { return flag; }
    }

    // STORED MESSAGES SUB-MENU //
    private static void storedMessagesMenu(Scanner scanner) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search messages by recipient");
            System.out.println("e) Delete a message by hash");
            System.out.println("f) Display full report");
            System.out.println("x) Back to main menu");
            System.out.print("Choose option: ");
            String opt = scanner.nextLine().trim().toLowerCase();
            switch (opt) {
                case "a":
                    System.out.println(MessageData.displayStoredSenderRecipient());
                    break;
                case "b":
                    System.out.println(MessageData.getLongestMessage());
                    break;
                case "c":
                    System.out.print("Enter Message ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.println(MessageData.searchByMessageID(id));
                    break;
                case "d":
                    System.out.print("Enter recipient number: ");
                    String rec = scanner.nextLine().trim();
                    System.out.println(MessageData.searchByRecipient(rec));
                    break;
                case "e":
                    System.out.print("Enter message hash: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(MessageData.deleteByHash(hash));
                    break;
                case "f":
                    System.out.println(MessageData.displayReport());
                    break;
                case "x":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // MAIN //
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // LOAD STORED MESSAGES FROM PREVIOUS SESSION //
        MessageData.loadStoredMessages("stored_messages.json");

        Login user = null;

        // WELCOME / REGISTER / LOGIN SCREEN //
        boolean welcomed = true;
        while (welcomed) {
            System.out.println("\n WELCOME TO QUICKCHAT ");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("Choose option: ");
            int welcome;
            try {
                welcome = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid option.");
                continue;
            }
            switch (welcome) {
                case 1:
                    System.out.println("\nREGISTRATION");
                    System.out.print("First Name: ");
                    String regFirst = scanner.nextLine();
                    System.out.print("Last Name: ");
                    String regLast = scanner.nextLine();

                    // CAPTURE USERNAME //
                    String regUsername;
                    while (true) {
                        System.out.print("Username: ");
                        regUsername = scanner.nextLine();
                        Login tempU = new Login(regFirst, regLast, regUsername, "", "+27000000000");
                        if (tempU.checkUserName()) {
                            System.out.println("Username successfully captured.");
                            break;
                        } else {
                            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
                        }
                    }

                    // CAPTURE PASSWORD //
                    String regPassword;
                    while (true) {
                        System.out.print("Password: ");
                        regPassword = scanner.nextLine();
                        Login tempP = new Login(regFirst, regLast, regUsername, regPassword, "+27000000000");
                        if (tempP.checkPasswordComplexity()) {
                            System.out.println("Password successfully captured.");
                            break;
                        } else {
                            System.out.println("Password is not correctly formatted; please ensure that the password contains at least 8 characters, a capital letter, a number and a special character.");
                        }
                    }

                    // CAPTURE CELL NUMBER //
                    String regCell;
                    while (true) {
                        System.out.print("Cell number (+27): ");
                        regCell = scanner.nextLine();
                        Login tempC = new Login(regFirst, regLast, regUsername, regPassword, regCell);
                        if (tempC.checkCellPhoneNumber()) {
                            System.out.println("Cellphone number successfully captured.");
                            break;
                        } else {
                            System.out.println("Cell phone number incorrectly formatted or does not contain international code ( +27 ).");
                        }
                    }

                    // SAVE NEW USER //
                    user = new Login(regFirst, regLast, regUsername, regPassword, regCell);
                    System.out.println("Registration successful! You can now log in.");
                    break;

                case 2:
                    // BLOCK LOGIN IF NO USER IS REGISTERED YET //
                    if (user == null) {
                        System.out.println("No user registered. Please register first.");
                        break;
                    }
                    System.out.println("\nLOGIN");
                    System.out.print("Username: ");
                    String enteredUsername = scanner.nextLine();
                    System.out.print("Password: ");
                    String enteredPassword = scanner.nextLine();
                    System.out.println(user.returnLoginStatus(enteredUsername, enteredPassword));

                    // EXIT WELCOME LOOP ON SUCCESSFUL LOGIN //
                    if (user.loginUser(enteredUsername, enteredPassword)) {
                        welcomed = false;
                    }
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }

        // ASK HOW MANY MESSAGES TO SEND THIS SESSION //
        System.out.print("\nHow many messages would you like to send? ");
        int numMessages = 0;
        try {
            numMessages = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Exiting.");
            scanner.close();
            return;
        }

        // MAIN MENU LOOP //
        boolean running = true;
        while (running) {
            System.out.println("\n WELCOME TO QUICKCHAT");
            System.out.println("1) Send Messages");
            System.out.println("2) Show Recently Sent Messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose option: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid option.");
                continue;
            }
            switch (choice) {
                case 1:
                    // LOOP THROUGH EACH MESSAGE SLOT //
                    for (int i = 1; i <= numMessages; i++) {
                        System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
                        System.out.print("Recipient cell (+27...): ");
                        String recipient = scanner.nextLine().trim();

                        // KEEP ASKING UNTIL MESSAGE IS WITHIN 250 CHARACTERS //
                        String messageText;
                        while (true) {
                            System.out.print("Message: ");
                            messageText = scanner.nextLine();
                            if (messageText.length() <= 250) {
                                System.out.println("Message ready to send.");
                                break;
                            } else {
                                int over = messageText.length() - 250;
                                System.out.println("Message exceeds 250 characters by " + over + "; please reduce the size.");
                            }
                        }

                        // CREATE MESSAGE OBJECT - ID AND HASH ARE AUTO-GENERATED //
                        MessageData msg = new MessageData(i, recipient, messageText);
                        System.out.println(msg.checkRecipientCell());
                        System.out.println("Message ID generated: " + msg.getMessageID());
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        // ASK USER WHAT TO DO WITH THE MESSAGE //
                        System.out.println("\n1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Choose: ");
                        int sendChoice;
                        try {
                            sendChoice = Integer.parseInt(scanner.nextLine().trim());
                        } catch (NumberFormatException e) {
                            sendChoice = 2;
                        }
                        System.out.println(msg.sentMessage(sendChoice));

                        // DISPLAY MESSAGE DETAILS //
                        System.out.println("\n Message Details ");
                        System.out.println("Message ID  : " + msg.getMessageID());
                        System.out.println("Message Hash: " + msg.getMessageHash());
                        System.out.println("Recipient   : " + msg.getRecipient());
                        System.out.println("Message     : " + msg.getMessageText());
                    }
                    System.out.println("\nTotal messages sent: " + MessageData.returnTotalMessages());

                    // SAVE STORED MESSAGES TO JSON //
                    if (!MessageData.storedMessages.isEmpty()) {
                        MessageData.storeMessage("stored_messages.json");
                    }
                    break;

                case 2:
                    System.out.println(MessageData.printMessages());
                    break;

                case 3:
                    storedMessagesMenu(scanner);
                    break;

                case 4:
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}
