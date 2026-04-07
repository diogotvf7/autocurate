import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans


def cluster_tracks(df: pd.DataFrame, n_clusters: int = 5) -> dict:
    """
    Takes a DataFrame of tracks, vectorizes their tags,
    and clusters them into n_clusters.
    """
    # 1. Clean the data (Replace any missing tags with empty strings)
    df["all_tags"] = df["all_tags"].fillna("")  # Handle missing tags

    # 2. Vectorize the tags
    vectorizer = TfidfVectorizer(tokenizer=lambda x: x.split(","), token_pattern=None)
    X = vectorizer.fit_transform(df["all_tags"])

    # 3. Run K-Means Clustering
    kmeans = KMeans(n_clusters=n_clusters, random_state=42)
    df["cluster"] = kmeans.fit_predict(X)

    order_centroids = kmeans.cluster_centers_.argsort()[:, ::-1]
    terms = vectorizer.get_feature_names_out()

    # 4. Format the output for FastAPI to send back to Java
    result_clusters = {}
    for i in range(n_clusters):
        top_tags = [terms[ind].title() for ind in order_centroids[i, :5]]

        cluster_tracks = df[df["cluster"] == i]["spotify_id"].tolist()

        result_clusters[str(i)] = {
            "topTags": top_tags,
            "tracks": cluster_tracks,
        }

    return result_clusters
