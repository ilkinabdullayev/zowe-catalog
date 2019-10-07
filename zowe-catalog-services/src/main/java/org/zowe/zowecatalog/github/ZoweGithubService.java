package org.zowe.zowecatalog.github;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoweGithubService {

    private static final String GITHUB_HOME = "https://github.com";
    private static final String GITHUB_API_HOME = "https://api.github.com";
    private static final String ORG_NAME = "zowe";
    private static final String ZOWE_INSTALL_PACKING_REPO_NAME = "zowe-install-packaging";
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json.template";

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;


    //WIP
    private List<Release> getRepositoryReleases(String repo) {
        List<Release> releases = new ArrayList<>();

        String url = String.format("%s/repos/%s/%s/releases",
                GITHUB_API_HOME,
                ORG_NAME,
                repo);

        Optional<ArrayNode> optionalArrayNode = getArrayResponse(url);
        if (optionalArrayNode.isPresent()) {
            ArrayNode arrayNode = optionalArrayNode.get();
            arrayNode.forEach(an -> {
                Release release = new Release();
               // release.setCommitHash();

                release.setNotes(an.get("body").asText());

                ZonedDateTime zdt = ZonedDateTime.parse(an.get("published_at").asText(), DateTimeFormatter.ISO_DATE_TIME);
                release.setReleaseDate(zdt);

                String version = an.get("tag_name").asText();
                if (version.startsWith("v")) {
                    version = version.substring(1);
                }

                release.setVersion(version);

                releases.add(release);
            });
        }

        return releases;
    }



    public Map<String, List<Content>> getManifestJsonContents() {
        return getAllValidManifestJsonContents()
                .stream()
                .sorted(Comparator.comparing(c -> c.getCommit().getDateTime(), Comparator.reverseOrder()))
                .collect(Collectors.groupingBy(Content::getVersion));
    }

    private List<Content> getAllValidManifestJsonContents() {
        List<Commit> commits = getManifestJsonCommitHashs();
        List<Content> contents = new ArrayList<>();
        commits.forEach(commit -> {
            Optional<String> optionalContent = getManifestJsonContentByCommit(commit.getHash());
            if (optionalContent.isPresent()) {
                String content = replaceNonValidSymbols(optionalContent.get());

                try {
                    ObjectNode objectNode = mapper.readValue(content, ObjectNode.class);
                    JsonNode versionNode = objectNode.get("version");
                    if (versionNode == null) {
                        log.warn("Version field is not exist for {}", commit);
                        return;
                    }

                    contents.add(new Content(versionNode.asText(), content, commit));
                } catch (IOException e) {
                    log.error("Parse exception commit: {}", commit);
                }
            }
        });

        return contents;
    }


    //TODO: PAGE SHOULD BE DYNAMIC
    private List<Commit> getManifestJsonCommitHashs() {
        List<Commit> commits = new ArrayList<>();

        String url = String.format("%s/repos/%s/%s/commits?path=%s&page=1&per_page=1000",
                GITHUB_API_HOME,
                ORG_NAME,
                ZOWE_INSTALL_PACKING_REPO_NAME,
                MANIFEST_JSON_FILE_NAME);

        Optional<ArrayNode> optionalArrayNode = getArrayResponse(url);
        if (optionalArrayNode.isPresent()) {
            ArrayNode arrayNode = optionalArrayNode.get();
            arrayNode.forEach(an -> {
                String commitDate = an.get("commit").get("committer").get("date").asText();
                ZonedDateTime zdt = ZonedDateTime.parse(commitDate, DateTimeFormatter.ISO_DATE_TIME);

                commits.add(new Commit(an.get("sha").asText(), zdt));
            });
        }

        return commits;
    }

    private Optional<String> getManifestJsonContentByCommit(String commit) {
        String url = String.format("%s/%s/%s/raw/%s/%s",
                GITHUB_HOME,
                ORG_NAME,
                ZOWE_INSTALL_PACKING_REPO_NAME,
                commit,
                MANIFEST_JSON_FILE_NAME);
        log.info("Downloading file from {}", url);

        Optional<String> optionalResponse = getResponse(url);
        if (optionalResponse.isPresent()) {
            return Optional.ofNullable(optionalResponse.get());
        }

        return Optional.empty();
    }

    private List<Tag> getRepositoryTags(String repo) {
        List<Tag> tags = new ArrayList<>();

        String url = String.format("%s/repos/%s/%s/tags",
                GITHUB_API_HOME,
                ORG_NAME,
                repo);

        Optional<ArrayNode> optionalArrayNode = getArrayResponse(url);
        if (optionalArrayNode.isPresent()) {
            ArrayNode arrayNode = optionalArrayNode.get();
            arrayNode.forEach(an -> {
                String version = getVersion(an, "name");
                String mergeCommitUrl = an.get("commit").get("url").asText();
                tags.add(new Tag(version, mergeCommitUrl));
            });
        }

        return tags;
    }

    private Optional<ArrayNode> getArrayResponse(String url) {
        Optional<String> optionalResponse = getResponse(url);
        if (optionalResponse.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            try {
                ArrayNode arrayNode = mapper.readValue(optionalResponse.get(), ArrayNode.class);
                return Optional.ofNullable(arrayNode);
            } catch (IOException e) {
                log.error("Could not extract url: {}", url, e);
            }
        }

        return Optional.empty();
    }

    private Optional<String> getResponse(String url) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.ofNullable(response.getBody());
            } else {
                log.error("Could not get response for request: {}, {}={}",
                        url, response.getStatusCode(), response.getBody());
            }
        } catch (RestClientException e) {
            log.error("Could not get response for request: {}", url, e);
        }

        return Optional.empty();
    }

    private String getVersion(JsonNode an, String fieldName) {
        String version = an.get("name").asText();
        if (version.startsWith("v")) {
            version = version.substring(1);
        }

        return version;
    }

    private String replaceNonValidSymbols(String json) {
        return json.replace("{BUILD_COMMIT_HASH}", "0")
                .replace("{BUILD_TIMESTAMP}", "0")
                .replace("{BUILD_BRANCH}", "0")
                .replace("{BUILD_NUMBER}", "0");
    }
}
