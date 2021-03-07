package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ServiceJSONParser extends TypeAdapter<IService> {

    @Override
    public void write(JsonWriter out, IService value) throws IOException {
        out.value(value.getAugmentedURL());

    }

    @Override
    public IService read(JsonReader in) throws IOException {
        while(in.hasNext()) {
            if (in.peek() == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
            }
            if (in.peek() == JsonToken.STRING) {
                return new Service(in.nextString());
            }
        }
        System.out.println(in);
        return new Service(in.nextString());
    }
}
