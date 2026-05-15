"use client";

import axios from "axios";
import { useEffect, useState, use } from "react";
import { useRouter } from "next/navigation";
import { Playlist } from "@/types";

interface PlaylistPageProps {
  params: Promise<{ id: string }>;
}

export default function PlaylistPage({ params }: PlaylistPageProps) {
  const { id } = use(params);
  const router = useRouter();

  const API_URL = process.env.NEXT_PUBLIC_API_URL;

  const [playlist, setPlaylist] = useState<Playlist | null>(null);

  useEffect(() => {
    axios
      .get(`${API_URL}/api/spotify/playlists/${id}`, {
        withCredentials: true,
      })
      .then((response) => {
        setPlaylist(response.data);
      })
      .catch((error) => {
        if (error.response?.status === 401) {
          router.push("/login");
        } else {
          console.error("Failed to fetch playlists:", error);
        }
      });
  }, [id, API_URL, router]);

  return (
    <main className="min-h-screen p-8">
      {playlist ? (
        <header className="mb-8 flex gap-6">
          {playlist.imageUrl && (
            <img
              src={playlist.imageUrl}
              alt={playlist.name}
              className="h-48 w-48 rounded-lg object-cover shadow-lg"
            />
          )}
          <div className="flex flex-col justify-end">
            <span className="text-sm font-semibold tracking-wider uppercase">
              Playlist
            </span>
            <h1 className="mb-2 text-5xl font-bold">{playlist.name}</h1>
            <p className="text-lg">{playlist.description}</p>
            {playlist.tracks && (
              <p className="mt-2 text-sm">
                Created by <span className="">{playlist.owner}</span> •{" "}
                {playlist.tracks.length} songs
              </p>
            )}
          </div>
        </header>
      ) : (
        <div className="h-48 w-full animate-pulse rounded-lg bg-zinc-900" />
      )}

      {/* The Shadcn Table will go here! */}
    </main>
  );
}
