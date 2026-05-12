import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { FolderOpen, Link, Loader2 } from "lucide-react";

interface PlaylistCardProps {
  playlist: {
    id: string;
    name: string;
    imageUrl?: string;
    description?: string;
    owner?: string;
    externalUrl?: string;
  };
  handleClick: (id: string) => void;
}

export default function PlaylistCard({
  playlist,
  handleClick: handleClick,
}: PlaylistCardProps) {
  return (
    <Card
      key={playlist.id}
      className="flex h-full flex-col overflow-hidden border-zinc-800 transition-colors hover:border-green-500/50"
    >
      <img
        src={playlist.imageUrl || "https://avatar.vercel.sh/shadcn1"}
        alt={playlist.name}
        className="aspect-square object-cover object-top brightness-75 transition-transform duration-500 hover:scale-105 dark:brightness-50"
      />
      <CardHeader className="flex-1">
        <CardTitle className="line-clamp-2 text-xl">{playlist.name}</CardTitle>
        <CardDescription className="font-mono text-xs text-zinc-500">
          {playlist.description}
        </CardDescription>
      </CardHeader>

      <CardFooter className="mt-auto flex items-center">
        <Button
          onClick={() => handleClick(playlist.id)}
          variant="outline"
          className="grow font-semibold text-black transition-all hover:bg-zinc-200"
        >
          <FolderOpen className="mr-2 h-4 w-4 text-green-600" />
          Open Playlist
        </Button>
        <Button size="icon" variant="outline" asChild className="ml-2">
          <a
            href={playlist.externalUrl}
            target="_blank"
            rel="noopener noreferrer"
          >
            <Link className="h-4 w-4" />
          </a>
        </Button>
      </CardFooter>
    </Card>
  );
}
