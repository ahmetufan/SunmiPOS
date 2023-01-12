package com.ahmet.sunmipost.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import com.ahmet.sunmipost.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 *  Simple package for connecting a sunmi printer via Bluetooth
 */
public class BluetoothUtil {

    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String Innerprinter_Address = "00:11:22:33:44:55";

    public static boolean isBlueToothPrinter = false;

    private static BluetoothSocket bluetoothSocket;

    private static BluetoothAdapter getBTAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private static BluetoothDevice getDevice(BluetoothAdapter bluetoothAdapter) {
        BluetoothDevice innerprinter_device = null;
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            if (device.getAddress().equals(Innerprinter_Address)) {
                innerprinter_device = device;
                break;
            }
        }
        return innerprinter_device;
    }

    private static BluetoothSocket getSocket(BluetoothDevice device) throws IOException {
        BluetoothSocket socket;
        socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
        socket.connect();
        return  socket;
    }

    /**
     * connect bluetooth
     */
    public static boolean connectBlueTooth(Context context) {
        if (bluetoothSocket == null) {
            if (getBTAdapter() == null) {
                Toast.makeText(context,  "Bluetooth device is unavailable", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!getBTAdapter().isEnabled()) {
                Toast.makeText(context, "Bluetooth device not detected. Please open Bluetooth!", Toast.LENGTH_SHORT).show();
                return false;
            }
            BluetoothDevice device;
            if ((device = getDevice(getBTAdapter())) == null) {
                Toast.makeText(context, "Not Found InnterPrinter!", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                bluetoothSocket = getSocket(device);
            } catch (IOException e) {
                Toast.makeText(context, "Bluetooth connect failed!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * disconnect bluethooth
     */
    public static void disconnectBlueTooth(Context context) {
        if (bluetoothSocket != null) {
            try {
                OutputStream out = bluetoothSocket.getOutputStream();
                out.close();
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  send esc cmd
     */
    public static void sendData(byte[] bytes) {
        if (bluetoothSocket != null) {
            OutputStream out = null;
            try {
                out = bluetoothSocket.getOutputStream();
                out.write(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //TODO 
        }
    }
}
