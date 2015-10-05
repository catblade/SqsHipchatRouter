package com.SqsHipchatRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marlow on 10/5/15.
 */
public class SqsHipchatRouterConst {

    public static final String AWS_ACCOUNT_NUMBER = "999999999999";
    public static final String SQS_BASE_URL = "https://queue.amazonaws.com/" + AWS_ACCOUNT_NUMBER + "/";

    public static String AWS_SQS_PREFIX = SQS_BASE_URL + "hipchat-";

    public enum MessageQueues {Q1, Q2, Q3, Q4};
    public final static Map<MessageQueues, String> roomNames = new HashMap<>();

    public final static Map<MessageQueues, String> queueNames = new HashMap<>();

    static {
        roomNames.put(MessageQueues.Q1, "Room1");
        roomNames.put(MessageQueues.Q2, "Room2");
        roomNames.put(MessageQueues.Q3, "Room3");
        roomNames.put(MessageQueues.Q4, "Room4");

        queueNames.put(MessageQueues.Q1, "Q1");
        queueNames.put(MessageQueues.Q1, "Q2");
        queueNames.put(MessageQueues.Q1, "Q3");
        queueNames.put(MessageQueues.Q1, "Q4");
    }
}
