# Autocurate

Automatically separate musics from Spotify playlist's by genre

# High-Level Architecture

Autocurate operates on a **Microservice Architecture**, splitting heavy HTTP orchestration/persistence and CPU-bound Machine Learning tasks into specialized services.

## 1. Orchestrator Service (Java / Spring Boot)

- **Port:** `8080`
- **Role:** Handles user authentication, ETL (Extract, Transform, Load) pipelines, and database persistence.
- **Key Components:**
  - `SpotifySyncService`: Orchestrates fetching tracks from Spotify, enriching them with `LastFmService`, and pushing final playlists back to Spotify.
  - `LastFmService`: Fetches crowd-sourced genre tags with a built-in Graceful Degradation pattern (Track -> Artist fallback).
  - `MlClient`: Internal wrapper to communicate securely with the Python ML Service.

## 2. Clustering Engine (Python / FastAPI)

- **Port:** `8000`
- **Role:** A dedicated mathematical microservice for Natural Language Processing (NLP) and K-Means Clustering.
- **Key Components:**
  - `database.py`: Directly queries PostgreSQL, using `STRING_AGG` to flatten relational tags into a Pandas DataFrame.
  - `clustering.py`: Converts text tags into mathematical matrices using `TfidfVectorizer` and groups them using `KMeans`.

## 3. Data Layer (PostgreSQL)

- **Role:** Persists normalized music data.
- **Tables:** `tracks`, `track_tags`, `playlist_proposals`, `proposal_tracks`.

---

# 📡 API Reference

## Orchestator Service [Spring Boot]

### Login

Initiates the OAuth 2.0 PKCE flow with Spotify.

```bash
curl -X GET "http://localhost:8080/api/auth/login"

```

### List User Playlists

Fetches all playlists of the authenticated user.

```bash
curl -X GET "http://localhost:8080/api/spotify/playlists"
```

### Fetch Playlist Tracks

Retrieves all tracks from a specified playlist, including their enriched tags.

```bash
curl -X POST "http://localhost:8080/api/spotify/sync/{playlistId}"
```

### Generate Playlist Proposals

These endpoints manage the AI-generated drafts. They safely store data in PostgreSQL without mutating the user's actual Spotify account until explicitly requested.
Triggers the Python ML engine to cluster the tracks, generates proposals, and saves them to the database.

```bash
curl -X POST "http://localhost:8080/api/spotify/proposals/generate"
```

### List Playlist Proposals

Fetches all generated playlist proposals for the authenticated user.

```bash
curl -X GET "http://localhost:8080/api/spotify/proposals"
```

### Create Spotify Playlist from Proposal

Takes a proposal ID, creates a new playlist in the user's Spotify account, and populates it with the proposed tracks.

```bash
curl -X POST "http://localhost:8080/api/spotify/proposals/{proposalId}/create"
```

## Clustering Service [FastAPI]

### Health Check

```bash
curl -X GET "http://localhost:8000/"
```

### Clustering

Clusters tracks based on their tags and returns proposed playlists.

```bash
curl -X POST "http://localhost:8000/api/ml/cluster"
```

# Directory Structure

```
autocurate
├─ LICENSE
├─ README.md
├─ backend
│  ├─ clustering-service
│  │  └─ src
│  │     ├─ clustering.py
│  │     ├─ database.py
│  │     └─ main.py
│  └─ orchestrator-service
│     ├─ README.md
│     ├─ pom.xml
│     ├─ src
│     │  ├─ main.java.com.autocurate.spotify.clustering
│     │  │  │  ├─ PlaylistClusterServiceApplication.java
│     │  │  │  ├─ client
│     │  │  │  │  ├─ MlClient.java
│     │  │  │  │  └─ SpotifyClient.java
│     │  │  │  ├─ config
│     │  │  │  │  ├─ LastFmProperties.java
│     │  │  │  │  ├─ NetworkConfig.java
│     │  │  │  │  ├─ SecurityConfig.java
│     │  │  │  │  ├─ SpotifyConfig.java
│     │  │  │  │  └─ SpotifyProperties.java
│     │  │  │  ├─ controller
│     │  │  │  │  ├─ AuthController.java
│     │  │  │  │  └─ SpotifyController.java
│     │  │  │  ├─ dto
│     │  │  │  │  ├─ ClusterResponse.java
│     │  │  │  │  ├─ LastFmResponse.java
│     │  │  │  │  ├─ PlaylistProposalResponse.java
│     │  │  │  │  └─ SpotifyItemsResponse.java
│     │  │  │  ├─ exception
│     │  │  │  ├─ model
│     │  │  │  │  ├─ PlaylistProposal.java
│     │  │  │  │  └─ Track.java
│     │  │  │  ├─ repository
│     │  │  │  │  ├─ PlaylistProposalRepository.java
│     │  │  │  │  └─ TrackRepository.java
│     │  │  │  └─ service
│     │  │  │     ├─ LastFmService.java
│     │  │  │     └─ SpotifySyncService.java
│     │  │  └─ resources
│     │  │     ├─ application.yaml
│     │  │     ├─ static
│     │  │     └─ templates
│     │  └─ test
│     │     └─ java
│     │        └─ com
│     │           └─ autocurate
│     │              └─ spotify
│     │                 └─ clustering
│     │                    └─ PlaylistClusterServiceApplicationTests.java
└─ frontend
```
