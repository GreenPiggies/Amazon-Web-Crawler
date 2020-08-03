import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private static MessageQueue instance = null;
    public LinkedBlockingQueue<String> queue;
    public Set<String> seen;
    private MessageQueue() { queue = new LinkedBlockingQueue<>(); seen = new HashSet<String>(); }
    public static MessageQueue getInstance() {
        if (instance == null) instance = new MessageQueue();
        return instance;
    }
}
