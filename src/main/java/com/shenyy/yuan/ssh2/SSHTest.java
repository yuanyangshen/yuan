package com.shenyy.yuan.ssh2;

import java.util.Scanner;

public class SSHTest {

    public static void main(String[] args){
        IpranSSH2Session session = new IpranSSH2Session();
        boolean conn = session.connect("10.109.50.100",22,"root","root");
        if (conn){
            /*while(true){
                Scanner sc = new Scanner(System.in);
                System.out.println("请输入命令：");
                String commond = sc.nextLine();
                if("exit".equals(commond)){
                    break;
                }
                String result = session.sendLinuxCommand(commond);
                System.out.println(result);

            }*/
            String result = session.sendLinuxCommand("/home/eos/host_backup_status_access -s 3 -v 55");
            System.out.println(result);
        }
    }

    public void sshTest2(){
        SSH2Connection connection = new SSH2Connection();
        boolean conn = connection.connect("10.109.50.100",22,"root","root");
        if (conn){
            String result = connection.executeCommand("/home/eos/host_backup_status_access -s 3 -v 55");
            System.out.println(result);
        }
    }
}
