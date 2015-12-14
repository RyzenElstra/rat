/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;
import java.util.UUID;

/**
 *
 * @author Lee
 */
public class Client {

    private Socket s;
    private Thread t;
    public String id;
    public String infos;
    
    public Client(Socket s, Thread t) {
        this.s = s;
        t.start();
        this.t = t;
        id = UUID.randomUUID().toString().replace("-", "");
        send("HALLO|" + id);
    }

    public void send(String msg) {
        try {
            s.getOutputStream().write((msg + "\n").getBytes());
            s.getOutputStream().flush();
        } catch (Exception e) {
        }
    }

    public void disconect() {
        try {
            t.stop();
            s.close();
        } catch (Exception e) {
        }
    }
}
