# Activity Detector

## Requirements
- Docker (tested on version 28.0.1)

## Set up
#### Backend
In order for backend to work path to videos folder must be specified.  
Path can be specified by:  
- Setting up `VIDEO_FOLDER_PATH` environment variable to directory video directory absolute path  
OR
- Changing `folderPath` property in `backend\src\main\resources\application.yaml` file. *Property must be in path\to\dir format*


## Docker environment
**To start project containers run:**  
```
docker compose -f docker/docker-compose.yml up 
```
*(--build flag can be added for image rebuilding purposes)*

## Backend endpoints
##### /videos/{videoIdentifier}
Request for video partial content. (TO CHANGE: At this moment video names in `IdentifierToVideoMapperService` are hardcoded)