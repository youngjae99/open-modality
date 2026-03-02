import Reveal from './Reveal';

export default function Philosophy() {
  return (
    <section id="philosophy" class="philosophy">
      <div class="container">
        <div class="philosophy__text">
          <Reveal variant="word">
            <p class="philosophy__line">
              For all their intelligence, AI models have been blind, deaf, and numb to the physical world.
            </p>
          </Reveal>
          <Reveal variant="word" delay={200}>
            <p class="philosophy__line">
              They reason about temperature but have never felt warmth.
            </p>
          </Reveal>
          <Reveal variant="word" delay={400}>
            <p class="philosophy__line">
              They describe landscapes but have never seen one.
            </p>
          </Reveal>
          <Reveal variant="word" delay={600}>
            <p class="philosophy__line" lang="ko">
              Open Modality는 디지털 지능과 물리적 현실 사이의 다리다.
            </p>
          </Reveal>
          <Reveal variant="word" delay={800}>
            <p class="philosophy__line philosophy__line--accent">
              Every phone becomes a sensory organ. Every sensor becomes a tool.
            </p>
          </Reveal>
        </div>
      </div>
    </section>
  );
}
