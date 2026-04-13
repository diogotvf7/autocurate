import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
import collections

# 1. NOISE FILTER (Domain-Specific Stopwords)
USELESS_TAGS = {
    "world",
    "vocalist",
    "female",
    "male",
    "female vocalists",
    "male vocalists",
    "mus",
    "music",
    "deusas",
    "seen live",
    "loved",
    "beautiful",
    "awesome",
    "favorite",
    "favourites",
    "all",
    "misc",
}

# 2. ALIAS DICTIONARY (Semantic Mapping)
TAG_ALIASES = {
    "portugal": "portuguese",
    "portugues": "portuguese",
    "pt": "portuguese",
    "brasil": "brazilian",
    "br": "brazilian",
    "electronica": "electronic",
    "edm": "electronic",
}


def robust_tokenizer(text: str):
    if not isinstance(text, str):
        return []
    return [tag.strip().lower() for tag in text.split(",") if tag.strip()]


def cluster_tracks(df: pd.DataFrame, max_clusters: int = 10) -> dict:
    df["all_tags"] = df["all_tags"].fillna("")

    # --- 1. TEXT CLEANING PIPELINE ---
    cleaned_docs = []

    for tags_string in df["all_tags"]:
        raw_tags = robust_tokenizer(tags_string)
        processed_tags = []

        for tag in raw_tags:
            # Step A: Apply Aliases (e.g., 'portugal' -> 'portuguese')
            mapped_tag = TAG_ALIASES.get(tag, tag)

            # Step B: Filter out useless noise and tiny 1-letter glitches
            if mapped_tag not in USELESS_TAGS and len(mapped_tag) > 2:
                processed_tags.append(mapped_tag.replace(" ", "_"))

        cleaned_docs.append(" ".join(processed_tags))

    df["cleaned_tags"] = cleaned_docs

    # --- 2. VECTORIZATION ---
    vectorizer = TfidfVectorizer(min_df=0.05, token_pattern=r"(?u)\b\w+\b")

    X = vectorizer.fit_transform(df["cleaned_tags"])
    n_samples = X.shape[0]

    # --- 3. HARDEST LIMIT ON CLUSTERS YET ---
    # Restrict the maximum clusters based on the playlist size
    hard_limit = min(max_clusters + 1, max(3, n_samples // 15 + 2))

    if n_samples < 3 or X.shape[1] == 0:
        df["cluster"] = 0
        best_k = 1
        terms = vectorizer.get_feature_names_out() if X.shape[1] > 0 else ["Mixed"]
        order_centroids = [[0]]
    else:
        highest_score = -1.0
        best_k = 2

        for k in range(2, hard_limit):
            kmeans = KMeans(n_clusters=k, random_state=42, n_init=10)
            labels = kmeans.fit_predict(X)

            if len(set(labels)) > 1:
                score = silhouette_score(X, labels)
                if score > highest_score:
                    highest_score = score
                    best_k = k

        kmeans = KMeans(n_clusters=best_k, random_state=42, n_init=10)
        df["cluster"] = kmeans.fit_predict(X)

        order_centroids = kmeans.cluster_centers_.argsort()[:, ::-1]
        terms = vectorizer.get_feature_names_out()

    # --- 4. FORMAT RESPONSE ---
    result_clusters = {}
    for i in range(best_k):
        top_tags_display = []

        limit = min(4, len(terms))
        top_indices = order_centroids[i, :limit]

        for ind in top_indices:
            # Revert the underscores back to spaces for the UI
            clean_name = terms[ind].replace("_", " ").title()
            top_tags_display.append(clean_name)

        if not top_tags_display:
            top_tags_display = ["Various"]

        cluster_tracks = df[df["cluster"] == i]["spotify_id"].tolist()
        result_clusters[str(i)] = {
            "top_tags": top_tags_display,
            "tracks": cluster_tracks,
        }

    return result_clusters
