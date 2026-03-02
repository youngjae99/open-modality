import './App.css';
import Hero from './components/Hero';
import Senses from './components/Senses';
import Bridge from './components/Bridge';
import FeelIt from './components/FeelIt';
import Philosophy from './components/Philosophy';
import GetStarted from './components/GetStarted';
import Footer from './components/Footer';

export default function App() {
  return (
    <>
      <main>
        <Hero />
        <Senses />
        <Bridge />
        <FeelIt />
        <Philosophy />
        <GetStarted />
      </main>
      <Footer />
    </>
  );
}
