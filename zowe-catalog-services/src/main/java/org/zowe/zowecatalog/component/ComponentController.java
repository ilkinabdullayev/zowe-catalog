package org.zowe.zowecatalog.component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Zowe Components", description = "REST API for Zowe components")
@RestController
@RequestMapping("/api/v1/components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService componentService;

    @ApiOperation(value = "List component of zowe")
    @GetMapping
    public List<ComponentView> getAllListComponent() {
        return componentService.getAllComponents();
    }

    @ApiOperation(value = "Get zowe component by name")
    @GetMapping("/name/{name}")
    public ComponentView getComponentByName(@PathVariable("name") String name) {
        return componentService.getComponentByName(name);
    }

    @ApiOperation(value = "List component name of zowe")
    @GetMapping("/names")
    public List<String> getAllListComponentNames() {
        return componentService.getAllComponentNames();
    }
}
