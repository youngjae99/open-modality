import { createSignal, onMount } from 'solid-js';

export default function Hero() {
  const [hovered, setHovered] = createSignal<string | null>(null);
  const [titleVisible, setTitleVisible] = createSignal(false);
  const [subtitleVisible, setSubtitleVisible] = createSignal(false);
  const [taglineVisible, setTaglineVisible] = createSignal(false);
  const [scrollVisible, setScrollVisible] = createSignal(false);
  const [teardownVisible, setTeardownVisible] = createSignal(false);

  onMount(() => {
    const rm = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (rm) {
      setTeardownVisible(true);
      setTitleVisible(true);
      setSubtitleVisible(true);
      setTaglineVisible(true);
      setScrollVisible(true);
      return;
    }
    setTimeout(() => setTeardownVisible(true), 300);
    setTimeout(() => setTitleVisible(true), 700);
    setTimeout(() => setSubtitleVisible(true), 900);
    setTimeout(() => setTaglineVisible(true), 1100);
    setTimeout(() => setScrollVisible(true), 1500);
  });

  const mc = (id: string) =>
    `teardown-module${hovered() === id ? ' teardown-module--active' : ''}`;

  const zc = (id: string) =>
    `teardown-phone__zone${hovered() === id ? ' teardown-phone__zone--active' : ''}`;

  return (
    <section id="hero" class="hero">
      <div class={`hero__teardown reveal${teardownVisible() ? ' visible' : ''}`}>
        {/* Left column: Camera, Microphone, IMU */}
        <div class="teardown__col teardown__col--left">
          {/* Camera → Eye */}
          <div
            class={mc('camera')}
            onMouseEnter={() => setHovered('camera')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="10" y="14" width="36" height="28" rx="3" stroke-width="1.2" />
                <circle cx="28" cy="28" r="10" stroke-width="1.2" />
                <circle cx="28" cy="28" r="6" stroke-width="0.8" />
                <circle cx="28" cy="28" r="2.5" stroke-width="0.5" />
                <rect x="14" y="16" width="8" height="4" rx="1" stroke-width="0.6" opacity="0.5" />
                <line x1="16" y1="42" x2="16" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="22" y1="42" x2="22" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="28" y1="42" x2="28" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="34" y1="42" x2="34" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="40" y1="42" x2="40" y2="46" stroke-width="0.6" opacity="0.4" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 28 C10 16, 18 12, 28 12 C38 12, 46 16, 52 28 C46 40, 38 44, 28 44 C18 44, 10 40, 4 28Z" stroke-width="1.2" />
                <circle cx="28" cy="28" r="9" stroke-width="1" />
                <circle cx="28" cy="28" r="4" stroke-width="0.8" />
                <circle cx="25.5" cy="25.5" r="1.5" fill="currentColor" opacity="0.15" />
                <line x1="28" y1="19" x2="28" y2="23" stroke-width="0.3" opacity="0.2" />
                <line x1="28" y1="33" x2="28" y2="37" stroke-width="0.3" opacity="0.2" />
                <line x1="19" y1="28" x2="23" y2="28" stroke-width="0.3" opacity="0.2" />
                <line x1="33" y1="28" x2="37" y2="28" stroke-width="0.3" opacity="0.2" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">Camera</span>
              <span class="teardown-module__human-name">Vision</span>
              <span class="teardown-module__desc">12MP CMOS</span>
            </div>
          </div>

          {/* Microphone → Ear */}
          <div
            class={mc('mic')}
            onMouseEnter={() => setHovered('mic')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="14" y="14" width="28" height="28" rx="2" stroke-width="1.2" />
                <circle cx="28" cy="28" r="6" stroke-width="1" />
                <circle cx="28" cy="28" r="2" stroke-width="0.6" />
                <circle cx="20" cy="20" r="1" stroke-width="0.4" opacity="0.3" />
                <circle cx="36" cy="20" r="1" stroke-width="0.4" opacity="0.3" />
                <circle cx="20" cy="36" r="1" stroke-width="0.4" opacity="0.3" />
                <circle cx="36" cy="36" r="1" stroke-width="0.4" opacity="0.3" />
                <line x1="18" y1="42" x2="18" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="28" y1="42" x2="28" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="38" y1="42" x2="38" y2="46" stroke-width="0.6" opacity="0.4" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M32 6 C42 8, 48 16, 48 26 C48 36, 44 42, 38 46 C34 48, 30 50, 28 50 C24 50, 20 46, 22 42" stroke-width="1.2" />
                <path d="M34 12 C38 16, 40 22, 38 28 C36 34, 32 38, 28 40" stroke-width="0.8" />
                <path d="M24 30 C22 28, 22 24, 26 22" stroke-width="0.8" />
                <path d="M24 30 C20 30, 16 28, 14 24" stroke-width="0.6" opacity="0.5" />
                <path d="M22 42 C20 44, 18 48, 22 50 C26 52, 28 50, 28 50" stroke-width="0.8" />
                <path d="M30 16 C34 18, 36 24, 34 30" stroke-width="0.4" opacity="0.25" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">Microphone</span>
              <span class="teardown-module__human-name">Hearing</span>
              <span class="teardown-module__desc">MEMS Array</span>
            </div>
          </div>

          {/* IMU → Vestibular */}
          <div
            class={mc('accel')}
            onMouseEnter={() => setHovered('accel')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="12" y="12" width="32" height="32" rx="2" stroke-width="1.2" />
                <rect x="20" y="20" width="16" height="16" rx="1" stroke-width="0.6" opacity="0.4" />
                <line x1="28" y1="28" x2="42" y2="28" stroke-width="0.8" />
                <path d="M40 26 L42 28 L40 30" stroke-width="0.8" />
                <line x1="28" y1="28" x2="28" y2="14" stroke-width="0.8" />
                <path d="M26 16 L28 14 L30 16" stroke-width="0.8" />
                <line x1="28" y1="28" x2="18" y2="38" stroke-width="0.8" />
                <path d="M20.5 36 L18 38 L20 39.5" stroke-width="0.8" />
                <circle cx="16" cy="48" r="1" stroke-width="0.5" opacity="0.3" />
                <circle cx="24" cy="48" r="1" stroke-width="0.5" opacity="0.3" />
                <circle cx="32" cy="48" r="1" stroke-width="0.5" opacity="0.3" />
                <circle cx="40" cy="48" r="1" stroke-width="0.5" opacity="0.3" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M28 34 C28 18, 14 10, 14 22" stroke-width="1.2" />
                <path d="M28 34 C28 18, 42 10, 42 22" stroke-width="1.2" />
                <path d="M28 34 C18 34, 10 28, 14 22" stroke-width="1" />
                <path d="M28 34 C38 34, 46 28, 42 22" stroke-width="1" />
                <circle cx="28" cy="34" r="5" stroke-width="1" />
                <path d="M28 39 C28 42, 24 46, 20 46 C16 46, 14 42, 16 40 C18 38, 21 40, 21 42" stroke-width="0.8" />
                <line x1="28" y1="39" x2="28" y2="50" stroke-width="0.6" opacity="0.4" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">IMU</span>
              <span class="teardown-module__human-name">Balance</span>
              <span class="teardown-module__desc">6-axis Motion</span>
            </div>
          </div>
        </div>

        {/* Center: Phone wireframe */}
        <div class="teardown__phone">
          <svg viewBox="0 0 200 400" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
            {/* Body */}
            <rect x="10" y="10" width="180" height="380" rx="32" stroke-width="1.5" />
            <rect x="16" y="16" width="168" height="368" rx="28" stroke-width="0.5" opacity="0.15" />
            {/* Dynamic Island */}
            <rect x="72" y="26" width="56" height="16" rx="8" stroke-width="1" />

            {/* Sensor zones — highlight on hover */}
            <g class={zc('camera')}>
              <rect x="28" y="52" width="48" height="40" rx="6" />
              <circle cx="52" cy="72" r="10" stroke-width="0.6" />
            </g>
            <g class={zc('mic')}>
              <rect x="124" y="52" width="48" height="30" rx="4" />
            </g>
            <g class={zc('accel')}>
              <rect x="28" y="155" width="44" height="44" rx="3" />
            </g>
            <g class={zc('gps')}>
              <rect x="128" y="155" width="44" height="44" rx="3" />
            </g>
            <g class={zc('baro')}>
              <rect x="78" y="120" width="44" height="34" rx="3" />
            </g>
            <g class={zc('radio')}>
              <rect x="55" y="346" width="90" height="28" rx="4" />
            </g>

            {/* Main board */}
            <rect x="55" y="215" width="90" height="55" rx="4" stroke-width="0.4" opacity="0.08" />
            {/* Battery */}
            <rect x="38" y="280" width="124" height="55" rx="8" stroke-width="0.4" opacity="0.06" />

            {/* Circuit traces */}
            <path d="M52 92 L52 155" stroke-width="0.3" opacity="0.06" />
            <path d="M148 82 L148 155" stroke-width="0.3" opacity="0.06" />
            <path d="M100 154 L100 120" stroke-width="0.3" opacity="0.06" />
            <path d="M55 240 L38 240 L38 280" stroke-width="0.3" opacity="0.06" />
            <path d="M145 240 L162 240 L162 280" stroke-width="0.3" opacity="0.06" />
            <path d="M100 270 L100 280" stroke-width="0.3" opacity="0.06" />
            <path d="M100 335 L100 346" stroke-width="0.3" opacity="0.06" />
          </svg>
        </div>

        {/* Right column: GPS, Barometer, Radio */}
        <div class="teardown__col teardown__col--right">
          {/* GPS → Direction */}
          <div
            class={mc('gps')}
            onMouseEnter={() => setHovered('gps')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="12" y="18" width="32" height="24" rx="2" stroke-width="1.2" />
                <rect x="18" y="8" width="20" height="10" rx="1" stroke-width="0.8" />
                <path d="M28 8 C28 4, 32 2, 36 4" stroke-width="0.5" opacity="0.4" />
                <path d="M28 8 C28 2, 34 0, 40 2" stroke-width="0.4" opacity="0.25" />
                <circle cx="28" cy="30" r="4" stroke-width="0.5" opacity="0.3" />
                <line x1="18" y1="42" x2="18" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="26" y1="42" x2="26" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="34" y1="42" x2="34" y2="46" stroke-width="0.6" opacity="0.4" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="28" cy="28" r="18" stroke-width="1.2" />
                <circle cx="28" cy="28" r="2" stroke-width="0.8" />
                <line x1="28" y1="10" x2="28" y2="18" stroke-width="0.8" />
                <line x1="28" y1="38" x2="28" y2="46" stroke-width="0.5" opacity="0.4" />
                <line x1="10" y1="28" x2="18" y2="28" stroke-width="0.5" opacity="0.4" />
                <line x1="38" y1="28" x2="46" y2="28" stroke-width="0.5" opacity="0.4" />
                <path d="M28 18 L24 26 L28 30 L32 26 Z" stroke-width="0.8" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">GNSS</span>
              <span class="teardown-module__human-name">Direction</span>
              <span class="teardown-module__desc">Multi-band</span>
            </div>
          </div>

          {/* Barometer → Touch */}
          <div
            class={mc('baro')}
            onMouseEnter={() => setHovered('baro')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="14" y="14" width="28" height="28" rx="2" stroke-width="1.2" />
                <circle cx="28" cy="26" r="4" stroke-width="0.8" />
                <circle cx="28" cy="26" r="7" stroke-width="0.4" opacity="0.3" />
                <path d="M24 26 C26 24, 30 24, 32 26" stroke-width="0.5" opacity="0.4" />
                <path d="M24 26 C26 28, 30 28, 32 26" stroke-width="0.5" opacity="0.4" />
                <rect x="20" y="34" width="16" height="4" rx="1" stroke-width="0.4" opacity="0.25" />
                <line x1="18" y1="42" x2="18" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="28" y1="42" x2="28" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="38" y1="42" x2="38" y2="46" stroke-width="0.6" opacity="0.4" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 50 C18 50, 16 40, 16 32 C16 20, 20 10, 28 10 C36 10, 40 20, 40 32 C40 40, 38 50, 38 50" stroke-width="1.2" />
                <path d="M22 38 C24 36, 32 36, 34 38" stroke-width="0.5" opacity="0.3" />
                <path d="M21 34 C24 31, 32 31, 35 34" stroke-width="0.5" opacity="0.3" />
                <path d="M22 30 C24 28, 32 28, 34 30" stroke-width="0.5" opacity="0.3" />
                <path d="M23 26 C25 24, 31 24, 33 26" stroke-width="0.5" opacity="0.3" />
                <circle cx="24" cy="20" r="1.5" stroke-width="0.5" opacity="0.4" />
                <circle cx="32" cy="20" r="1.5" stroke-width="0.5" opacity="0.4" />
                <circle cx="28" cy="16" r="1.5" stroke-width="0.5" opacity="0.4" />
                <line x1="24" y1="21.5" x2="24" y2="26" stroke-width="0.3" opacity="0.2" />
                <line x1="32" y1="21.5" x2="32" y2="26" stroke-width="0.3" opacity="0.2" />
                <line x1="28" y1="17.5" x2="28" y2="22" stroke-width="0.3" opacity="0.2" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">Barometer</span>
              <span class="teardown-module__human-name">Touch</span>
              <span class="teardown-module__desc">Pressure MEMS</span>
            </div>
          </div>

          {/* Radio → Synapse */}
          <div
            class={mc('radio')}
            onMouseEnter={() => setHovered('radio')}
            onMouseLeave={() => setHovered(null)}
          >
            <div class="teardown-module__icons">
              <svg class="teardown-module__device" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <rect x="10" y="18" width="36" height="24" rx="3" stroke-width="1.2" />
                <path d="M28 18 L28 10 L36 10" stroke-width="0.8" />
                <path d="M38 10 C42 6, 46 8, 46 10" stroke-width="0.5" opacity="0.4" />
                <path d="M40 10 C46 4, 50 8, 50 12" stroke-width="0.4" opacity="0.25" />
                <rect x="16" y="24" width="10" height="6" rx="1" stroke-width="0.5" opacity="0.35" />
                <rect x="30" y="24" width="10" height="6" rx="1" stroke-width="0.5" opacity="0.35" />
                <line x1="16" y1="42" x2="16" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="24" y1="42" x2="24" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="32" y1="42" x2="32" y2="46" stroke-width="0.6" opacity="0.4" />
                <line x1="40" y1="42" x2="40" y2="46" stroke-width="0.6" opacity="0.4" />
              </svg>
              <svg class="teardown-module__human" viewBox="0 0 56 56" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="28" cy="22" r="8" stroke-width="1.2" />
                <circle cx="28" cy="22" r="3" stroke-width="0.6" opacity="0.4" />
                <path d="M20 18 C14 14, 8 16, 6 12" stroke-width="0.8" />
                <path d="M22 16 C18 10, 12 8, 10 4" stroke-width="0.7" />
                <path d="M6 12 C4 10, 6 8, 4 6" stroke-width="0.4" opacity="0.4" />
                <path d="M34 16 C38 10, 44 8, 46 4" stroke-width="0.7" />
                <path d="M36 18 C42 14, 48 16, 50 12" stroke-width="0.8" />
                <path d="M50 12 C52 10, 50 8, 52 6" stroke-width="0.4" opacity="0.4" />
                <path d="M28 30 L28 46" stroke-width="1" />
                <circle cx="24" cy="48" r="2" stroke-width="0.6" />
                <circle cx="28" cy="50" r="2" stroke-width="0.6" />
                <circle cx="32" cy="48" r="2" stroke-width="0.6" />
              </svg>
            </div>
            <div class="teardown-module__text">
              <span class="teardown-module__device-name">Radio</span>
              <span class="teardown-module__human-name">Synapse</span>
              <span class="teardown-module__desc">BLE + WiFi</span>
            </div>
          </div>
        </div>
      </div>

      <div class="hero__content">
        <h1 class={`hero__title reveal${titleVisible() ? ' visible' : ''}`}>
          Give AI <em>Senses</em>
        </h1>
        <p class={`hero__subtitle reveal${subtitleVisible() ? ' visible' : ''}`} lang="ko">
          AI에게 감각을 주다
        </p>
        <p class={`hero__tagline reveal${taglineVisible() ? ' visible' : ''}`}>
          Your phone's sensors as MCP tools. No cloud. No middleman.
        </p>
        <div class={`hero__scroll-hint reveal${scrollVisible() ? ' visible' : ''}`}>
          <span class="scroll-chevron" />
        </div>
      </div>
    </section>
  );
}
