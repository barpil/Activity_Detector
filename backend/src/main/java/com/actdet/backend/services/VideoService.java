package com.actdet.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class VideoService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Path videoFolderPath;

    public VideoService(@Value("${activity-detector.video.folderPath}") String relativeFolderPath) {
        //Aktualnie sciezka do katalogu jest wzgledem katalogu w ktorym uruchamiamy projekt
        Path baseDir = Paths.get("").toAbsolutePath();

        this.videoFolderPath =  baseDir.resolve(relativeFolderPath);
        logger.info("IdentifierToVideoMapperService has been initialized. Video files will be read from: {}", this.videoFolderPath);
    }

    public Path getVideoPathForIdentifier(String videoIdentifier){
        String fileName = getFileNameForId(videoIdentifier);
        return videoFolderPath.resolve(fileName);
    }

    private String getFileNameForId(String id){
        //Tymczasowe mapowanie statyczne, potem przerobic na czytanie z bazy
        switch (id){
            case "1":
                return "test_video1.mov";
            case "2":
                return "test_video2.mp4";
            default:
                throw new RuntimeException("WIDEO NIEZNANE: "+id);
        }
    }


}
