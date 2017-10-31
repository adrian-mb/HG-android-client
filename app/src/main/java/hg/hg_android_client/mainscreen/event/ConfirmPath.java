package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.select_path.Path;

public class ConfirmPath {

    private Path selected;

    public ConfirmPath(Path selected) {
        this.selected = selected;
    }

    public Path getPath() {
        return selected;
    }

}
