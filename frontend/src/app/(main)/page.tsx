"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { Loader2, Music, Sparkles } from "lucide-react";
import PlaylistCard from "./_components/PlaylistCard";

interface Playlist {
  id: string;
  name: string;
}

export default function Dashboard() {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [loading, setLoading] = useState(true);
  const [syncingPlaylistId, setSyncingPlaylistId] = useState<string | null>(
    null,
  );
  const router = useRouter();

  const API_URL = process.env.NEXT_PUBLIC_API_URL;

  useEffect(() => {
    axios
      .get(`${API_URL}/api/spotify/playlists`, {
        withCredentials: true,
      })
      .then((res) => {
        setPlaylists(res.data);
        setLoading(false);
      })
      .catch((err) => {
        if (err.response?.status === 401) {
          router.push("/login");
        } else {
          console.error("Failed to fetch playlists:", err);
          setLoading(false);
        }
      });
  }, [router, API_URL]);

  const handleSync = async (playlistId: string) => {
    setSyncingPlaylistId(playlistId);

    try {
      await axios.post(
        `${API_URL}/api/spotify/playlists/${playlistId}/sync`,
        {},
        {
          withCredentials: true,
        },
      );
      router.push("/proposals");
    } catch (err) {
      console.error("Failed to sync playlist:", err);
      alert("Something went wrong while syncing the playlist.");
      setSyncingPlaylistId(null);
    }
  };

  if (loading) {
    return (
      <main className="min-h-screen p-8">
        <h1 className="mb-8 text-3xl font-bold">Loading your library...</h1>
        <div className="grid grid-cols-1 gap-6 md:grid-cols-3 lg:grid-cols-4">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <div
              key={i}
              className="h-32 animate-pulse rounded-xl border border-zinc-800 bg-zinc-900"
            />
          ))}
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen p-8">
      <header className="mb-10 max-w-2xl">
        <h1 className="mb-2 text-4xl font-bold tracking-tight">Your Library</h1>
        <p className="text-lg text-zinc-400">
          Select a playlist to extract its tracks, analyze their genres using
          Last.fm, and generate mathematically perfect clusters.
        </p>
      </header>

      {playlists.length === 0 ? (
        <div className="flex items-center gap-2 text-zinc-500">
          <Music /> No playlists found in your account.
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
          {playlists.map((playlist) => {
            const isSyncing = syncingPlaylistId === playlist.id;

            return (
              <PlaylistCard
                key={playlist.id}
                playlist={playlist}
                isSyncing={isSyncing}
                isAnySyncing={!!syncingPlaylistId}
                onSync={handleSync}
              />
            );
          })}
        </div>
      )}
    </main>
  );
}
