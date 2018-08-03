package com.shenyy.yuan.ssh2;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * This class is used to Get Equipment-Connection by telnet,Login OS/PS,Execute Command and Get Echo Data
 */
public class TelnetConnection {
    /**
     * Log
     */
    private static final Logger LOGGER = LogManager.getLogger(TelnetConnection.class);
    /**
     * Type of equipment's OS
     */
    private static final String TERM_TYPE = "LINUX";
    /**
     * Single sleep time(ms)
     */
    private static final int SINGLE_SLEEP_TIME = 10; //Integer.parseInt(SysConfigUtil.getProperty("SINGLE_SLEEP_TIME"));
    /**
     * Sleep times
     */
    private static final int SLEEP_TIMES = 300; //Integer.parseInt(SysConfigUtil.getProperty("SLEEP_TIMES"));
    /**
     * Telnet client
     */
    private TelnetClient client = new TelnetClient(TERM_TYPE);
    /**
     * Equipment's ip
     */
    private String ip;
    /**
     * Telnet OutputStream: use this to send command
     */
    private OutputStream output;
    /**
     * Telnet InputStream: use this to get Echo Data
     */
    private InputStream input;
    /**
     * BufferedReader
     */
    private BufferedReader reader;

    /**
     * Get telnet connection with the equipment
     *
     * @param neIp     ip of NE
     * @param port     port for telnet
     * @param username Username for PS(telnet)
     * @param password Password for PS(telnet)
     * @return boolean
     */
    public boolean connect(String neIp, int port, String username, String password) {
        this.ip = neIp;
        boolean isConnected = false;
        try {
            client.connect(ip, port);
            output = client.getOutputStream();
            input = client.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            char[] chars = new char[1024 * 10];
            int length = -1;
            Thread.sleep(SINGLE_SLEEP_TIME);
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                if (content.trim().endsWith("No username or bad password")) {
                    return false;
                } else if (content.trim().endsWith("Username:")) {
                    output.write((username + "\r").getBytes());
                    output.flush();
                } else if (content.trim().endsWith("Password:")) {
                    output.write((password + "\r").getBytes());
                    output.flush();
                } else if (content.trim().endsWith("#")) {
                    isConnected = true;
                    break;
                }
                Thread.sleep(SINGLE_SLEEP_TIME);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Get Telnet Connection Failed, Ip: " + ip, e);
        }
        return isConnected;
    }

    /**
     * Login OS by telnet
     *
     * @param username Username for OS
     * @param password Password for OS
     * @return boolean
     */
    public boolean loginOS(String username, String password) {
        boolean isLogin = false;
        try {
            output.write(("ostelnet 127.0.0.1" + "\r\n").getBytes());
            output.flush();
            char[] chars = new char[1024 * 10];
            int length = -1;
            Thread.sleep(10);
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                if (content.trim().endsWith("login:")) {
                    output.write((username + "\r").getBytes());
                    output.flush();
                } else if (content.trim().endsWith("Password:")) {
                    output.write((password + "\r").getBytes());
                    output.flush();
                } else if (content.trim().endsWith(">")) {
                    isLogin = true;
                    break;
                }
                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Login OS by telnet Failed, Ip: " + ip, e);
        }
        return isLogin;
    }

    /**
     * Execute Command(OS_Command & PS_Command) and Get Echo Data
     *
     * @param command Command
     * @return Echo Data
     */
    public String executeCommand(String command) {
        StringBuilder echo = new StringBuilder("");
        try {
            output.write((command + "\r").getBytes());
            output.flush();
            while (true) {
                if (hasData()) {
                    int available = input.available();
                    byte[] bytes = new byte[available];
                    int len = input.read(bytes);
                    String content = new String(bytes, 0, len, "UTF-8");
                    if (content.contains("--More--")) {
                        output.write("\r\n".getBytes());
                        output.flush();
                    }
                    echo.append(content);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fail to get data! Ip: " + ip + "PS Command: " + command, e);
        }
        return echo.toString();
    }

    /**
     * Waiting for Echo Data
     * if there is still no data,return false
     *
     * @return boolean
     */
    private boolean hasData() {
        boolean hasData = false;
        try {
            for (int i = 0; i < SLEEP_TIMES; i++) {
                Thread.sleep(SINGLE_SLEEP_TIME);
                int available = input.available();
                if (available != 0) {
                    hasData = true;
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return hasData;
    }

    /**
     * Exit the connection and release resources
     */
    public void close() {
        try {
            output.write("exit\r".getBytes());
            output.flush();
            output.write("exit\r".getBytes());
            output.flush();
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (client != null) {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
