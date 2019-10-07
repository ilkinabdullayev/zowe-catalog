package org.zowe.zowecatalog.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.Component;
import org.zowe.zowecatalog.github.Content;
import org.zowe.zowecatalog.github.ZoweGithubService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoweCatalogService {

    private final ZoweGithubService zoweGithubService;
    private final ObjectMapper objectMapper;

    public List<Catalog> getAllCatalogsFromGithub() {
        List<Catalog> listCatalog = new ArrayList<>();
        zoweGithubService.getManifestJsonContents().forEach((version, listContent) -> {
            log.info("Collected data key:{}, value:{}", version, listContent);
            Content content = listContent.get(0);

            Catalog catalog = new Catalog();
            catalog.setVersion(version);
            catalog.setReleaseDate(content.getCommit().getDateTime());

            try {
                ObjectNode objectNode = objectMapper.readValue(content.getText(), ObjectNode.class);
                JsonNode descriptionNode = objectNode.get("description");
                if (descriptionNode != null) {
                    catalog.setDescription(descriptionNode.asText());
                }

                fillComponents(catalog, objectNode);
                listCatalog.add(catalog);
            } catch (IOException e) {
                log.error("Parse exception commit: {}", version);
            }
        });

        return listCatalog;
    }

    private void fillComponents(Catalog catalog, ObjectNode objectNode) {
        JsonNode sourceDependenciesNode = objectNode.get("sourceDependencies");
        if (sourceDependenciesNode != null) {
            List<Component> components = new ArrayList<>();
            sourceDependenciesNode.forEach(sourceDependency -> {
                Component component = new Component();
                fillComponent(component, sourceDependency);
                components.add(component);
            });

            catalog.setComponents(components);
        }
    }

    private void fillComponent(Component component, JsonNode sourceDependency) {
        JsonNode entriesNode = sourceDependency.get("entries");
        if (entriesNode.size() == 1) {
            JsonNode entryNode = entriesNode.get(0);
            fillComponentName(component, sourceDependency);
            fillComponentRepository(component, entryNode);
            fillComponentVersion(component, entryNode);
        } else {
            entriesNode.forEach(entryNode -> {
                fillComponentRepository(component, entryNode);
                fillComponentVersion(component, entryNode);

                JsonNode componentGroupNode = sourceDependency.get("componentGroup");
                if (componentGroupNode != null) {
                    component.setName(component.getRepository() + "(" + componentGroupNode.asText() + ")");
                }
            });
        }
    }

    private void fillComponentVersion(Component component, JsonNode entryNode) {
        JsonNode tagNode = entryNode.get("tag");
        if (tagNode != null) {
            String version = tagNode.asText();
            if (version.startsWith("v")) {
                version = version.substring(1);
            }

            component.setVersion(version);
        }
    }

    private void fillComponentRepository(Component component, JsonNode entryNode) {
        JsonNode repositoryNode = entryNode.get("repository");
        if (repositoryNode != null) {
            component.setRepository(repositoryNode.asText());
        }
    }

    private void fillComponentName(Component component, JsonNode sourceDependency) {
        JsonNode componentGroupNode = sourceDependency.get("componentGroup");
        if (componentGroupNode != null) {
            component.setName(componentGroupNode.asText());
        }
    }

}
