package be.flmr.secmon.probemc.service;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.probe.service.ServiceProber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MinecraftServiceProber implements ServiceProber {
    private final Gson gson;

    public MinecraftServiceProber() {
        this.gson = new Gson();
    }

    @Override
    public int get(IService service) throws IOException {
        String host = PatternUtils.extractGroup(service.getURL(), PatternGroup.URL, PatternGroup.HOST.name());
        String port = PatternUtils.extractGroup(service.getURL(), PatternGroup.URL, PatternGroup.PORT.name());
        port = port == null ? "25565" : port;

        try (Socket socket = new Socket(host, Integer.parseInt(port));
             var in = new DataInputStream(socket.getInputStream());
             var out = new DataOutputStream(socket.getOutputStream())) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(0x00); // Packet ID
            writeVarInt(dos, -1); // Protocol Version
            writeString(dos, host); // Host
            dos.writeShort(Short.parseShort(port)); // Port
            writeVarInt(dos, 0x01); // Next State

            writeVarInt(out, baos.size());
            out.write(baos.toByteArray());

            out.writeByte(0x01); // Length
            out.writeByte(0x00); // Packet ID
            out.flush();

            int length = readVarInt(in);
            if (length <= 0) throw new RuntimeException("Unexpected length");

            int id = readVarInt(in);
            int strLength = readVarInt(in);

            byte[] data = new byte[strLength];
            in.readFully(data);

            JsonObject object = gson.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
            return object.get("players").getAsJsonObject()
                    .get("online").getAsInt();
        }
    }

    public static void writeByte(DataOutputStream buf, byte b) throws IOException {
        buf.writeByte(b);
    }

    public static void writeString(DataOutputStream buf, String str) throws IOException {
        writeVarInt(buf, str.getBytes(StandardCharsets.UTF_8).length);
        for (byte b : str.getBytes(StandardCharsets.UTF_8)) writeByte(buf, b);
    }

    public static void writeVarInt(DataOutputStream buf, int value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(buf, temp);
        } while (value != 0);
    }

    public static byte readByte(DataInputStream buf) throws IOException {
        return buf.readByte();
    }

    public static int readVarInt(DataInputStream buf) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte(buf);
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public String a(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }
}
