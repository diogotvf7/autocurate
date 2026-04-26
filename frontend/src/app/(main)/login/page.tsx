"use client";

import { Eye, LayoutDashboard, Settings, Sparkles } from "lucide-react";

export default function LoginPage() {
  const API_URL = process.env.NEXT_PUBLIC_API_URL;

  return (
    <div className="flex">
      <div className="flex flex-col items-start gap-4 p-8">
        <h1 className="text-3xl font-bold">Organize your world.</h1>
        <p className="text-muted-foreground text-lg">
          Curate your Spotify playlists with ease.
        </p>
        <div className="mt-4 flex flex-col gap-2">
          <span className="inline-flex gap-2">
            <Sparkles /> Cluster playlists effortlessly
          </span>
          <span className="inline-flex gap-2">
            <Eye />
            Visualize hidden gems
          </span>
          <span className="inline-flex gap-2">
            <Settings />
            Automate library logic
          </span>
          <span className="inline-flex gap-2">
            <LayoutDashboard />
            Visualize clarity
          </span>
        </div>
      </div>
    </div>
  );
}
