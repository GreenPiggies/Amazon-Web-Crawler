package awc.jobrepo;

import awc.csv.Entry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AmazonJobRepo implements JobRepo{

    private static Queue<String> jobQueue = new ConcurrentLinkedQueue<>();

    private static Set<String> visited = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static AmazonJobRepo jobRepo = new AmazonJobRepo("https://www.amazon.com/Echo-Dot/dp/B07FZ8S74R/");

    private static List<Entry> data = new LinkedList<Entry>();

    private static Set<Integer> crawlers = new HashSet<Integer>();

    public static AmazonJobRepo getInstance ()
    {
        return jobRepo;
    }

    private AmazonJobRepo (String seedUrl)
    {
        addLink(seedUrl);
    }

    public boolean hasNextLink() {
        return !jobQueue.isEmpty();
    }

    public boolean isRunning(int num) {
        return crawlers.contains(num);
    }

    public void start(int num) {
        crawlers.add(num);
    }

    public void end(int num) {
        crawlers.remove(num);
    }

    public boolean stillRunning() {return !crawlers.isEmpty();}



    public String getNextLink ()
    {
        return jobQueue.poll();
    }

    public void visited (String link)
    {
        visited.add(link);
    }

    public void addLinks (List<String> links)
    {
        links.forEach(link -> this.addLink(link));
    }

    public void addLink (String link)
    {
        if (!visited.contains(link)) {
            jobQueue.offer(link);
        }
    }

    public void addData(Entry line) {
        data.add(line);
    }

    public List<Entry> getData() {
        return data;
    }

    public boolean hasVisited(String link) {
        return visited.contains(link);
    }

    public int size() {
        return visited.size();
    }

}
