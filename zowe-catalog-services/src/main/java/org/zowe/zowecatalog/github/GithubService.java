package org.zowe.zowecatalog.github;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.zowe.zowecatalog.release.Release;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {

    private static final String GITHUB_HOME = "https://github.com";
    private static final String GITHUB_API_HOME = "https://api.github.com";
    private static final String ORG_NAME = "zowe";
    private static final String ZOWE_INSTALL_PACKING_REPO_NAME = "zowe-install-packaging";
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json.template";

    private static final List<String> EXCLUDED_TAG_NAMES = Arrays.asList(
            "master", "staging", "lts-incremental"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Value("${github.token}")
    private String githubToken;

    public Map<String, List<Content>> getMapJsonContents() {
        return getListManifestJsonContents()
                .stream()
                .sorted(Comparator.comparing(c -> c.getCommit().getDateTime(), Comparator.reverseOrder()))
                .collect(Collectors.groupingBy(Content::getVersion));
    }

    public List<Content> getListManifestJsonContents() {
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

    public List<Release> getRepositoryReleases(String repo) {
        List<Release> releases = new ArrayList<>();
        List<Tag> tags = getRepositoryTags(repo);
        tags.parallelStream().forEach(tag -> {
            if (EXCLUDED_TAG_NAMES.contains(tag.getName())) {
                log.warn("{} is included EXCLUDED_TAG_NAMES. skipped", tag.getName());
                return;
            }

            Release release = getRelease(tag, repo);
            releases.add(release);
        });


        return releases;
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

    //TODO: PAGE SHOULD BE DYNAMIC
    private List<Commit> getManifestJsonCommitHashs() {
        String url = String.format("%s/repos/%s/%s/commits?path=%s&page=1&per_page=1000",
                GITHUB_API_HOME,
                ORG_NAME,
                ZOWE_INSTALL_PACKING_REPO_NAME,
                MANIFEST_JSON_FILE_NAME);

        return getGitObjectCommits(url);
    }

    //TODO: PAGE SHOULD BE DYNAMIC
    private List<Commit> getGitObjectCommits(String url) {
        List<Commit> commits = new ArrayList<>();

        Optional<ArrayNode> optionalArrayNode = getArrayResponse(url + "&page=1&per_page=1000");
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


    //TODO: PAGE SHOULD BE DYNAMIC
    private List<Tag> getRepositoryTags(String repo) {
        List<Tag> tags = new ArrayList<>();

        String url = String.format("%s/repos/%s/%s/tags?page=1&per_page=1000",
                GITHUB_API_HOME,
                ORG_NAME,
                repo);

        Optional<ArrayNode> optionalArrayNode = getArrayResponse(url);
        if (optionalArrayNode.isPresent()) {
            ArrayNode arrayNode = optionalArrayNode.get();
            arrayNode.forEach(an -> {
                String version = getVersion(an, "name");
                String mergeCommitHash = an.get("commit").get("sha").asText();
                tags.add(new Tag(version, mergeCommitHash));
            });
        }

        return tags;
    }

    private Release getRelease(Tag tag, String repo) {
        Release release = new Release();
        release.setVersion(tag.getName());
        release.setCommitHash(tag.getMergeCommitHash());

        boolean filled = filledReleaseFromTagApi(repo, release);
        if (!filled) {
            fillReleaseFromCommitApi(repo, release);
        }

        return release;
    }

    private boolean filledReleaseFromTagApi(String repo, Release release) {
        String url = String.format("%s/repos/%s/%s/releases/tags/v%s",
                GITHUB_API_HOME,
                ORG_NAME,
                repo,
                release.getVersion());

        Optional<ObjectNode> optionalContent = getObjectResponse(url);
        if (optionalContent.isPresent()) {
            ObjectNode objectNode = optionalContent.get();
            release.setNotes(objectNode.get("body").asText());

            ZonedDateTime zdt = ZonedDateTime.parse(objectNode.get("published_at").asText(), DateTimeFormatter.ISO_DATE_TIME);
            release.setReleaseDate(zdt);

            return true;
        }

        return false;
    }

    private void fillReleaseFromCommitApi(String repo, Release release) {
        String url = String.format("%s/repos/%s/%s/commits/%s",
                GITHUB_API_HOME,
                ORG_NAME,
                repo,
                release.getCommitHash());

        Optional<ObjectNode> optionalContent = getObjectResponse(url);
        if (optionalContent.isPresent()) {
            ObjectNode objectNode = optionalContent.get();
            ZonedDateTime zdt = ZonedDateTime.parse(objectNode.get("commit").get("committer").get("date").asText(), DateTimeFormatter.ISO_DATE_TIME);
            release.setReleaseDate(zdt);
        }
    }

    private Optional<ObjectNode> getObjectResponse(String url) {
        Optional<String> optionalResponse = getResponse(url);
        if (optionalResponse.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            try {
                ObjectNode objectNode = mapper.readValue(optionalResponse.get(), ObjectNode.class);
                return Optional.ofNullable(objectNode);
            } catch (IOException e) {
                log.error("Could not extract url: {}", url, e);
            }
        }

        return Optional.empty();
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
            // Check if the token is not null or empty
            if (StringUtils.hasText(githubToken)) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.AUTHORIZATION, "token " + githubToken);
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(null, headers),
                        String.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return Optional.ofNullable(response.getBody());
                } else {
                    log.error("Could not get response for request: {}, {}={}", url, response.getStatusCode(), response.getBody());
                }
            } else {
                log.error("Invalid GitHub token");
            }
        } catch (RestClientException e) {
            log.error("Could not get response for request: {}", url);
        }

        return Optional.empty();
    }

    private String getVersion(JsonNode an, String fieldName) {
        String version = an.get(fieldName).asText();
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
