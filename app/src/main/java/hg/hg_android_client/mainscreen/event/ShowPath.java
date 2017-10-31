package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.select_path.Path;

public class ShowPath {

    private Path selected;

    public ShowPath(Path selected) {
        this.selected = selected;
    }

    public Path getPath() {
        return selected;
    }

}
