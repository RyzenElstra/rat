/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Lee
 */
public class Server {

    static List<Client> clients = new ArrayList<Client>();
    static Gui gui;

    public static void main(String[] args) throws Exception {
        gui = new Gui();
        System.out.println("Server gestartet.");
        ServerSocket ss = new ServerSocket(1604);
        while (true) {
            Socket s = ss.accept();
            System.out.println("Neuer client: " + s);
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter p = new PrintWriter(s.getOutputStream());
                        Client cl = null;

                        while (true) {
                            String line = r.readLine();
                            if (line.startsWith("info")) {
                                String[] ar = line.split("\\|");
                                String uuid = ar[1];
                                System.out.println("Client hat sich verbunden " + uuid);
                                for (Client c : clients) {
                                    if (c.id.equals(uuid)) {
                                        cl = c;
                                        break;
                                    }
                                }
                                cl.infos = s.getInetAddress().getHostAddress() + " " + ar[2] + "@" + ar[4] + " " + ar[3];
                                addClient(cl);
                            }
                            if (line.startsWith("systeminfos")) {
                                String[] ar = line.split("\\|");
                                String data = fromHex(ar[1]);
                                String[] ar1 = data.split("\n");
                                DefaultTableModel d = (DefaultTableModel) Gui.jTable1.getModel();
                                while (d.getRowCount() > 0) {
                                    d.removeRow(0);
                                }
                                for (String str : ar1) {
                                    try {
                                        String[] ar2 = str.split("\\|");
                                        d.addRow(new Object[]{ar2[0], ar2[1]});
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            };
            Client c = new Client(s, t);
            clients.add(c);
        }
    }

    static String fromHex(String input) {
        try {
            return new String(Hex.decodeHex(input.toCharArray()));
        } catch (Exception e) {
            return new String();
        }
    }

    static void addClient(Client c) {
        DefaultListModel dlm = (DefaultListModel) gui.jList1.getModel();
        dlm.addElement("Client: " + c.infos + " " + c.id);
    }

    static Client getClient(String uuid) {
        for (Client c : clients) {
            if (c.id.equals(uuid)) {
                return c;
            }
        }
        return null;
    }
}
