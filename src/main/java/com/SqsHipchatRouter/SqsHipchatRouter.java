package com.SqsHipchatRouter;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.Executors;


/**
 * Created by marlow on 10/5/15.
 */
public class SqsHipchatRouter {
    /**
     * Created by marlow on 9/18/15.
     */
    public static void main(String[] args) {

        for ( SqsHipchatRouterConst.MessageQueues queue : SqsHipchatRouterConst.MessageQueues.values() ) {
            // special group to catch all dev traffic and keep our channels clear
                sqsUrlNames.put(queue,
                        SqsHipchatRouterConst.AWS_SQS_PREFIX + SqsHipchatRouterConst.roomNames.get(queue));
            }
        }
        for ( SqsHipchatRouterConst.MessageGroup messageGroup : SqsHipchatRouterConst.MessageGroup.values() ) {

            String sqsQueueUrl = sqsUrlNames.get(messageGroup);
            AmazonSQSAsync sqs = createSQSAsyncClient(10);

            MessageProvider messageProvider = new AmazonSQSMessageProvider(sqs, sqsQueueUrl);
            HipchatMessageHandler hipchatMessageHandler = new HipchatMessageHandler(messageProvider,
                    messageGroup);

            MessageQueueReader<String, Void> messageQueueReader = new MessageQueueReader<>(
                    messageProvider,
                    hipchatMessageHandler,
                    Executors.newSingleThreadExecutor());

            // create a producer thread and kick off everything
            Thread producer = new Thread(messageQueueReader, "Producer-Thread");

            producer.start();
        }
    }

    public static class HipchatMessageHandler extends AbstractMessageHandler<String, Void> {

        private final MessageProvider messageProvider;
        private final BlNotifyAwsNames.MessageGroup messageGroup;

        public HipchatMessageHandler(MessageProvider messageProvider,
                                     SqsHipchatRouterConst.MessageGroup messageGroup) {
            this.messageProvider = messageProvider;
            this.messageGroup = messageGroup;
        }

        @Override
        public Void process(String message) throws Exception {
            return null;
        }

        @Override
        public String convert(Message queueItem) throws Exception {
            WireNotify wireMessage = Babel.fromJson(WireNotify.class, queueItem.getBody());
            String formattedMessage = wireMessage.getTimestamp() + " " + wireMessage.getSubject() + " : "
                    +  wireMessage.getMessage();
            HipchatMessageSender.getDefaultInstance().sendMessage(
                    BlNotifyHipchatConst.roomNames.get(this.messageGroup),
                    formattedMessage);
            return formattedMessage;
        }

        @Override
        public void onSuccess(Message message, String converted, Void result) {
                return;
            }

        @Override
        public void onError(Message message, String converted, Throwable throwable) {
            HipchatMessageSender.getDefaultInstance().sendMessage(
                    SqsHipchatRouterConst.roomNames.get(this.messageGroup),
                    "Error while fetching message for BlNotifyHipchatRouter");
        }

        @Override
        public void onComplete(Message message, String converted, Void result) {
                messageProvider.delete(message);
            }
    }
}
