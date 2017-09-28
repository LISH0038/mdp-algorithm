package model.util;

/**
 * Created by koallen on 22/9/17.
 */
public class MessageGenerator {

    public static String generateMapDescriptorMsg(String descriptor) {
        return "{\"grid\" : \"" + descriptor + "\"}";
    }
}
