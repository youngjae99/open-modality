import FadeIn from "./FadeIn";

const lines = [
  {
    text: "For all their intelligence, AI models have been blind, deaf, and numb to the physical world.",
  },
  {
    text: "They reason about temperature but have never felt warmth.",
  },
  {
    text: "They describe landscapes but have never seen one.",
  },
  {
    text: "Open Modality\uB294 \uB514\uC9C0\uD138 \uC9C0\uB2A5\uACFC \uBB3C\uB9AC\uC801 \uD604\uC2E4 \uC0AC\uC774\uC758 \uB2E4\uB9AC\uB2E4.",
    className: "philosophy__line--ko",
    lang: "ko",
  },
  {
    text: "Every phone becomes a sensory organ. Every sensor becomes a tool.",
    className: "philosophy__line--accent",
  },
];

export default function Philosophy() {
  return (
    <section id="philosophy" className="philosophy">
      <div className="container">
        <div className="philosophy__lines">
          {lines.map((line, i) => (
            <FadeIn key={i} delay={i * 120}>
              <p
                className={`philosophy__line ${line.className ?? ""}`}
                lang={line.lang}
              >
                {line.text}
              </p>
            </FadeIn>
          ))}
        </div>
      </div>
    </section>
  );
}
