import psycopg2
import pandas as pd
from dotenv import load_dotenv
import os

load_dotenv()

DB_CONFIG = {
    "dbname": os.getenv("DB_USERNAME"),
    "user": os.getenv("DB_USERNAME"),
    "password": os.getenv("DB_PASSWORD"),
    "host": os.getenv("DB_HOST"),
    "port": os.getenv("DB_PORT"),
}


def fetch_tracks_as_df():
    """Fetches tracks from the database and returns them as a pandas DataFrame."""
    try:
        # Establish the connection
        conn = psycopg2.connect(**DB_CONFIG)

        # We use string_agg to group all tags for a single song into one comma-separated string
        query = """
            SELECT 
                t.spotify_id, 
                t.name, 
                t.artist_name, 
                STRING_AGG(tt.tags, ',') as all_tags
            FROM tracks t
            LEFT JOIN track_tags tt ON t.spotify_id = tt.track_spotify_id
            GROUP BY t.spotify_id, t.name, t.artist_name;
        """

        df = pd.read_sql_query(query, conn)

        return df
    except Exception as e:
        print(f"Error fetching tracks: {e}")
        return pd.DataFrame()  # Return an empty DataFrame on error
    finally:
        if "conn" in locals():
            conn.close()


if __name__ == "__main__":
    df = fetch_tracks_as_df()
    if df is not None and not df.empty:
        print("Successfully fetched data!")
        # print all rows in the DataFrame
        print(df.to_string(index=False))
