/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitura_serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

public class Leitura_serial extends JFrame implements SerialPortEventListener {

    SerialPort serialPort;
    private BufferedReader input;
    private OutputStream output;
    private JTextArea textArea;
    private static final String PORT_NAMES[] = {
        "COM3", // Windows
    };
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    public Leitura_serial() {
        super("Leitura Serial");
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }

        if (portId == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar a porta COM.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                textArea.append(inputLine + "\n");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Leitura_serial mainFrame = new Leitura_serial();
            mainFrame.setVisible(true);
            mainFrame.initialize();
            System.out.println("Iniciando leitura da porta serial");

            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            });
            t.start();
            System.out.println("Thread inicializada.");
        });
    }
}
