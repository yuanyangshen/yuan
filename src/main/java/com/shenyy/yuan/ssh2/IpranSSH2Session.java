package com.shenyy.yuan.ssh2;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * IPRAN巡检工具SSH2 Session 用于连接设备,登陆OS/telnet,发送命令,获取回显
 */
public class IpranSSH2Session {
    /**
     * 日志
     */
    private static final Logger LOGGER = LogManager.getLogger(IpranSSH2Session.class);
    /**
     * SSH2连接
     */
    private Connection conn;
    /**
     * SSH2会话
     */
    private Session session;
    /**
     * 输出流,用于发送命令
     */
    private OutputStream output;
    /**
     * 输入流,用于读取回显
     */
    private BufferedReader reader;
    /**
     * 是否为操作系统命令,默认false
     */
    private boolean isLinuxCommand = false;

    /**
     * 与设备建立连接
     *
     * @param ip          设备ip
     * @param port        ssh2端口号
     * @param sshUsername 通过ssh2登陆操作系统的用户名
     * @param sshPassword 通过ssh2登陆操作系统的密码
     * @return 是否成功与设备建立连接
     */
    public boolean connect(String ip, int port, String sshUsername, String sshPassword) {
        boolean isConnect;
        conn = new Connection(ip);//, port
        try {
            conn.connect();
        } catch (IOException e) {
            LOGGER.error("Fail to get ssh2 connection, Ip: " + ip + " Port: " + port, e);
            return false;
        }
        try {
            isConnect = conn.authenticateWithPassword(sshUsername, sshPassword);
        } catch (IOException e) {
            LOGGER.error("Fail to login OS, Ip: " + ip + " Port: " + port, e);
            return false;
        }
        if (isConnect) {
            try {
                session = conn.openSession();
            } catch (IOException e) {
                LOGGER.error("Fail to get ssh2 session, Ip: " + ip + " Port: " + port, e);
                return false;
            }
        }
        output = session.getStdin();
        reader = new BufferedReader(new InputStreamReader(session.getStdout()));
        return isConnect;
    }

    /**
     * 登陆协议栈
     *
     * @param port           端口号(6111,2650)
     * @param telnetUsername 协议栈用户名
     * @param telnetPassword 协议栈密码
     * @return 是否成功登陆协议栈
     */
    public boolean loginTelnet(int port, String telnetUsername, String telnetPassword) {
        String command = "telnet 127.1 " + port;
        try {
            session.execCommand(command);
            Thread.sleep(1000);
            session.ping();
            char[] chars = new char[1024 * 10];
            int length = -1;
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                if (content.contains("% Bad passwords, too many failures!")) {
                    LOGGER.error("% Bad passwords, too many failures!");
                    break;
                } else if (content.trim().endsWith("Username:")) {
                    output.write((telnetUsername + "\r").getBytes());
                } else if (content.trim().endsWith("Password:") || content.trim().endsWith("Password")) {
                    output.write((telnetPassword + "\r").getBytes());
                } else if (content.trim().endsWith(telnetPassword)) {
                    output.write("\r".getBytes());
                } else if (content.trim().endsWith(">")) {
                    output.write("en\r".getBytes());
                } else if (content.trim().endsWith("#")) {
                    return true;
                } else {
                    break;
                }
                output.flush();
                Thread.sleep(100);
                session.ping();
            }
        } catch (IOException | InterruptedException e) {
            session.close();
            conn.close();
            LOGGER.error("fail to login telnet by ssh", e);
            return false;
        }
        return false;
    }

    /**
     * 发送协议栈命令,获取回显
     *
     * @param command 命令
     * @return 回显
     */
    public String sendCommand(String command) {
        StringBuilder echo = new StringBuilder("");
        try {
            output.write((command + "\r").getBytes());
            output.flush();
            Thread.sleep(10);
            session.ping();
            boolean flag = false;
            char[] chars = new char[1024 * 10];
            int length = -1;
            int count = 0;
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                if (content.contains(command)) {
                    flag = true;
                }
                if (flag) {
                    echo.append(content);
                    if (content.contains("--More--")) {
                        output.write("\r\n".getBytes());
                        output.flush();
                        Thread.sleep(10);
                        session.ping();
                        continue;
                    } else if (content.trim().endsWith("#")) {
                        return echo.toString();
                    }
                }
                output.write("\r\n".getBytes());
                output.flush();
                Thread.sleep(10);
                count++;
                if (count > 200) {
                    LOGGER.error("Fail to match the end char!");
                    return echo.toString();
                }
                session.ping();
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Fail to get data! Ip: " + conn.getHostname() + " PS Command: " + command, e);
            return null;
        }
        return null;
    }

//    public String sendCommand(String command) {
//        StringBuilder echo = new StringBuilder("");
//        try {
//            output.write((command + "\r").getBytes());
//            output.flush();
//            InputStream in = session.getStdout();
//            byte[] b = new byte[1024 * 10];
//            int dataByte;
//            int i = 0;
//            while ((dataByte = in.read()) != -1) {
//                b[i] = (byte) dataByte;
//                i++;
//                echo.append((char) dataByte);
//                if (echo.toString().endsWith("--More--")) {
//                    output.write("\r\n".getBytes());
//                    output.flush();
//                }
//            }
//            String ret = new String(b, 0, i);
//            return ret;

//            Thread.sleep(10);
//            session.ping();
//            boolean flag = false;
//            char[] chars = new char[1024 * 10];
//            int length = -1;
//            int count = 0;
//            while ((length = reader.read(chars)) != -1) {
//                String content = String.valueOf(Arrays.copyOf(chars, length));
//                if (content.contains(command)) {
//                    flag = true;
//                }
//                if (flag) {
//                    echo.append(content);
//                    if (content.contains("--More--")) {
//                        output.write("\r\n".getBytes());
//                        output.flush();
//                        Thread.sleep(10);
//                        session.ping();
//                        continue;
//                    } else if (content.trim().endsWith("#")) {
//                        return echo.toString();
//                    }
//                }
//                output.write("\r\n".getBytes());
//                output.flush();
//                Thread.sleep(10);
//                count++;
//                if (count > 200) {
//                    LOGGER.error("Fail to match the end char!");
//                    return echo.toString();
//                }
//                session.ping();
//            }
//        } catch (IOException e) {
//            LOGGER.error("Fail to get data! Ip: " + conn.getHostname() + " PS Command: " + command, e);
//            return null;
//        }
//        //return null;
//    }


    /**
     * 发送操作系统命令,获取回显
     *
     * @param command 命令
     * @return 回显
     */
    public String sendLinuxCommand(String command) {
        StringBuilder echo = new StringBuilder("");
        try {
            if (!isLinuxCommand) {
                session.requestDumbPTY();
                session.startShell();
                isLinuxCommand = true;
            }
            output.write((command + "\r").getBytes());
            output.flush();
            Thread.sleep(10);
            session.ping();
            boolean flag = false;
            char[] chars = new char[1024 * 10];
            int length = -1;
            int count = 0;
            while ((length = reader.read(chars)) != -1) {
                String content = String.valueOf(Arrays.copyOf(chars, length));
                //System.out.println(content);
                if (content.contains(command)) {
                    flag = true;
                }
                if (flag) {
                    echo.append(content);
                    /*if (content.contains("--More--")) {
                        output.write('\n');
                        output.flush();
                        Thread.sleep(10);
                        session.ping();
                        continue;
                    } else if (content.endsWith("> ") || content.endsWith("~$")) {
                        return echo.toString();
                    }*/
                    return echo.toString();
                }
                output.write('\n');
                output.flush();
                Thread.sleep(10);
                count++;
                if (count > 200) {
                    LOGGER.error("Fail to match the end char!");
                    return echo.toString();
                }
                session.ping();
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Fail to get data! Ip: " + conn.getHostname() + " OS Command: " + command, e);
            return null;
        }
        return null;
    }

    /**
     * 协议栈登出
     */
    public void psLogout() {
        try {
            Thread.sleep(1000);
            output.write("exit\r\n".getBytes());
            Thread.sleep(1000);
            output.write("exit\r\n".getBytes());
            output.flush();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        } finally {
            disconnect();
        }
    }

    /**
     * 操作系统登出
     */
    public void osLogout() {
        try {
            Thread.sleep(1000);
            output.write("exit\r\n".getBytes());
            output.flush();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        } finally {
            disconnect();
        }
    }

    /**
     * 关闭连接,释放资源
     */
    public void disconnect() {
        try {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
            if (null != session) {
                session.close();
            }
            if (null != conn) {
                conn.close();
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
