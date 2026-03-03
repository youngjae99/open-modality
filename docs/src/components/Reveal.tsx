import { createSignal, onMount, onCleanup, type JSX, type ParentComponent } from 'solid-js';

interface RevealProps {
  delay?: number;
  threshold?: number;
  rootMargin?: string;
  class?: string;
  /** Use 'word' for reveal-word style */
  variant?: 'default' | 'word';
}

const Reveal: ParentComponent<RevealProps> = (props) => {
  const [visible, setVisible] = createSignal(false);
  let el: HTMLDivElement | undefined;

  onMount(() => {
    if (!el) return;

    // Check for reduced motion
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reducedMotion) {
      setVisible(true);
      return;
    }

    const obs = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          const delay = props.delay ?? 0;
          if (delay > 0) {
            setTimeout(() => setVisible(true), delay);
          } else {
            setVisible(true);
          }
          obs.disconnect();
        }
      },
      {
        threshold: props.threshold ?? 0.15,
        rootMargin: props.rootMargin ?? '0px 0px -40px 0px',
      }
    );

    obs.observe(el);
    onCleanup(() => obs.disconnect());
  });

  const baseClass = () => {
    const variant = props.variant === 'word' ? 'reveal-word' : 'reveal';
    const vis = visible() ? ' visible' : '';
    const extra = props.class ? ` ${props.class}` : '';
    return `${variant}${vis}${extra}`;
  };

  return (
    <div ref={el} class={baseClass()}>
      {props.children}
    </div>
  );
};

export default Reveal;
