package com.LuckyHub.Backend.service;

import java.util.List;
import java.util.Map;

public interface VideoService {
    Map<String, List<String>> fetchComments(List<String> videoIds, String keyword);

    boolean verifySameUser(List<String> videoLinks);
}
