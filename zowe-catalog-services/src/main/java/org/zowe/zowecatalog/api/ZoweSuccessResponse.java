package org.zowe.zowecatalog.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ZoweSuccessResponse<T> {
    private List<T> data;
}
