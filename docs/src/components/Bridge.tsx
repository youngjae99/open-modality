import Reveal from './Reveal';
import Terminal from './Terminal';

export default function Bridge() {
  return (
    <section id="bridge" class="bridge">
      <div class="container">
        <Reveal><span class="section-label">The Bridge</span></Reveal>
        <Reveal><h2 class="section-heading">One <em>Protocol</em></h2></Reveal>
        <Reveal><p class="section-intro">Connects digital intelligence to physical reality.</p></Reveal>

        <Reveal>
          <div class="architecture">
            <div class="arch-col arch-col--agent">
              <div class="arch-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <rect x="3" y="3" width="18" height="18" rx="2" />
                  <path d="M7 8h4M7 12h8M7 16h6" stroke-linecap="round" />
                </svg>
              </div>
              <h3>AI Agent</h3>
              <p>Claude Code, Desktop,<br />or any MCP client</p>
            </div>

            <div class="arch-col arch-col--protocol">
              <div class="arch-flow">
                <div class="arch-flow__line" />
                <span class="arch-flow__label">MCP</span>
                <div class="arch-flow__line" />
              </div>
              <p class="arch-proto">Streamable HTTP<br />JSON-RPC 2.0</p>
            </div>

            <div class="arch-col arch-col--phone">
              <div class="arch-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <rect x="5" y="2" width="14" height="20" rx="3" />
                  <circle cx="12" cy="18" r="1" />
                </svg>
              </div>
              <h3>Your Phone</h3>
              <p>Open Modality<br />sensor gateway</p>
            </div>
          </div>
        </Reveal>

        <Reveal>
          <Terminal />
        </Reveal>

        <Reveal>
          <div class="features">
            <div class="feature-card">
              <div class="feature-card__icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M12 2L3 7v10l9 5 9-5V7l-9-5z" />
                  <path d="M12 22V12M12 12L3 7M12 12l9-5" />
                </svg>
              </div>
              <h4>No cloud. No middleman.</h4>
              <p>Direct WiFi connection. Your data never leaves your network.</p>
            </div>
            <div class="feature-card">
              <div class="feature-card__icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M4 12h16M8 6l-4 6 4 6M16 6l4 6-4 6" />
                </svg>
              </div>
              <h4>Streamable HTTP</h4>
              <p>MCP protocol with JSON-RPC 2.0. POST for requests, SSE for streaming.</p>
            </div>
            <div class="feature-card">
              <div class="feature-card__icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <rect x="3" y="8" width="8" height="8" rx="1" />
                  <rect x="13" y="8" width="8" height="8" rx="1" />
                  <path d="M7 8V5a5 5 0 0110 0v3" />
                </svg>
              </div>
              <h4>Kotlin Multiplatform</h4>
              <p>Shared logic, native performance. Android and iOS from one codebase.</p>
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}
