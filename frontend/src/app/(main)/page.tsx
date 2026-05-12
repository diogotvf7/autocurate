"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { Music } from "lucide-react";
import PlaylistCard from "./_components/PlaylistCard";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader } from "@/components/ui/card";

interface Playlist {
  id: string;
  name: string;
  imageUrl?: string;
  description?: string;
  owner?: string;
  externalUrl?: string;
}

export default function Dashboard() {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [loading, setLoading] = useState(true);
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
      .catch((error) => {
        if (error.response?.status === 401) {
          router.push("/login");
        } else {
          console.error("Failed to fetch playlists:", error);
          setLoading(false);
        }
      });
  }, [router, API_URL]);

  const handleClick = async (playlistId: string) => {
    router.push(`/playlists/${playlistId}`);
  };

  if (loading) {
    return (
      <main className="min-h-screen p-8">
        <h1 className="mb-8 text-3xl font-bold">Loading your library...</h1>
        <div className="grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
          {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((i) => (
            <Card key={i} className="w-full max-w-xs">
              <CardHeader>
                <Skeleton className="h-4 w-2/3" />
                <Skeleton className="h-4 w-1/2" />
              </CardHeader>
              <CardContent>
                <Skeleton className="aspect-video w-full" />
              </CardContent>
            </Card>
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
            return (
              <PlaylistCard
                key={playlist.id}
                playlist={playlist}
                handleClick={handleClick}
              />
            );
          })}
        </div>
      )}
    </main>
  );
}
