package org.zowe.zowecatalog.release;

import lombok.Data;


@Data
public class ReleaseView {
    private String version;
    private String commitHash;
    private String releaseDate;
    private String notes;

    public ReleaseView() {
    }

    public ReleaseView(String version) {
        this.version = version;
    }
}
