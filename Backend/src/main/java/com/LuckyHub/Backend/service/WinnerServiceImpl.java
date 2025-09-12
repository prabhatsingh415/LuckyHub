package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.exception.VideosFromDifferentChannelsException;
import com.LuckyHub.Backend.model.WinnerRequest;
import com.LuckyHub.Backend.model.WinnerResponse;
import org.springframework.stereotype.Service;

@Service
public class WinnerServiceImpl implements WinnerService {

    private final VideoService videoService;

    public WinnerServiceImpl(VideoService videoService) {
        this.videoService = videoService;
    }

    @Override
    public WinnerResponse findWinner(WinnerRequest request) {

         if(!videoService.verifySameUser(request.getVideoLinks()))
              throw new VideosFromDifferentChannelsException("All provided videos must be from the same channel.");



        return null;
    }
}
