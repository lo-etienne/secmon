package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceJSONParser extends TypeAdapter<IService> {

    public static void main(String[] args) {
        List<IService> services = new ArrayList<>();

        services.add(new Service("snmp1!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.4.11.0!10000!99999999!120"));
        services.add(new Service("snmp2!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.4.11.0!10000!99999999!120"));
        services.add(new Service("snmp3!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.4.11.0!10000!99999999!120"));

        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(IService.class, new ServiceJSONParser()).create();

        String str = gson.toJson(services);

        var temp = (List<IService>) gson.fromJson(str, new TypeToken<List<Service>>() {}.getType());
        temp.stream().map(IService::getAugmentedURL).forEach(System.out::println);

    }

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
