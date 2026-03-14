"use client";

import { useEffect, useState } from "react";
import Logo from "./Logo";

export default function Header() {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <header className={`header${scrolled ? " header--scrolled" : ""}`}>
      <div className="header__inner">
        <a href="#" className="header__logo">
          <Logo size={28} />
          <span>Open Modality</span>
        </a>
        <nav className="header__nav">
          <a href="#senses">Senses</a>
          <a href="#bridge">Protocol</a>
          <a href="#start">Get Started</a>
          <a
            href="https://github.com/anthropics/open-modality"
            target="_blank"
            rel="noopener noreferrer"
            className="btn-sm"
          >
            GitHub
          </a>
        </nav>
      </div>
    </header>
  );
}
