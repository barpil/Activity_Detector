package com.actdet.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class DataRefreshService {

    private final VideoFilesWatcherService videoFilesWatcherService;


    public DataRefreshService(@Value("${activity-detector.video.folderPath}") String relativeFolderPath,
                              @Value("${activity-detector.video.subfolderDepth}") int subfolderDepth) throws IOException {
        //Aktualnie sciezka do katalogu jest wzgledem katalogu w ktorym uruchamiamy projekt
        Path baseDir = Paths.get("").toAbsolutePath();

        Path videoFolderPath = baseDir.resolve(relativeFolderPath);

        this.videoFilesWatcherService = new VideoFilesWatcherService(videoFolderPath, subfolderDepth);
    }

    public void startWatcher(){
        this.videoFilesWatcherService.start();
    }

    public void stopWatcher(){
        this.videoFilesWatcherService.stop();
    }

    static class VideoFilesWatcherService{
        private final Set<String> SUPPORTED_EXTENSIONS = Set.of("mp4", "mov");


        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final WatchService watchService;
        private final Map<WatchKey, Path> pathMap;

        private final int rootDepth;
        private final int maxSubfolderDepth;

        private volatile boolean running = true;

        public VideoFilesWatcherService(Path directoryToWatch, int subfolderDepth) throws IOException {
            this.rootDepth = StringUtils.countOccurrencesOf(directoryToWatch.toString(), "\\");
            this.maxSubfolderDepth = subfolderDepth;
            this.pathMap = new HashMap<>();
            this.watchService = directoryToWatch.getFileSystem().newWatchService();
            registerAll(directoryToWatch, subfolderDepth);

            Thread thread = new Thread(this::processEvents);
            thread.setDaemon(true);
            thread.start();
            logger.info("Watcher started.");
        }

        public void start() {
            this.running=true;
        }

        public void stop() {
            this.running=false;
        }

        private void register(Path dirPath) throws IOException {
            WatchKey watchKey = dirPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );
            pathMap.put(watchKey, dirPath);
        }

        //Zwraca ilość zarejestrowanych folderow
        public void registerAll(Path dirPath, int subDirDepth) throws IOException {
            long registeredDirCount = 0;
            try(Stream<Path> fileStream = Files.walk(dirPath, subDirDepth)){
                registeredDirCount = fileStream
                        .filter(Files::isDirectory)
                        .filter(path -> {
                            try{
                                register(path);
                                return true;
                            } catch (IOException e) {
                                logger.error("Could not register directory: {}", path);
                                return false;
                            }
                        }).count();
            }
            if(registeredDirCount <1) throw new IOException("Could not register any directories under data watcher!");
            logger.info("{} directories have been registered by data watcher.", registeredDirCount);
        }

        private void processEvents(){
            while (running){
                WatchKey key;
                try{
                    key = watchService.take();
                } catch(InterruptedException e){
                    return;
                }

                Path dir = this.pathMap.get(key);
                if(dir == null) continue;

                for(WatchEvent<?> event : key.pollEvents()){
                    WatchEvent.Kind<?> kind = event.kind();

                    Path name = (Path) event.context();
                    Path child = dir.resolve(name);

                    if(kind == StandardWatchEventKinds.ENTRY_DELETE){
                        onFileDeleted(child);
                    }else if(kind == StandardWatchEventKinds.ENTRY_CREATE){
                        onFileCreated(child);
                    }
                    //Musze pamietac ze zmiana nazwy pliku to najpierw dla watchera jest rownoznaczna
                    //z najpierw DELETE a potem CREATE
                }

                boolean valid = key.reset();
                if (!valid) {
                    pathMap.remove(key);
                    if (pathMap.isEmpty()) {
                        logger.error("No directories are longer watched! Check if this is an appropriate behavior.");
                        break;
                    }
                }

            }
        }

        private boolean hasSupportedExtension(Path path) {
            String fileName = path.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
            return SUPPORTED_EXTENSIONS.contains(extension);
        }


        private void onFileDeleted(Path deletedFilePath){
            if(hasSupportedExtension(deletedFilePath)){
                logger.info("Usunieto plik video: {}", deletedFilePath);
            }
        }

        private void onFileCreated(Path createdFilePath){
            if(Files.isDirectory(createdFilePath)){
                int createdDirDepth = StringUtils.countOccurrencesOf(createdFilePath.toString(), "\\");
                if(this.maxSubfolderDepth>=createdDirDepth-this.rootDepth){
                    try{
                        register(createdFilePath);
                    } catch (IOException e) {
                        logger.error("Could not register new directory ({}) in data watcher.", createdFilePath);
                    }
                }else{
                    logger.warn("Directory {} was not registered as its depth exceeds subfolderDepth={}.", createdFilePath, this.maxSubfolderDepth);
                }
                return;
            }
            if(hasSupportedExtension(createdFilePath)){
                logger.info("Dodano plik video: {}", createdFilePath);
            }
        }


    }

}
