package be.flmr.secmon.core.protocols;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRouter {
    private Map<Method, String> protocols;

    public void execute(String input) {
        for(Map.Entry<Method, String> entry : protocols.entrySet()) {
            Method method = entry.getKey();
            String regex = entry.getValue();
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(input);
            final String[] matches = new String[matcher.groupCount()];

            if(matcher.matches()) {
                for (int i = 1; i <= matches.length; ++i)
                    matches[i - 1] = matcher.group(1);
                try {
                    method.setAccessible(true);
                    method.invoke(this, (Object[]) matches);
                    method.setAccessible(false);
                } catch(IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}