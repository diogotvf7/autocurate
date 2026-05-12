import axios from "axios";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface PlaylistPageProps {
  params: Promise<{ id: string }>;
}

interface Playlist {
  id: string;
  name: string;
  imageUrl?: string;
  description?: string;
  owner?: string;
  externalUrl?: string;
}

export default async function PlaylistPage({ params }: PlaylistPageProps) {
  const { id } = await params;
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
  }, [id, API_URL]);

  return (
    <div>
      <header>
        {playlist && (
          <div>
            <h1 className="text-3xl font-bold">{playlist.name}</h1>
            <p className="text-lg text-zinc-400">{playlist.description}</p>
          </div>
        )}
        {/* TODO
        ~ Criar endpoint para obter detalhes da playlist (nome, descrição, imagem, etc.)
        ~ Exibir detalhes da playlist no topo da página
        ~ Listar músicas da playlist abaixo dos detalhes
        */}
      </header>
    </div>
  );
}
