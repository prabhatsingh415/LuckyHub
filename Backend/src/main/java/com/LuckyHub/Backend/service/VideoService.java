package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.Comment;
import com.LuckyHub.Backend.model.SubscriptionTypes;

import java.util.List;
import java.util.Map;

public interface VideoService {
    List<Comment> fetchComments(List<String> videoIds, String keyword, SubscriptionTypes plan);

    boolean verifySameUser(List<String> videoLinks);

    List<Comment> selectWinner(List<Comment> fetchedComments, int numberOfWinners);
}
