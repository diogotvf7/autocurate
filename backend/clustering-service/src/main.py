from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn

from clustering import cluster_tracks
from database import fetch_tracks_as_df

app = FastAPI(
    title="Autocurate Clustering Engine",
    description="ML Microservice for Spotify playlist clustering",
    version="1.0.0",
)


class ClusterRequest(BaseModel):
    n_clusters: int = 5


@app.get("/")
def health_check():
    return {"status": "ok", "service": "clustering-engine"}


@app.post("/api/ml/cluster")
def generate_clusters(request: ClusterRequest):
    try:
        print(
            f"DEBUG: Starting clustering process for {request.n_clusters} clusters..."
        )

        df = fetch_tracks_as_df()
        if df is None or df.empty:
            raise HTTPException(
                status_code=404, detail="No tracks found in the database."
            )

        clusters_dict = cluster_tracks(df, n_clusters=request.n_clusters)

        return {
            "status": "success",
            "message": f"Successfully grouped {len(df)} tracks into {request.n_clusters} clusters.",
            "clusters": clusters_dict,
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
