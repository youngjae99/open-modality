import Reveal from './Reveal';

const senses = [
  {
    name: 'Vision',
    description: 'Three lenses that see what AI imagines.',
    tools: ['Back Camera', 'Front Camera', 'LiDAR Scanner'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <path d="M8 36 C16 20, 28 12, 36 12 C44 12, 56 20, 64 36 C56 52, 44 60, 36 60 C28 60, 16 52, 8 36Z" stroke-width="1.2" />
        <circle cx="36" cy="36" r="10" stroke-width="1.2" />
        <circle cx="36" cy="36" r="4" stroke-width="1" />
        <path d="M36 26 C42 28, 46 32, 46 36" stroke-width="0.5" opacity="0.3" />
        <path d="M14 34 C20 24, 30 18, 42 20" stroke-width="0.5" opacity="0.2" />
      </svg>
    ),
  },
  {
    name: 'Audio',
    description: 'A microphone that gives AI the sense of hearing.',
    tools: ['Microphone'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <path d="M42 12 C54 14, 60 24, 58 38 C56 48, 48 56, 40 60 C36 62, 32 60, 32 56 C32 52, 36 48, 38 44 C42 38, 44 32, 42 26 C40 22, 36 22, 34 26" stroke-width="1.2" />
        <path d="M34 26 C32 30, 30 36, 32 40" stroke-width="1" />
        <path d="M38 30 C40 32, 40 36, 38 40" stroke-width="0.6" opacity="0.4" />
      </svg>
    ),
  },
  {
    name: 'Location',
    description: 'GPS coordinates anchoring AI to physical space.',
    tools: ['GPS / Location'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="36" cy="36" r="24" stroke-width="1.2" />
        <circle cx="36" cy="36" r="2" fill="currentColor" opacity="0.3" />
        <path d="M36 14 L38 30 L36 36 L34 30Z" stroke-width="0.8" fill="currentColor" fill-opacity="0.15" />
        <path d="M36 58 L34 42 L36 36 L38 42Z" stroke-width="0.8" />
        <path d="M14 36 L30 34 L36 36 L30 38Z" stroke-width="0.8" />
        <path d="M58 36 L42 38 L36 36 L42 34Z" stroke-width="0.8" fill="currentColor" fill-opacity="0.15" />
        <line x1="36" y1="8" x2="36" y2="12" stroke-width="0.8" />
        <line x1="36" y1="60" x2="36" y2="64" stroke-width="0.8" />
        <line x1="8" y1="36" x2="12" y2="36" stroke-width="0.8" />
        <line x1="60" y1="36" x2="64" y2="36" stroke-width="0.8" />
      </svg>
    ),
  },
  {
    name: 'Motion',
    description: 'Four instruments measuring the physics of movement.',
    tools: ['Accelerometer', 'Gyroscope', 'Magnetometer', 'Pedometer'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 52 C20 48, 24 36, 30 28 C36 20, 40 16, 46 20 C52 24, 54 32, 58 22 C60 18, 62 14, 64 12" stroke-width="1.2" />
        <path d="M14 58 C22 54, 28 44, 34 36 C40 28, 44 24, 50 28" stroke-width="0.6" opacity="0.4" />
        <circle cx="30" cy="28" r="3" stroke-width="1" />
        <path d="M18 28 L24 28" stroke-width="0.5" opacity="0.3" />
        <path d="M16 32 L22 30" stroke-width="0.5" opacity="0.3" />
        <path d="M14 36 L20 34" stroke-width="0.5" opacity="0.3" />
      </svg>
    ),
  },
  {
    name: 'Environment',
    description: 'Atmospheric awareness — pressure, light, proximity.',
    tools: ['Barometer', 'Ambient Light', 'Proximity'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="48" cy="24" r="8" stroke-width="1.2" />
        <line x1="48" y1="10" x2="48" y2="6" stroke-width="0.8" />
        <line x1="48" y1="38" x2="48" y2="42" stroke-width="0.8" />
        <line x1="34" y1="24" x2="30" y2="24" stroke-width="0.8" />
        <line x1="62" y1="24" x2="66" y2="24" stroke-width="0.8" />
        <line x1="38.1" y1="14.1" x2="35.3" y2="11.3" stroke-width="0.7" />
        <line x1="57.9" y1="14.1" x2="60.7" y2="11.3" stroke-width="0.7" />
        <line x1="38.1" y1="33.9" x2="35.3" y2="36.7" stroke-width="0.7" />
        <line x1="57.9" y1="33.9" x2="60.7" y2="36.7" stroke-width="0.7" />
        <path d="M20 18 L20 52 C20 58, 28 58, 28 52 L28 18 C28 14, 20 14, 20 18Z" stroke-width="1" />
        <line x1="24" y1="52" x2="24" y2="30" stroke-width="2" opacity="0.15" />
      </svg>
    ),
  },
  {
    name: 'Connectivity',
    description: 'Bluetooth, WiFi, NFC — the invisible threads.',
    tools: ['Bluetooth LE', 'WiFi Scanner', 'NFC'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round">
        <circle cx="36" cy="52" r="4" stroke-width="1.2" fill="currentColor" fill-opacity="0.08" />
        <path d="M24 44 C28 38, 32 36, 36 36 C40 36, 44 38, 48 44" stroke-width="1.2" />
        <path d="M16 36 C22 26, 28 22, 36 22 C44 22, 50 26, 56 36" stroke-width="1" />
        <path d="M8 28 C16 14, 24 8, 36 8 C48 8, 56 14, 64 28" stroke-width="0.8" />
      </svg>
    ),
  },
  {
    name: 'Device',
    description: 'Battery state — the pulse of the device itself.',
    tools: ['Battery'],
    icon: (
      <svg viewBox="0 0 72 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
        <rect x="18" y="14" width="36" height="50" rx="4" stroke-width="1.2" />
        <rect x="28" y="10" width="16" height="4" rx="2" stroke-width="1" />
        <rect x="24" y="38" width="24" height="20" rx="1" stroke-width="0.6" fill="currentColor" fill-opacity="0.04" />
        <path d="M38 28 L32 42 L38 42 L34 54" stroke-width="1" />
      </svg>
    ),
  },
];

export default function Senses() {
  return (
    <section id="senses" class="senses">
      <div class="container">
        <Reveal><span class="section-label">The Senses</span></Reveal>
        <Reveal><h2 class="section-heading">Seven Dimensions of <em>Perception</em></h2></Reveal>
        <Reveal><p class="section-intro">Sixteen instruments of awareness, exposed as MCP tools.</p></Reveal>

        <div class="senses-grid">
          {senses.map((s) => (
            <Reveal>
              <div class="sense-card">
                <div class="sense-card__illustration">{s.icon}</div>
                <h3 class="sense-card__name">{s.name}</h3>
                <p class="sense-card__description">{s.description}</p>
                <div class="sense-card__tools">
                  {s.tools.map((t) => (
                    <span class="sense-card__tool">{t}</span>
                  ))}
                </div>
              </div>
            </Reveal>
          ))}
        </div>
      </div>
    </section>
  );
}
