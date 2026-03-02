import { createSignal, onMount, onCleanup } from 'solid-js';

interface SensorValues {
  accelX: string;
  accelY: string;
  accelZ: string;
  gyroAlpha: string;
  gyroBeta: string;
  gyroGamma: string;
  geoLat: string;
  geoLon: string;
}

export function useSensors() {
  const [values, setValues] = createSignal<SensorValues>({
    accelX: '0.00', accelY: '-9.81', accelZ: '0.00',
    gyroAlpha: '0.00', gyroBeta: '0.00', gyroGamma: '0.00',
    geoLat: '--', geoLon: '--',
  });
  const [isLive, setIsLive] = createSignal(false);
  const [buttonText, setButtonText] = createSignal('Enable sensors to feel it live');
  const [changedKey, setChangedKey] = createSignal<string | null>(null);

  let simInterval: ReturnType<typeof setInterval> | null = null;
  let motionHandler: ((e: DeviceMotionEvent) => void) | null = null;
  let geoWatchId: number | null = null;

  function updateVal(key: keyof SensorValues, value: string) {
    setValues((prev) => {
      if (prev[key] !== value) {
        setChangedKey(key);
        setTimeout(() => setChangedKey(null), 150);
        return { ...prev, [key]: value };
      }
      return prev;
    });
  }

  function startSimulation() {
    if (simInterval) return;
    let t = 0;
    simInterval = setInterval(() => {
      t += 0.05;
      updateVal('accelX', (Math.sin(t * 0.7) * 0.15).toFixed(2));
      updateVal('accelY', (-9.81 + Math.sin(t * 0.3) * 0.02).toFixed(2));
      updateVal('accelZ', (Math.cos(t * 0.5) * 0.12).toFixed(2));
      updateVal('gyroAlpha', (Math.sin(t * 0.4) * 0.02).toFixed(3));
      updateVal('gyroBeta', (Math.cos(t * 0.6) * 0.015).toFixed(3));
      updateVal('gyroGamma', (Math.sin(t * 0.8) * 0.01).toFixed(3));
    }, 100);
  }

  function stopSimulation() {
    if (simInterval) {
      clearInterval(simInterval);
      simInterval = null;
    }
  }

  async function enableLive() {
    if (isLive()) return;
    let hasAny = false;

    try {
      if (typeof DeviceMotionEvent !== 'undefined' &&
          typeof (DeviceMotionEvent as any).requestPermission === 'function') {
        const perm = await (DeviceMotionEvent as any).requestPermission();
        if (perm !== 'granted') throw new Error('denied');
      }

      motionHandler = (e: DeviceMotionEvent) => {
        const acc = e.accelerationIncludingGravity;
        if (acc) {
          updateVal('accelX', (acc.x || 0).toFixed(2));
          updateVal('accelY', (acc.y || 0).toFixed(2));
          updateVal('accelZ', (acc.z || 0).toFixed(2));
        }
        const rot = e.rotationRate;
        if (rot) {
          updateVal('gyroAlpha', (rot.alpha || 0).toFixed(3));
          updateVal('gyroBeta', (rot.beta || 0).toFixed(3));
          updateVal('gyroGamma', (rot.gamma || 0).toFixed(3));
        }
      };

      window.addEventListener('devicemotion', motionHandler);
      hasAny = true;
    } catch {
      // Motion not available
    }

    try {
      if ('geolocation' in navigator) {
        geoWatchId = navigator.geolocation.watchPosition(
          (pos) => {
            updateVal('geoLat', pos.coords.latitude.toFixed(2));
            updateVal('geoLon', pos.coords.longitude.toFixed(2));
          },
          () => {},
          { enableHighAccuracy: false, maximumAge: 5000 }
        );
        hasAny = true;
      }
    } catch {
      // Geo not available
    }

    if (hasAny) {
      stopSimulation();
      setIsLive(true);
      setButtonText('Sensors active');
    }
  }

  onMount(() => {
    startSimulation();

    onCleanup(() => {
      stopSimulation();
      if (motionHandler) window.removeEventListener('devicemotion', motionHandler);
      if (geoWatchId !== null) navigator.geolocation.clearWatch(geoWatchId);
    });
  });

  return { values, isLive, buttonText, changedKey, enableLive };
}
