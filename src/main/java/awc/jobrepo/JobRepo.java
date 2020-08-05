package awc.jobrepo;

import java.util.List;

public interface JobRepo {

    String getNextLink ();

    void visited (String link);

    void addLinks (List<String> links);

    void addLink (String link);

    boolean hasVisited(String link);

    int size();
}
