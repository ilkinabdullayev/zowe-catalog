package org.zowe.zowecatalog.github;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class Release {
    private String commitHash;
    private ZonedDateTime releaseDate;
    private String version;
    private String notes;
}
