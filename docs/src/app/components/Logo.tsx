export default function Logo({
  size = 28,
  className = "",
}: {
  size?: number;
  className?: string;
}) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 104 104"
      fill="none"
      className={className}
    >
      <line x1="52" y1="36" x2="52" y2="18" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="62" y1="42" x2="76" y2="28" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="62" y1="52" x2="80" y2="52" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="62" y1="62" x2="74" y2="74" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="52" y1="68" x2="52" y2="86" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="42" y1="62" x2="30" y2="74" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="42" y1="52" x2="24" y2="52" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="42" y1="42" x2="30" y2="30" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <rect x="42" y="36" width="20" height="32" rx="4" stroke="currentColor" strokeWidth="1.5" />
      <circle cx="52" cy="10" r="6" stroke="currentColor" strokeWidth="1.5" />
      <circle cx="52" cy="10" r="1.8" fill="currentColor" />
      <path d="M71 24 C75 24, 80 29, 80 33" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M72 19 C78 20, 84 26, 85 32" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M88 45 L95 52 L88 59 L81 52 Z" stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round" />
      <circle cx="80" cy="80" r="3.5" stroke="currentColor" strokeWidth="1.5" />
      <circle cx="80" cy="80" r="8" stroke="currentColor" strokeWidth="1.5" />
      <path d="M44 90 Q52 84, 60 90" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M46 96 Q52 91, 58 96" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M27,87 L29,74 L16,76" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
      <line x1="16" y1="44" x2="16" y2="60" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="8" y1="52" x2="24" y2="52" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <polygon points="30,30 16,27 27,16" stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round" fill="none" />
    </svg>
  );
}
