# Activity Detector
## Notes
- Frontend is not dockerized at his moment.


## Requirements
- Docker (tested on version 28.0.1)

## Set up
#### Backend
In order for backend to work path to videos folder must be specified.  
Path can be specified by:  
- Setting up `VIDEO_FOLDER_PATH` environment variable to directory video directory absolute path  
OR
- Changing `folderPath` property in `backend\src\main\resources\application.yaml` file.  
###### Backend properties description
- `folderPath` - property specifies the folder from which video files will be registered.  
*Property must be in path\to\dir format*
- `subfolderDepth` - property specifies how many levels of subfolders under `folderPath` will also be 
monitored, where depth `0` means only `folderPath` itself, without any subfolders.  
*Values [0, 4]*


## Docker environment
**To start project containers run:**  
```
docker compose -f docker/docker-compose.yml up 
```
*(--build flag can be added for image rebuilding purposes)*  
*(db container must be up for backend to start without errors!)*
## Backend endpoints

##### /videos
Return JSON of registered video record.

##### /videos/{video_id}
Request for video partial content.

## Frontend endpoint (temporary overview build)

##### /video/{video_id}
Presentation of video with video_id, in simple in-browser player.