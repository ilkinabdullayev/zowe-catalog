package org.zowe.zowecatalog.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.catalog.repository.CatalogH2;
import org.zowe.zowecatalog.catalog.repository.CatalogRepository;
import org.zowe.zowecatalog.component.Component;
import org.zowe.zowecatalog.github.GithubService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogFetcher {

    private static final List<String> SPECIAL_CATALOG_COMMITS = Arrays.asList(
            "2e795dfbe675839aa9bb301fa6ef029f9aa920cf",
            "b07767b7c51d6b316baada360b1d5df9d58433fb",
            "8214f25e45c912206bbc9ed30557642de2cd754e",
            "ad2ea5e622e27ffd7e1dd84c85493bbec39e4770",
            "9ce24b6b0574fff756eae6a5778affcc7f5d67a0",
            "99b603ed6220be947828becc71e377a9f80c1a89",
            "e8e749200501b3d1442082816a0f94a37c5e4e81");

    private final GithubService zoweGithubService;
    private final CatalogRepository catalogRepository;
    private final ObjectMapper objectMapper;

    public List<Catalog> getAllCatalogsFromFolder() {
        List<Catalog> listCatalog = new ArrayList<>();

        List<String> contents = catalogRepository.findAll()
                .stream()
                .map(CatalogH2::getContentJson)
                .collect(Collectors.toList());
        contents.forEach(content -> {
            try {
                Catalog catalog = objectMapper.readValue(content, Catalog.class);
                listCatalog.add(catalog);
            } catch (IOException e) {
                log.error("Parse exception content: {}", content);
            }
        });

        return listCatalog;
    }

    public List<Catalog> getAllCatalogsFromGithub() {
        List<Catalog> listCatalog = new ArrayList<>();

        zoweGithubService.getListManifestJsonContents()
                .stream()
                .filter(content -> SPECIAL_CATALOG_COMMITS.contains(content.getCommit().getHash()))
                .forEach(content -> {
                    String version = content.getVersion();
                    log.info("Collected data content: {}", content);

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
