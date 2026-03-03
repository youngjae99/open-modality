import LogoSvg from './LogoSvg';

export default function Footer() {
  return (
    <footer class="footer">
      <div class="container">
        <LogoSvg width={80} height={80} class="footer__logo" />
        <p class="footer__text">Open Modality</p>
        <nav class="footer__links" aria-label="Footer navigation">
          <a href="https://github.com/anthropics/open-modality" target="_blank" rel="noopener noreferrer">GitHub</a>
          <a href="https://modelcontextprotocol.io" target="_blank" rel="noopener noreferrer">MCP</a>
        </nav>
      </div>
    </footer>
  );
}
