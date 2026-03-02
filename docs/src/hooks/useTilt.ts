import { createSignal, onMount, onCleanup } from 'solid-js';

export function useTilt(mockupGetter: () => HTMLElement | undefined) {
  const [transform, setTransform] = createSignal('rotateX(0deg) rotateY(0deg)');

  onMount(() => {
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reducedMotion) return;

    const mockup = mockupGetter();
    if (!mockup) return;

    let targetX = 0;
    let targetY = 0;
    let currentX = 0;
    let currentY = 0;
    const maxTilt = 12;

    function lerp(a: number, b: number, t: number) {
      return a + (b - a) * t;
    }

    function updateTilt() {
      currentX = lerp(currentX, targetX, 0.08);
      currentY = lerp(currentY, targetY, 0.08);
      setTransform(`rotateX(${-currentY}deg) rotateY(${currentX}deg)`);
      requestAnimationFrame(updateTilt);
    }

    const section = mockup.closest('section');
    if (section) {
      section.addEventListener('mousemove', (e: MouseEvent) => {
        const rect = mockup.getBoundingClientRect();
        const cx = rect.left + rect.width / 2;
        const cy = rect.top + rect.height / 2;
        const dx = (e.clientX - cx) / (rect.width / 2);
        const dy = (e.clientY - cy) / (rect.height / 2);
        targetX = dx * maxTilt;
        targetY = dy * maxTilt;
      });

      section.addEventListener('mouseleave', () => {
        targetX = 0;
        targetY = 0;
      });
    }

    if ('DeviceOrientationEvent' in window) {
      const orientHandler = (e: DeviceOrientationEvent) => {
        const gamma = e.gamma || 0;
        const beta = e.beta || 0;
        targetX = (gamma / 45) * maxTilt;
        targetY = ((beta - 45) / 45) * maxTilt;
      };

      if (typeof (DeviceOrientationEvent as any).requestPermission === 'function') {
        // iOS permission is handled by the enable-sensors button
        const btn = document.getElementById('enable-sensors');
        if (btn) {
          btn.addEventListener('click', async () => {
            try {
              const perm = await (DeviceOrientationEvent as any).requestPermission();
              if (perm === 'granted') {
                window.addEventListener('deviceorientation', orientHandler, { passive: true });
              }
            } catch {
              // Silently fail
            }
          }, { once: true });
        }
      } else {
        window.addEventListener('deviceorientation', orientHandler, { passive: true });
      }
    }

    updateTilt();
  });

  return transform;
}
