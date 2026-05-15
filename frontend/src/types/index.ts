export interface Track {
  id: string;
  name: string;
  primaryArtist: string;
  displayArtists: string;
  album?: string;
  durationMs?: number;
  imageUrl?: string;
  externalUrl?: string;
}

export interface Playlist {
  id: string;
  name: string;
  description?: string;
  owner: string;
  imageUrl: string;
  externalUrl: string;
  tracks?: Track[];
}

export interface User {
  name: string;
  id: string;
  image: string;
}
