package com.actdet.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoSupplierService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IdentifierToVideoMapperService identifierToVideoMapperService;

    public VideoSupplierService(IdentifierToVideoMapperService identifierToVideoMapperService) {
        this.identifierToVideoMapperService = identifierToVideoMapperService;
    }

    public ResourceRegion getVideoResourceRegion(String fileIdentifier, HttpHeaders headers) {
        Resource videoMedia = new FileSystemResource(identifierToVideoMapperService.getVideoPathForIdentifier(fileIdentifier));
        return getVideoResourceRegion(videoMedia, headers);
    }

    private ResourceRegion getVideoResourceRegion(Resource media, HttpHeaders headers){
        List<HttpRange> rangeList = headers.getRange();
        HttpRange range;
        if(rangeList.isEmpty()){
            logger.error("Missing range header! Result not returned.");
            throw new RuntimeException("Missing range header! Result not returned.");
        } else if (rangeList.size()>1) logger.warn("Header has more than one range. Check if this is an appropriate behaviour.");
        range = rangeList.getFirst();
        return range.toResourceRegion(media);
    }


}
