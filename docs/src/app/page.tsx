import Header from "./components/Header";
import Hero from "./components/Hero";
import Senses from "./components/Senses";
import Bridge from "./components/Bridge";
import Philosophy from "./components/Philosophy";
import GetStarted from "./components/GetStarted";
import Footer from "./components/Footer";

export default function Home() {
  return (
    <>
      <Header />
      <main>
        <Hero />
        <div className="divider" />
        <Senses />
        <div className="divider" />
        <Bridge />
        <Philosophy />
        <GetStarted />
      </main>
      <Footer />
    </>
  );
}
