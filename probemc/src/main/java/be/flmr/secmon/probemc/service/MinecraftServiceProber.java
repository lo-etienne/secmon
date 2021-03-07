package be.flmr.secmon.probemc.service;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.probe.service.ServiceProber;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MinecraftServiceProber implements ServiceProber {

    /*
        mc1!minecraft://mc.hypixel.net/![minimum]![maximum]![frequency]
     */

    public static void main(String[] args) throws IOException {
        MinecraftServiceProber minecraftServiceProber = new MinecraftServiceProber();
        IService service = new Service("mcserv1!minecraft://mc.hypixel.net/abc!12!12!12");
        minecraftServiceProber.get(service);

    }

    @Override
    public int get(IService service) throws IOException {
        String host = PatternUtils.extractGroup(service.getURL(), PatternGroup.URL, PatternGroup.HOST.name());
        String port = PatternUtils.extractGroup(service.getURL(), PatternGroup.URL, PatternGroup.PORT.name());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // PACKET ID
        writeVarInt(0, buffer);
        // PROTOCOL VERSION
        writeVarInt(-1, buffer);
        // SERVER ADDRESS
        writeString(service.getURL(), buffer);
        // SERVER PORT
        writeShort(port == null ? 25565 : Short.parseShort(port), buffer);
        // NEXT STATE
        writeVarInt(1, buffer);
        try (Socket socket = new Socket(host, port == null ? 25565 : Integer.parseInt(port))) {

            int length = buffer.size();

            writeVarInt(length, socket.getOutputStream());

            byte[] byteArrayFromBuffer = buffer.toByteArray();
            writeBytes(byteArrayFromBuffer, socket.getOutputStream());

            socket.getOutputStream().flush();
            writeVarInt(1, buffer);
            writeVarInt(0, buffer);
            socket.getOutputStream().flush();

            int dataLength = readVarInt(socket.getInputStream());
            int packetId = readVarInt(socket.getInputStream());
            String data = readString(socket.getInputStream());



            System.out.println(data);
        }
        return 0;
    }

    public void writeString(final String temp, final OutputStream outputStream) {
        byte[] byteArray = temp.getBytes(StandardCharsets.UTF_8);
        writeVarInt(byteArray.length, outputStream);
        writeBytes(byteArray, outputStream);
    }

    public void writeShort(final short temp, final OutputStream outputStream) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeShort(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readString(final InputStream inputStream) {
        int length = readVarInt(inputStream);
        byte[] array = new byte[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readByte(inputStream);
        }
        return new String(array, StandardCharsets.UTF_8);
    }

    private void writeBytes(byte[] byteArray, OutputStream outputStream) {
        for (byte b :
                byteArray) {
            writeByte(b, outputStream);
        }
    }

    public int readVarInt(final InputStream inputStream) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte(inputStream);
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    private byte readByte(final InputStream inputStream) {
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            return dataInputStream.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void writeVarInt(int value, final OutputStream outputStream) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp, outputStream);
        } while (value != 0);
    }

    private void writeByte(final byte temp, final OutputStream outputStream) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeByte(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
