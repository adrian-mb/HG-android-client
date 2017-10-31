package hg.hg_android_client.mainscreen.event;

import java.util.List;

import hg.hg_android_client.mainscreen.select_path.Path;

public class PathResponse {

    private List<Path> retrieved;

    public PathResponse(List<Path> retrieved) {
        this.retrieved = retrieved;
    }

    public List<Path> getRetrievedPaths() {
        return retrieved;
    }

}
