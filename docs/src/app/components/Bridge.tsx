import FadeIn from "./FadeIn";
import Terminal from "./Terminal";

export default function Bridge() {
  return (
    <section id="bridge" className="section">
      <div className="container">
        <FadeIn>
          <span className="section-label">The Bridge</span>
        </FadeIn>
        <FadeIn delay={80}>
          <h2 className="section-heading">
            One <em>Protocol</em>
          </h2>
        </FadeIn>
        <FadeIn delay={160}>
          <p className="section-intro">
            Connects digital intelligence to physical reality.
          </p>
        </FadeIn>

        <FadeIn delay={240}>
          <div className="arch">
            <div className="arch__node">
              <svg className="arch__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2" />
                <path d="M7 8h4M7 12h8M7 16h6" strokeLinecap="round" />
              </svg>
              <div className="arch__name">AI Agent</div>
              <div className="arch__sub">Claude, GPT, or any MCP client</div>
            </div>

            <div className="arch__connector">
              <div className="arch__line" />
              <div className="arch__proto">MCP</div>
              <div className="arch__line" />
            </div>

            <div className="arch__node">
              <svg className="arch__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="5" y="2" width="14" height="20" rx="3" />
                <circle cx="12" cy="18" r="1" />
              </svg>
              <div className="arch__name">Your Phone</div>
              <div className="arch__sub">Open Modality sensor gateway</div>
            </div>
          </div>
        </FadeIn>

        <FadeIn delay={320}>
          <div style={{ marginTop: 32 }}>
            <Terminal />
          </div>
        </FadeIn>

        <FadeIn delay={400}>
          <div className="features-grid">
            <div className="feature-card">
              <svg className="feature-card__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M12 2L3 7v10l9 5 9-5V7l-9-5z" />
                <path d="M12 22V12M12 12L3 7M12 12l9-5" />
              </svg>
              <h4>No cloud. No middleman.</h4>
              <p>Direct WiFi connection. Your data never leaves your network.</p>
            </div>
            <div className="feature-card">
              <svg className="feature-card__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M4 12h16M8 6l-4 6 4 6M16 6l4 6-4 6" />
              </svg>
              <h4>Streamable HTTP</h4>
              <p>MCP protocol with JSON-RPC 2.0. POST for requests, SSE for streaming.</p>
            </div>
            <div className="feature-card">
              <svg className="feature-card__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="3" y="8" width="8" height="8" rx="1" />
                <rect x="13" y="8" width="8" height="8" rx="1" />
                <path d="M7 8V5a5 5 0 0110 0v3" />
              </svg>
              <h4>Kotlin Multiplatform</h4>
              <p>Shared logic, native performance. Android and iOS from one codebase.</p>
            </div>
          </div>
        </FadeIn>
      </div>
    </section>
  );
}
