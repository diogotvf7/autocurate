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


@app.get("/")
def health_check():
    return {"status": "ok", "service": "clustering-engine"}


@app.post("/api/ml/cluster")
def generate_clusters():
    try:
        print(f"Received clustering request.")

        df = fetch_tracks_as_df()
        if df is None or df.empty:
            raise HTTPException(
                status_code=404, detail="No tracks found in the database."
            )

        clusters_dict = cluster_tracks(df)

        return {
            "status": "success",
            "message": f"Successfully grouped {len(df)} tracks into {len(clusters_dict)} clusters.",
            "clusters": clusters_dict,
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
