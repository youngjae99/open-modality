import { createSignal, onMount, onCleanup } from 'solid-js';

export default function Terminal() {
  const [line1Visible, setLine1Visible] = createSignal(false);
  const [line2Visible, setLine2Visible] = createSignal(false);
  const [tickX, setTickX] = createSignal('0.02');
  const [tickY, setTickY] = createSignal('-9.81');
  const [tickZ, setTickZ] = createSignal('0.15');
  const [changed, setChanged] = createSignal(false);

  let termEl: HTMLDivElement | undefined;
  let tickId: ReturnType<typeof setInterval> | null = null;

  onMount(() => {
    if (!termEl) return;

    const obs = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setTimeout(() => setLine1Visible(true), 0);
          setTimeout(() => setLine2Visible(true), 600);

          // Start ticking after response visible
          tickId = setInterval(() => {
            const bases = [0.02, -9.81, 0.15];
            const newVals = bases.map((b) => {
              const variance = Math.abs(b) < 1 ? 0.03 : 0.05;
              return (b + (Math.random() - 0.5) * variance).toFixed(2);
            });
            setTickX(newVals[0]);
            setTickY(newVals[1]);
            setTickZ(newVals[2]);
            setChanged(true);
            setTimeout(() => setChanged(false), 200);
          }, 1500);

          obs.disconnect();
        }
      },
      { threshold: 0.3 }
    );

    obs.observe(termEl);
    onCleanup(() => {
      obs.disconnect();
      if (tickId) clearInterval(tickId);
    });
  });

  return (
    <div ref={termEl} class="terminal">
      <div class="terminal__bar">
        <span class="terminal__dot" />
        <span class="terminal__dot" />
        <span class="terminal__dot" />
        <span class="terminal__title">MCP Exchange</span>
      </div>
      <div class="terminal__body">
        <div class={`terminal__line terminal__line--request${line1Visible() ? ' visible' : ''}`}>
          <span class="terminal__arrow">&rarr;</span>
          <span class="terminal__text">
            {'{ "method": "tools/call", "params": { "name": "'}<em>read_accelerometer</em>{'" } }'}
          </span>
        </div>
        <div class={`terminal__line terminal__line--response${line2Visible() ? ' visible' : ''}`}>
          <span class="terminal__arrow">&larr;</span>
          <span class="terminal__text">
            {'{ "result": { "x": '}
            <span class={`tick-value${changed() ? ' changed' : ''}`}>{tickX()}</span>
            {', "y": '}
            <span class={`tick-value${changed() ? ' changed' : ''}`}>{tickY()}</span>
            {', "z": '}
            <span class={`tick-value${changed() ? ' changed' : ''}`}>{tickZ()}</span>
            {' } }'}
          </span>
        </div>
      </div>
    </div>
  );
}
