package com.github.savepullrequeststocsv;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler2 {
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub gitHub = GitHub.connectAnonymously();
        GHRepository repository = gitHub.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);
        List<String> list = new ArrayList<>();
        list.add("number,author,title");
        for (GHPullRequest pullRequest : pullRequests) {
            list.add(getLine(pullRequest));
            if (list.size() > n) {
                break;
            }
        }
        try (FileWriter fileWriter = new FileWriter(csvFile, true)) {
            for (String line : list) {
                fileWriter.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String getLine(GHPullRequest pullRequest) {
        try {
            return pullRequest.getNumber() + "," + pullRequest.getUser().getLogin() + "," + pullRequest.getTitle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
