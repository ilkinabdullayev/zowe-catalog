package org.zowe.zowecatalog.github;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Commit {

    private String hash;
    private ZonedDateTime dateTime;
}
