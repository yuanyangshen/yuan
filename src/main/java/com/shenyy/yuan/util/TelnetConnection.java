package com.shenyy.yuan.util;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
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
    private static final int SINGLE_SLEEP_TIME = 10;//Integer.parseInt(SysConfigUtil.getProperty("SINGLE_SLEEP_TIME"));
    /**
     * Sleep times
     */
    private static final int SLEEP_TIMES = 300;//Integer.parseInt(SysConfigUtil.getProperty("SLEEP_TIMES"));
    /**
     * Telnet client
     */
    private TelnetClient client = new TelnetClient(TERM_TYPE);
    /**
     * Ip of Ne
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

    private PrintStream printStream;

    private static void makeBackupDir(TelnetConnection telnetConnection) {
        telnetConnection.executePSCommand("mkdir backup/12.80.1.254");
    }

    public static void main(String[] args) {
        TelnetConnection telnetConnection = new TelnetConnection();
        boolean isConnect = telnetConnection.connect("192.168.18.1",23,"admin","admin");
        if (isConnect){
            telnetConnection.executePSCommand("sh");
            String result = telnetConnection.executePSCommand("wl bss");
            System.out.println(result);
            telnetConnection.close();
        }
    }

    /**
     * Get telnet connection with the equipment
     *
     * @param neIp     ip of NE
     * @param port     port for telnet
     * @param username Username for PS(telnet)
     * @param password Password for PS(telnet)
     * @return boolean
     */
    public boolean connect(String ip, int port, String username, String password) {
        this.ip = ip;
        boolean isConnected = false;
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        try {
            client.connect(ip, port);
            output = client.getOutputStream();
            input = client.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            printStream = new PrintStream(output);
            char[] chars = new char[1024 * 10];
            int length = -1;
            Thread.sleep(SINGLE_SLEEP_TIME);
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                sb.append(content);
                if (content.trim().endsWith("Login:")) {
                    printStream.println(username);
                    printStream.flush();
                } else if (content.trim().endsWith("Password:")) {
                    printStream.println(password);
                    printStream.flush();
                } else if (content.trim().endsWith(">")) {
                    isConnected = true;
                    break;
                }
                Thread.sleep(SINGLE_SLEEP_TIME);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Get Telnet Connection Failed, Ip: " + ip, e);
        }
        //LoggerUtil.getLoggerByName(ip, 8000).info("连接设备时的命令信息为：" + sb.toString());
        return isConnected;
    }


    /**
     * Execute Command(OS_Command & PS_Command) and Get Echo Data
     *
     * @param command Command
     * @return Echo Data
     */
    public String executePSCommand(String command) {
        if (StringUtil.isEmpty(command)) {
            return null;
        }
        StringBuilder echo = new StringBuilder("");
        try {
            printStream.println(command);
            printStream.flush();
            while (true) {
                if (hasData(SINGLE_SLEEP_TIME)) {
                    int available = input.available();
                    byte[] bytes = new byte[available];
                    int len = input.read(bytes);
                    String content = new String(bytes, 0, len, "UTF-8");
                    /*if (content.contains("--More--")) {
                        printStream.println();
                        printStream.flush();
                    }*/
                    echo.append(content);
                    if (content.trim().endsWith("#")) {// || content.trim().endsWith("]")
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fail to get data! Ip: " + ip + " Command: " + command, e);
        }
        //LoggerUtil.getLoggerByName(ip, 8000).info("命令" + command + "的执行结果为：" + echo.toString());
        return echo.toString();
    }

    /**
     * Waiting for Echo Data
     * if there is still no data,return false
     *
     * @return boolean
     */
    private boolean hasData(Integer time) {
        boolean hasData = false;
        try {
            for (int i = 0; i < SLEEP_TIMES; i++) {
                Thread.sleep(time);
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
            printStream.println("exit");
            printStream.flush();
            printStream.println("exit");
            printStream.flush();
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (client != null) {
                    client.disconnect();
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
