package com.skcraft.launcher.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class RedditUtils {

    private static final String API = "https://reddit.com/r/%s.json?count=25&after=%s";

    public static List<String> getBackgrounds(final String subreddit, final int count) {
        String after = "";
        List<String> results = new LinkedList<String>();

        while (results.size() < count) {
            String url = String.format(RedditUtils.API, subreddit, after);
            JsonNode page = getPage(url);

            if (page != null && page.has("data")) {
                JsonNode data = page.get("data");
                visit(data, results, count);

                if (!data.has("after")) {
                    break;
                }

                after = data.get("after").asText();
                if (after == null) {
                    break;
                }
            }
        }

        return results;
    }

    private static JsonNode getPage(String address) {
        HttpRequest request = null;
        try {
            request = HttpRequest.get(new URL(address));
            return new ObjectMapper().readTree(request.execute().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closer.close(request);
        }
        return null;
    }

    private static void visit(JsonNode data, List<String> collector, int limit) {
        if (data.has("children")) {
            JsonNode children = data.get("children");
            for (JsonNode child : children) {
                if (child.has("data")) {
                    visitChild(child.get("data"), collector);
                }
                if (collector.size() >= limit) {
                    return;
                }
            }
        }
    }

    private static void visitChild(JsonNode data, List<String> collector) {
        // ignore videos/albums
        if (data.has("media_embed") && data.get("media_embed").size() > 0) {
            return;
        }

        if (data.has("preview")) {
            JsonNode preview = data.get("preview");
            if (preview.has("images")) {
                JsonNode images = preview.get("images");
                for (JsonNode image : images) {
                    if (image.has("source")) {
                        JsonNode source = image.get("source");
                        if (source.has("url")) {
                            String url = source.get("url").asText();
                            if (!url.contains(".gif")) {
                                url = url.replaceAll("amp;", "");
                                collector.add(url);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
