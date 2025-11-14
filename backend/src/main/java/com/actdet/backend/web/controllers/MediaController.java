package com.actdet.backend.web.controllers;

import com.actdet.backend.services.VideoSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/videos")
public class MediaController {

    private VideoSupplierService videoSupplierService;

    @Autowired
    public MediaController(VideoSupplierService videoSupplierService) {
        this.videoSupplierService = videoSupplierService;
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

}
