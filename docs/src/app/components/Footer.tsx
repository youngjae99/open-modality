import Logo from "./Logo";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer__inner">
          <div className="footer__brand">
            <Logo size={24} />
            <span>Open Modality</span>
          </div>
          <nav className="footer__links">
            <a
              href="https://github.com/anthropics/open-modality"
              target="_blank"
              rel="noopener noreferrer"
            >
              GitHub
            </a>
            <a
              href="https://modelcontextprotocol.io"
              target="_blank"
              rel="noopener noreferrer"
            >
              MCP
            </a>
            <a
              href="https://www.anthropic.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              Anthropic
            </a>
          </nav>
        </div>
      </div>
    </footer>
  );
}
