import FadeIn from "./FadeIn";

const senses = [
  {
    name: "Vision",
    desc: "Three lenses that see what AI imagines.",
    tools: ["Back Camera", "Front Camera", "LiDAR Scanner"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 20C8 12 14 8 20 8s12 4 16 12c-4 8-10 12-16 12S8 28 4 20z" strokeWidth="1.2" />
        <circle cx="20" cy="20" r="5" strokeWidth="1.2" />
        <circle cx="20" cy="20" r="2" strokeWidth="0.8" />
      </svg>
    ),
  },
  {
    name: "Audio",
    desc: "A microphone that gives AI the sense of hearing.",
    tools: ["Microphone"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <path d="M24 6c6 1 10 6 9 14s-6 14-10 16c-2 1-4 0-4-2s2-4 3-7c2-4 3-7 2-10s-3-4-4-2" strokeWidth="1.2" />
        <path d="M18 14c-1 2-2 6-1 8" strokeWidth="1" />
      </svg>
    ),
  },
  {
    name: "Location",
    desc: "GPS coordinates anchoring AI to physical space.",
    tools: ["GPS / Location"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="20" cy="20" r="13" strokeWidth="1.2" />
        <path d="M20 7l1 9-1 4-1-4zM20 33l-1-9 1-4 1 4z" strokeWidth="0.8" />
        <path d="M7 20l9-1 4 1-4 1zM33 20l-9 1-4-1 4-1z" strokeWidth="0.8" />
      </svg>
    ),
  },
  {
    name: "Motion",
    desc: "Four instruments measuring the physics of movement.",
    tools: ["Accelerometer", "Gyroscope", "Magnetometer", "Pedometer"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <path d="M6 30c4-2 7-9 11-14s6-5 9-3 4 6 7 1l3-5" strokeWidth="1.2" />
        <circle cx="17" cy="16" r="2" strokeWidth="1" />
      </svg>
    ),
  },
  {
    name: "Environment",
    desc: "Atmospheric awareness — pressure, light, proximity.",
    tools: ["Barometer", "Ambient Light", "Proximity"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="27" cy="14" r="5" strokeWidth="1.2" />
        <line x1="27" y1="5" x2="27" y2="3" strokeWidth="0.8" />
        <line x1="27" y1="23" x2="27" y2="25" strokeWidth="0.8" />
        <line x1="18" y1="14" x2="16" y2="14" strokeWidth="0.8" />
        <line x1="36" y1="14" x2="38" y2="14" strokeWidth="0.8" />
        <path d="M11 10v18c0 3 4 3 4 0V10c0-2-4-2-4 0z" strokeWidth="1" />
      </svg>
    ),
  },
  {
    name: "Connectivity",
    desc: "Bluetooth, WiFi, NFC — the invisible threads.",
    tools: ["Bluetooth LE", "WiFi Scanner", "NFC"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round">
        <circle cx="20" cy="30" r="2" strokeWidth="1.2" />
        <path d="M12 24c4-4 8-5 8-5s4 1 8 5" strokeWidth="1.2" />
        <path d="M6 18c6-6 10-7 14-7s8 1 14 7" strokeWidth="1" />
      </svg>
    ),
  },
  {
    name: "Device",
    desc: "Battery state — the pulse of the device itself.",
    tools: ["Battery"],
    icon: (
      <svg viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round">
        <rect x="10" y="8" width="20" height="28" rx="2" strokeWidth="1.2" />
        <rect x="15" y="5" width="10" height="3" rx="1" strokeWidth="1" />
        <path d="M21 16l-3 8h3l-2 7" strokeWidth="1" />
      </svg>
    ),
  },
];

export default function Senses() {
  return (
    <section id="senses" className="section">
      <div className="container">
        <FadeIn>
          <span className="section-label">The Senses</span>
        </FadeIn>
        <FadeIn delay={80}>
          <h2 className="section-heading">
            Seven Dimensions of <em>Perception</em>
          </h2>
        </FadeIn>
        <FadeIn delay={160}>
          <p className="section-intro">
            Sixteen instruments of awareness, exposed as MCP tools.
          </p>
        </FadeIn>

        <FadeIn delay={240}>
          <div className="senses-grid">
            {senses.map((s, i) => (
              <div key={s.name} className="sense-card">
                <div className="sense-card__num">
                  {String(i + 1).padStart(2, "0")}
                </div>
                <div className="sense-card__icon">{s.icon}</div>
                <h3 className="sense-card__name">{s.name}</h3>
                <p className="sense-card__desc">{s.desc}</p>
                <div className="sense-card__tools">
                  {s.tools.map((t) => (
                    <span key={t} className="sense-card__tool">
                      {t}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </FadeIn>
      </div>
    </section>
  );
}
