"use client";

import gsap from "gsap";
import ScrambleTextPlugin from "gsap/ScrambleTextPlugin";
import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import axios from "axios";
import { Separator } from "./ui/separator";
import { Button } from "./ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";
import { Skeleton } from "./ui/skeleton";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

interface User {
  name: string;
  id: string;
  image: string;
}

gsap.registerPlugin?.(ScrambleTextPlugin);

const Navbar = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const router = useRouter();
  const pathname = usePathname();

  const publicRoutes = ["/login", "/about"];

  useEffect(() => {
    setIsLoading(true);

    axios
      .get(`${API_URL}/api/auth/me/profile`, { withCredentials: true })
      .then((res) => {
        setUser(res.data);
        setIsLoading(false);
      })
      .catch(() => {
        setUser(null);
        setIsLoading(false);

        if (!publicRoutes.includes(pathname)) {
          router.push("/login");
        }
      });
  }, [pathname, router]);

  useEffect(() => {
    const elems = Array.from(
      document.getElementsByClassName("scramble"),
    ) as HTMLElement[];

    if (elems.length === 0) return;

    elems.forEach((el) => {
      gsap.to(el, {
        duration: 1.8,
        scrambleText: { text: "Autocurate" },
        ease: "power2.out",
      });
    });

    return () => {
      gsap.killTweensOf(elems as any);
    };
  }, []);

  const handleLogin = () => {
    if (!API_URL) {
      console.error(
        "NEXT_PUBLIC_API_URL is not defined. Cannot redirect to login.",
      );
      return;
    }

    window.location.href = `${API_URL}/api/auth/login`;
  };

  const getInitials = (name: string) => {
    const parts = name.split(" ");
    return ((parts[0]?.[0] || "") + (parts[1]?.[0] || "")).toUpperCase();
  };

  return (
    <header className="sticky top-0 z-50 flex min-w-full items-center justify-between border-b bg-inherit p-4">
      <h1 className="scramble truncate font-mono text-4xl select-none">
        Autocurate
      </h1>
      <div className="flex size-max items-center justify-between gap-6">
        <a href="/about" className="hover:underline">
          About Us
        </a>
        <Separator orientation="vertical" className="m-2" />{" "}
        {isLoading ? (
          <Skeleton className="bg-muted h-8 w-32 rounded-full" />
        ) : user ? (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="outline"
                className="flex h-min items-center gap-2 p-2"
              >
                <img
                  src={user.image}
                  alt={getInitials(user.name) || ""}
                  className="h-8 w-8 rounded-full object-cover"
                />
                <span className="text-muted-foreground text-sm">
                  {user.name}
                </span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuGroup>
                <DropdownMenuItem>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      axios
                        .post(
                          `${API_URL}/api/auth/logout`,
                          {},
                          { withCredentials: true },
                        )
                        .then(() => {
                          setUser(null);
                          router.push("/login");
                        })
                        .catch((err) => {
                          console.error("Logout failed:", err);
                        });
                    }}
                    className="w-full"
                  >
                    Logout
                  </Button>
                </DropdownMenuItem>
              </DropdownMenuGroup>
            </DropdownMenuContent>
          </DropdownMenu>
        ) : (
          <Button
            size="lg"
            onClick={handleLogin}
            className="bg-green-600 hover:bg-green-700"
          >
            Connect Spotify
          </Button>
        )}
      </div>
    </header>
  );
};

export default Navbar;
