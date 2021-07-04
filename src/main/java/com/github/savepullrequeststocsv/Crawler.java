package com.github.savepullrequeststocsv;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    // repo 仓库名，例如 golang/go
    // 爬取前 n 个 pull request 数据
    // 保存为 csv 文件的格式
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        String url = "https://github.com/" + repo + "/pulls";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (FileWriter fileWriter = new FileWriter(csvFile, true)) {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String htmlString = response.body().string();
                Document document = Jsoup.parse(htmlString);
                Elements issuesList = document.getElementsByClass("js-navigation-container js-active-navigation-container");
                List<Node> nodes = issuesList.get(0).childNodes();
                List<Node> issuesNodes = new ArrayList<>();
                for (int i = 0; i < nodes.size(); i++) {
                    if (i % 2 == 0) {
                        // skip
                    } else {
                        issuesNodes.add(nodes.get(i));
                    }
                }
                // 保存到 CSV 文件当中
                List<String> lines = new ArrayList<>();
                fileWriter.write("number,author,title" + "\n");
                for (int i = 0; i < 10; i++) {
                    Node node = issuesNodes.get(i).childNodes().get(1).childNodes().get(5);
                    Node node1 = node.childNodes().get(1);
                    Node node2 = node.childNodes().get(7).childNodes().get(1).childNodes().get(0);
                    Node node3 = node2.parentNode().childNodes().get(3).childNodes().get(0);

                    String number = node2.toString().substring(2, 7);
                    String author = node3.toString();
                    String title = node1.childNodes().get(0).toString();
                    lines.add(number + "," + author + "," + title);
                }

                for (String line : lines) {
                    fileWriter.write(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Crawler.savePullRequestsToCSV("golang/go", 10, new File("/Users/macbook/Desktop/myProject/save-pull-requests-to-csv/test.csv"));
        Crawler2.savePullRequestsToCSV("golang/go", 10, new File("/Users/macbook/Desktop/myProject/save-pull-requests-to-csv/test.csv"));
    }
}
