package org.zowe.zowecatalog;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseView {
    private String commitHash;
    private LocalDateTime releaseDate;
    private String version;
}
