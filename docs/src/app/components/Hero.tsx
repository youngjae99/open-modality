"use client";

import { useEffect, useState } from "react";
import Terminal from "./Terminal";

export default function Hero() {
  const [show, setShow] = useState(false);

  useEffect(() => {
    requestAnimationFrame(() => setShow(true));
  }, []);

  return (
    <section className="hero">
      <div
        className="fade-in"
        style={{
          opacity: show ? 1 : 0,
          transform: show ? "translateY(0)" : "translateY(24px)",
          transition: "opacity 0.8s ease, transform 0.8s ease",
        }}
      >
        <div className="hero__badge">Open Source MCP Server</div>
      </div>

      <h1
        className="hero__title"
        style={{
          opacity: show ? 1 : 0,
          transform: show ? "translateY(0)" : "translateY(32px)",
          transition: "opacity 0.8s ease 0.1s, transform 0.8s ease 0.1s",
        }}
      >
        Give AI <em>Senses</em>
      </h1>

      <p
        className="hero__subtitle-ko"
        lang="ko"
        style={{
          opacity: show ? 1 : 0,
          transition: "opacity 0.8s ease 0.2s",
        }}
      >
        AI&#xC5D0;&#xAC8C; &#xAC10;&#xAC01;&#xC744; &#xC8FC;&#xB2E4;
      </p>

      <p
        className="hero__desc"
        style={{
          opacity: show ? 1 : 0,
          transform: show ? "translateY(0)" : "translateY(16px)",
          transition: "opacity 0.8s ease 0.3s, transform 0.8s ease 0.3s",
        }}
      >
        Your phone&apos;s sensors as MCP tools.
        <br />
        No cloud. No middleman.
      </p>

      <div
        className="hero__actions"
        style={{
          opacity: show ? 1 : 0,
          transform: show ? "translateY(0)" : "translateY(16px)",
          transition: "opacity 0.8s ease 0.4s, transform 0.8s ease 0.4s",
        }}
      >
        <a
          href="https://github.com/anthropics/open-modality"
          target="_blank"
          rel="noopener noreferrer"
          className="btn btn--primary"
        >
          <svg viewBox="0 0 24 24" fill="currentColor" width="18" height="18">
            <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
          </svg>
          View on GitHub
        </a>
        <a href="#start" className="btn btn--ghost">
          Get Started
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
            <path d="M7 17L17 7M17 7H7M17 7v10" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </a>
      </div>

      <div
        style={{
          opacity: show ? 1 : 0,
          transform: show ? "translateY(0)" : "translateY(24px)",
          transition: "opacity 0.8s ease 0.5s, transform 0.8s ease 0.5s",
          width: "100%",
        }}
      >
        <Terminal />
      </div>
    </section>
  );
}
