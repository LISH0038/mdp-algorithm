package model.util;

import static constant.MapConstants.MAP_ROWS;
import static constant.RobotConstants.*;

/**
 * Message generator
 */
public class MessageGenerator {

    /**
     * Generate map string for Android communication, note that on Android the coordinate of
     * the robot is the upper right corner.
     * @param descriptor Map descriptor in Android format
     * @param x Robot's x coordinates
     * @param y Robot's y coordinates
     * @param heading Robot's heading
     * @return Message string for sending to Android
     */
    public static String generateMapDescriptorMsg(String descriptor, int x, int y, int heading) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"robot\":\"");
        builder.append(descriptor);
        builder.append(",");
        builder.append(MAP_ROWS - y);
        builder.append(",");
        builder.append(x + 1);
        builder.append(",");
        if (heading == NORTH) {
            builder.append(0);
        } else if (heading == EAST) {
            builder.append(90);
        } else if (heading == SOUTH) {
            builder.append(180);
        } else if (heading == WEST) {
            builder.append(270);
        }
        builder.append("\"}");
        return builder.toString();
    }
}
