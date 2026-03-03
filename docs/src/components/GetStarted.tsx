import { createSignal } from 'solid-js';
import Reveal from './Reveal';

export default function GetStarted() {
  const [copied, setCopied] = createSignal(false);

  const configCode = `{
  "mcpServers": {
    "open-modality": {
      "url": "http://<phone-ip>:8080/mcp"
    }
  }
}`;

  async function copyConfig() {
    try {
      await navigator.clipboard.writeText(configCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch {
      // Silent fail
    }
  }

  return (
    <section id="start" class="start">
      <div class="container">
        <Reveal><span class="section-label">Get Started</span></Reveal>
        <Reveal><h2 class="section-heading">Three <em>Steps</em></h2></Reveal>
        <Reveal><p class="section-intro">Your AI can see, hear, and feel.</p></Reveal>

        <Reveal>
          <div class="quickstart">
            <div class="quickstart__steps">
              <div class="step">
                <span class="step__num">1</span>
                <div class="step__content">
                  <h4>Install the app</h4>
                  <p>On your Android or iOS device.</p>
                </div>
              </div>
              <div class="step">
                <span class="step__num">2</span>
                <div class="step__content">
                  <h4>Tap Start</h4>
                  <p>Launch the MCP server on your phone.</p>
                </div>
              </div>
              <div class="step">
                <span class="step__num">3</span>
                <div class="step__content">
                  <h4>Add to your MCP client</h4>
                  <p>Paste the config and connect.</p>
                </div>
              </div>
            </div>

            <div class="quickstart__config">
              <div class="code-block">
                <div class="code-block__bar">
                  <span>mcp-config.json</span>
                  <button
                    class={`code-block__copy${copied() ? ' copied' : ''}`}
                    onClick={copyConfig}
                    aria-label="Copy to clipboard"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="16" height="16">
                      <rect x="9" y="9" width="13" height="13" rx="2" />
                      <path d="M5 15H4a2 2 0 01-2-2V4a2 2 0 012-2h9a2 2 0 012 2v1" />
                    </svg>
                  </button>
                </div>
                <pre><code>{configCode}</code></pre>
              </div>
            </div>
          </div>
        </Reveal>

        <Reveal>
          <div class="cta-buttons">
            <a href="https://github.com/anthropics/open-modality" class="btn btn--primary" target="_blank" rel="noopener">
              <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
              </svg>
              View on GitHub
            </a>
          </div>
        </Reveal>

        <Reveal><p class="start__license">MIT License. Open source. No tracking. No cloud.</p></Reveal>
      </div>
    </section>
  );
}
