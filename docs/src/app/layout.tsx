import type { Metadata } from "next";
import { Inter, IBM_Plex_Mono } from "next/font/google";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  display: "swap",
});

const ibmPlexMono = IBM_Plex_Mono({
  weight: ["400", "500"],
  subsets: ["latin"],
  variable: "--font-mono",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Open Modality — Give AI Senses",
  description:
    "Turn your phone into a sensor gateway for AI agents. Open Modality exposes 16 hardware sensors — camera, microphone, GPS, accelerometer and more — as MCP tools. Open source. No cloud.",
  openGraph: {
    title: "Open Modality — Give AI Senses",
    description:
      "Your phone's 16 sensors as MCP tools for AI agents. Camera, microphone, GPS, accelerometer and more. No cloud. No middleman.",
    type: "website",
    url: "https://anthropics.github.io/open-modality/",
    siteName: "Open Modality",
    locale: "en_US",
  },
  twitter: {
    card: "summary_large_image",
    title: "Open Modality — Give AI Senses",
    description:
      "Your phone's 16 sensors as MCP tools for AI agents. Open source. No cloud.",
  },
  robots: "index, follow",
  authors: [{ name: "Anthropic" }],
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={`${inter.variable} ${ibmPlexMono.variable}`}>
      <head>
        <link rel="icon" href="/favicon.svg" type="image/svg+xml" />
        <meta name="theme-color" content="#050505" />
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify({
              "@context": "https://schema.org",
              "@type": "SoftwareApplication",
              name: "Open Modality",
              description:
                "Turn your phone into a sensor gateway for AI agents. Exposes 16 hardware sensors as MCP tools.",
              url: "https://anthropics.github.io/open-modality/",
              applicationCategory: "DeveloperApplication",
              operatingSystem: "Android, iOS",
              offers: { "@type": "Offer", price: "0", priceCurrency: "USD" },
              author: {
                "@type": "Organization",
                name: "Anthropic",
                url: "https://www.anthropic.com",
              },
              license: "https://opensource.org/licenses/MIT",
              codeRepository:
                "https://github.com/anthropics/open-modality",
              programmingLanguage: ["Kotlin", "Swift"],
            }),
          }}
        />
      </head>
      <body>{children}</body>
    </html>
  );
}
