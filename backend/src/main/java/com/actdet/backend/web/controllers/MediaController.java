package com.actdet.backend.web.controllers;

import com.actdet.backend.data.entities.Video;
import com.actdet.backend.data.repositories.VideoRepository;
import com.actdet.backend.services.VideoSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class MediaController {

    private final VideoSupplierService videoSupplierService;
    private final VideoRepository videoRepository;


    @Autowired
    public MediaController(VideoSupplierService videoSupplierService, VideoRepository videoRepository) {
        this.videoSupplierService = videoSupplierService;
        this.videoRepository = videoRepository;
    }

    @GetMapping("/{fileIdentifier}")
    public ResponseEntity<ResourceRegion> getVideoMedia(@RequestHeader HttpHeaders headers,
                                                        @PathVariable String fileIdentifier) throws IOException {
        ResourceRegion resource = videoSupplierService.getVideoResourceRegion(fileIdentifier, headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resource.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .contentLength(resource.getResource().contentLength())
                .body(resource);
    }


    //Tymczasowy endpoint do podgladu jakie pliki sie zapisaly
    @GetMapping("")
    public List<Video> getAllVideos(){
        return videoRepository.findAll();
    }

}
