
```
orchestrator-service
├─ .env
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ HELP.md
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com
│  │  │     └─ autocurate
│  │  │        └─ spotify
│  │  │           └─ clustering
│  │  │              ├─ PlaylistClusterServiceApplication.java
│  │  │              ├─ client
│  │  │              │  └─ SpotifyClient.java
│  │  │              ├─ config
│  │  │              │  ├─ LastFmProperties.java
│  │  │              │  ├─ NetworkConfig.java
│  │  │              │  ├─ SecurityConfig.java
│  │  │              │  ├─ SpotifyConfig.java
│  │  │              │  └─ SpotifyProperties.java
│  │  │              ├─ controller
│  │  │              │  ├─ AuthController.java
│  │  │              │  └─ SpotifyController.java
│  │  │              ├─ dto
│  │  │              │  ├─ LastFmResponse.java
│  │  │              │  └─ SpotifyItemsResponse.java
│  │  │              ├─ exception
│  │  │              ├─ model
│  │  │              │  └─ Track.java
│  │  │              ├─ repository
│  │  │              │  └─ TrackRepository.java
│  │  │              └─ service
│  │  │                 ├─ LastFmService.java
│  │  │                 └─ SpotifySyncService.java
│  │  └─ resources
│  │     ├─ application.yaml
│  │     ├─ static
│  │     └─ templates
│  └─ test
│     └─ java
│        └─ com
│           └─ autocurate
│              └─ spotify
│                 └─ clustering
│                    └─ PlaylistClusterServiceApplicationTests.java
└─ target
   ├─ classes
   │  ├─ META-INF
   │  │  └─ spring-configuration-metadata.json
   │  ├─ application.yaml
   │  └─ com
   │     └─ autocurate
   │        └─ spotify
   │           └─ clustering
   │              ├─ PlaylistClusterServiceApplication.class
   │              ├─ client
   │              │  └─ SpotifyClient.class
   │              ├─ config
   │              │  ├─ LastFmProperties.class
   │              │  ├─ NetworkConfig.class
   │              │  ├─ SecurityConfig.class
   │              │  ├─ SpotifyConfig.class
   │              │  └─ SpotifyProperties.class
   │              ├─ controller
   │              │  ├─ AuthController.class
   │              │  └─ SpotifyController.class
   │              ├─ dto
   │              │  ├─ LastFmResponse$Tag.class
   │              │  ├─ LastFmResponse$Toptags.class
   │              │  ├─ LastFmResponse.class
   │              │  ├─ SpotifyItemsResponse$PlaylistItem$Track$Artist.class
   │              │  ├─ SpotifyItemsResponse$PlaylistItem$Track.class
   │              │  ├─ SpotifyItemsResponse$PlaylistItem.class
   │              │  └─ SpotifyItemsResponse.class
   │              ├─ model
   │              │  └─ Track.class
   │              ├─ repository
   │              │  └─ TrackRepository.class
   │              └─ service
   │                 ├─ LastFmService.class
   │                 └─ SpotifySyncService.class
   ├─ generated-sources
   │  └─ annotations
   ├─ generated-test-sources
   │  └─ test-annotations
   ├─ maven-status
   │  └─ maven-compiler-plugin
   │     ├─ compile
   │     │  └─ default-compile
   │     │     ├─ createdFiles.lst
   │     │     └─ inputFiles.lst
   │     └─ testCompile
   │        └─ default-testCompile
   │           ├─ createdFiles.lst
   │           └─ inputFiles.lst
   └─ test-classes
      └─ com
         └─ autocurate
            └─ spotify
               └─ clustering
                  └─ PlaylistClusterServiceApplicationTests.class

```