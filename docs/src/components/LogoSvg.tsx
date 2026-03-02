interface LogoSvgProps {
  width?: number;
  height?: number;
  class?: string;
}

export default function LogoSvg(props: LogoSvgProps) {
  return (
    <svg
      width={props.width ?? 160}
      height={props.height ?? 160}
      viewBox="0 0 104 104"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      class={props.class}
    >
      {/* Spokes */}
      <line class="spoke" x1="52" y1="36" x2="52" y2="18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="62" y1="42" x2="76" y2="28" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="62" y1="52" x2="80" y2="52" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="62" y1="62" x2="74" y2="74" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="52" y1="68" x2="52" y2="86" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="42" y1="62" x2="30" y2="74" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="42" y1="52" x2="24" y2="52" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      <line class="spoke" x1="42" y1="42" x2="30" y2="30" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />

      {/* Phone outline */}
      <rect x="42" y="36" width="20" height="32" rx="4" stroke="currentColor" stroke-width="1.5" fill="none" class="phone-outline" />

      {/* Vision */}
      <circle cx="52" cy="10" r="6" stroke="currentColor" stroke-width="1.5" fill="none" class="sensor-icon" />
      <circle cx="52" cy="10" r="1.8" fill="currentColor" class="sensor-icon" />

      {/* Audio */}
      <path d="M71 24 C75 24, 80 29, 80 33" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" class="sensor-icon" />
      <path d="M72 19 C78 20, 84 26, 85 32" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" class="sensor-icon" />

      {/* Location */}
      <path d="M88 45 L95 52 L88 59 L81 52 Z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round" class="sensor-icon" />

      {/* Touch/Proximity */}
      <circle cx="80" cy="80" r="3.5" stroke="currentColor" stroke-width="1.5" fill="none" class="sensor-icon" />
      <circle cx="80" cy="80" r="8" stroke="currentColor" stroke-width="1.5" fill="none" class="sensor-icon" />

      {/* Environment */}
      <path d="M44 90 Q52 84, 60 90" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" class="sensor-icon" />
      <path d="M46 96 Q52 91, 58 96" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" class="sensor-icon" />

      {/* Motion */}
      <path d="M27,87 L29,74 L16,76" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" class="sensor-icon" />

      {/* Light */}
      <line x1="16" y1="44" x2="16" y2="60" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" class="sensor-icon" />
      <line x1="8" y1="52" x2="24" y2="52" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" class="sensor-icon" />

      {/* Connectivity */}
      <polygon points="30,30 16,27 27,16" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round" class="sensor-icon" />
    </svg>
  );
}
