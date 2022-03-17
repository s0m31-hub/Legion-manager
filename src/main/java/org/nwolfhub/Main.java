package org.nwolfhub;

import org.apache.logging.log4j.LogManager;
import org.nwolfhub.database.ManualDatabase;
import org.nwolfhub.vk.VkGroup;

import java.io.*;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Legion AntiSpam - startup.");
        System.out.println("Checking database connection");
        String login = ""; String password = ""; String token = ""; Integer group_id = 1;
        try (FileInputStream in = new FileInputStream("dbAuthInfo")) {
            String content = new String(in.readAllBytes()).replace("\n", "");
            if(content.replace(" ", "").equals("") || !content.contains("@")) {
                System.out.println("dbAuthInfo is empty or invalid. Format: login@password");
                System.exit(2);
            }
            login = content.split("@")[0]; password = content.split("@")[1];
        } catch (IOException e) {
            File f = new File("dbAuthInfo");
            f.createNewFile();
            System.out.println("Failed to read authinfo for database! New file has been created at " + f.getAbsolutePath());
            e.printStackTrace();
            System.exit(2);
        }
        try (FileInputStream in = new FileInputStream("token")) {
            token = new String(in.readAllBytes()).replace("\n", "").replace(" ", "");
            if(token.replace(" ", "").equals("") || !token.contains("@")) {
                System.out.println("token is empty or invalid. Format: token@groupId");
                System.exit(2);
            }
            String[] holder = token.split("@");
            token = holder[0];
            group_id = Integer.valueOf(holder[1]);
        } catch (IOException e) {
            File f = new File("token");
            f.createNewFile();
            System.out.println("Failed to read vk token! New file has been created at " + f.getAbsolutePath());
            e.printStackTrace();
            System.exit(2);
        }
        try {
            ManualDatabase db = new ManualDatabase("192.168.1.35", "legion", login, password);
            System.out.println("PreStartup has been finished. Connecting to vk");
            VkGroup vk = new VkGroup(token, group_id);
            new File("logs").mkdirs();
            File errFile = new File("logs/errlog" + new Random().nextInt() + ".log");
            errFile.createNewFile();
            System.out.println("Error log file: " + errFile.getAbsolutePath() + ". Redirecting error stream");
            File msgFile = new File("logs/msglog" + new Random().nextInt() + ".log");
            msgFile.createNewFile();
            System.out.println("message log file: " + errFile.getAbsolutePath() + ". Redirecting log stream");
            ChatKeeper.initialize(vk);
            System.setOut(new PrintStream(msgFile));
            System.setErr(new PrintStream(errFile));
            UpdateHandler.initialize(vk, db);
            UpdateListener.initialize(vk, db);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database!");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to connect to vk.com. Make sure your token and group_id is correct");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
