package com.meiziaccess.CommandTool;

/**
 * Created by user-u1 on 2016/7/30.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * Provides static methods for running SSH, scp as well as local commands.
 *
 */
public class CommandRunner {

    /*****************************************
     * @param host
     * @param port
     * @param username
     * @param password
    ****************************************/
    private static final String host = "162.105.16.64";

    private static final  int port = 8022;

    private static final  String username = "luyj";

    private static final  String password = "pkulky201";
    /****************************************/

    private static final Logger logger = Logger.getLogger(CommandRunner.class);

    private CommandRunner() {
    }

    /**
     * Get remote file through scp
     * @param remoteFile
     * @param localDir
     * @throws IOException
     */
    public static void scpGet(String remoteFile, String localDir) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("spc [" + remoteFile + "] from " + host + " to " + localDir);
        }
        Connection conn = getOpenedConnection();
        SCPClient client = new SCPClient(conn);
        client.get(remoteFile, localDir);
        conn.close();
    }

    /**
     * Put local file to remote machine.
     * @param localFile
     * @param remoteDir
     * @throws IOException
     */
    public static void scpPut(String localFile, String remoteDir) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("spc [" + localFile + "] to " + host + remoteDir);
        }
        Connection conn = getOpenedConnection();
        SCPClient client = new SCPClient(conn);
        client.put(localFile, remoteDir);
        conn.close();
    }

    /**
     * Run SSH command.
     * @param cmd
     * @return exit status
     * @throws IOException
     */
    public static int runSSH(String cmd) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("running SSH cmd [" + cmd + "]");
        }

        Connection conn = getOpenedConnection();
        Session sess = conn.openSession();
        sess.execCommand(cmd);

        InputStream stdout = new StreamGobbler(sess.getStdout());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

        while (true) {
            // attention: do not comment this block, or you will hit NullPointerException
            // when you are trying to read exit status
            String line = br.readLine();
            if (line == null)
                break;
            if (logger.isDebugEnabled()) {
                logger.debug(line);
            }
        }

        sess.close();
        conn.close();
        return sess.getExitStatus().intValue();
    }

    public static Vector<String> runSSHAndGetString(String cmd) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("running SSH cmd [" + cmd + "]");
        }

        Connection conn = getOpenedConnection();
        Session sess = conn.openSession();
        sess.execCommand(cmd);

        InputStream stdout = new StreamGobbler(sess.getStdout());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

        Vector<String> vec = new Vector<String>();
        while (true) {
            // attention: do not comment this block, or you will hit NullPointerException
            // when you are trying to read exit status
            String line = br.readLine();
            if (line == null)
                break;
            if (logger.isDebugEnabled()) {
                logger.debug(line);
            }
            vec.add(line);
        }

        sess.close();
        conn.close();
        return vec;
    }

    /**
     * return a opened Connection
     * @return
     * @throws IOException
     */
    private static Connection getOpenedConnection() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("connecting to " + host + " with user " + username
                    + " and pwd " + password);
        }

        Connection conn = new Connection(host, port);
        conn.connect(); // make sure the connection is opened
        boolean isAuthenticated = conn.authenticateWithPassword(username,
                password);
        if (isAuthenticated == false)
            throw new IOException("Authentication failed.");
        return conn;
    }

    /**
     * Run local command
     * @param cmd
     * @return exit status
     * @throws IOException
     */
    public static int runLocal(String cmd) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("running local cmd [" + cmd + "]");
        }

        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);

        InputStream stdout = new StreamGobbler(p.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            if (logger.isDebugEnabled()) {
                logger.debug(line);
            }

        }
        return p.exitValue();
    }

    public static Vector<String> execCmds(String cmd) {
        Vector<String> outs = new Vector<String>();
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            pro.waitFor();
            InputStream in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = read.readLine())!=null){
                outs.add(line);
            }
            //如果pro不为空，那么要清空
            if(null!=pro){
                pro.destroy();
                pro=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outs;
    }
    public static Vector<String> execCmdsArray(String[] cmd) {
        Vector<String> outs = new Vector<String>();
        try {
            Process pro = new ProcessBuilder(cmd).start();
            pro.waitFor();
            InputStream in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = read.readLine())!=null){
                outs.add(line);
            }
            //如果pro不为空，那么要清空
            if(null!=pro){
                pro.destroy();
                pro=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outs;
    }
}