package com.shenyy.yuan.ssh2;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is used to Get Equipment-Connection by ssh2,Login OS/PS,Execute Command and Get Echo Data
 */
public class SSH2Connection {
    /**
     * Log
     */
    private static final Logger LOGGER = LogManager.getLogger(SSH2Connection.class);
    /**
     * Single sleep time(ms)
     */
    private static final int SINGLE_SLEEP_TIME = 300; //Integer.parseInt(SysConfigUtil.getProperty("SINGLE_SLEEP_TIME"));
    /**
     * Sleep times
     */
    private static final int SLEEP_TIMES = 10; //Integer.parseInt(SysConfigUtil.getProperty("SLEEP_TIMES"));

    /**
     * SSH2 Connection
     */
    private Connection conn;
    /**
     * SSH2 Session
     */
    private Session session;
    /**
     * Use this to get Echo Data
     */
    private InputStream stdout;
    /**
     * InputStream
     */
    private InputStream stderr;
    /**
     * Use this to send command
     */
    private OutputStream stdin;

    /**
     * Get connection with the equipment
     *
     * @param ip       Ip
     * @param port     Port for ssh2
     * @param username Username for OS(ssh2)
     * @param password Password for OS(ssh2)
     * @return boolean
     */
    public boolean connect(String ip, int port, String username, String password) {
        boolean isConnected = false;
        conn = new Connection(ip, port);
        try {
            conn.connect();
            if (conn.authenticateWithPassword(username, password)) {
                session = conn.openSession();
                session.requestDumbPTY();
                session.startShell();
                stdout = session.getStdout();
                stderr = session.getStderr();
                stdin = session.getStdin();
                isConnected = true;
            }
        } catch (IOException e) {
            LOGGER.error("Get SSH2 Connection Failed, Ip: " + ip + " Port: " + port, e);
        }
        return isConnected;
    }

    /**
     * Login Protocol-Stack
     *
     * @param port     Port for telnet(6111,2650)
     * @param username Username for PS(telnet)
     * @param password Password for PS(telnet)
     * @return boolean
     */
    public boolean loginPS(int port, String username, String password) {
        String echo = executeCommand("telnet 127.1 " + port);
        if (echo.contains("Username")) {
            echo = executeCommand(username);
            if (echo.contains("No username or bad password!")) {
                LOGGER.error("No username or bad password!");
                return false;
            }
        }
        if (echo.contains("Password")) {
            echo = executeCommand(password);
            if (echo.contains("No username or bad password!")) {
                LOGGER.error("No username or bad password!");
                return false;
            }
            if (echo.contains("Bad passwords, too many failures!")) {
                LOGGER.error("Bad passwords, too many failures!");
                return false;
            }
            if (echo.trim().endsWith("#")) {
                return true;
            } else {
                echo = executeCommand("en");
                if (echo.trim().endsWith("#")) {
                    return true;
                }
            }
        }
        LOGGER.error("Fail to login Protocol-Stack by SSH2");
        return false;
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
            stdin.write((command + "\r").getBytes());
            stdin.flush();
            while (true) {
                if (hasData()) {
                    int outAvailable = stdout.available();
                    if (outAvailable != 0) {
                        byte[] bytes = new byte[outAvailable];
                        int len = stdout.read(bytes);
                        String content = new String(bytes, 0, len, "UTF-8");
                        echo.append(content);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("(SSH2) Fail to Execute Command : " + command + " Ip:" + conn.getHostname(), e);
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
                int outAvailable = stdout.available();
                int errAvailable = stderr.available();
                if (outAvailable != 0 || errAvailable != 0) {
                    hasData = true;
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        }
        return hasData;
    }

    /**
     * Exit the connection and release resources
     */
    public void close() {
        try {
            stdin.write("exit\r".getBytes());
            stdin.flush();
            stdin.write("exit\r".getBytes());
            stdin.flush();
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (null != stdin) {
                    stdin.close();
                }
                if (null != stdout) {
                    stdout.close();
                }
                if (null != stderr) {
                    stderr.close();
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
            if (null != session) {
                session.close();
            }
            if (null != conn) {
                conn.close();
            }
        }
    }

}
