import Reveal from './Reveal';
import PhoneMockup from './PhoneMockup';
import { useSensors } from '../hooks/useSensors';

export default function FeelIt() {
  const { values, isLive, buttonText, changedKey, enableLive } = useSensors();

  return (
    <section id="feel" class="feel">
      <div class="container">
        <Reveal><span class="section-label">Feel It</span></Reveal>
        <Reveal><h2 class="section-heading">Touch the <em>Data</em></h2></Reveal>
        <Reveal><p class="section-intro">Your device is already a sensor gateway. Experience it.</p></Reveal>

        <Reveal>
          <div class="sensor-demo">
            <PhoneMockup values={values} isLive={isLive} changedKey={changedKey} />

            <div class="sensor-demo__prompt">
              <button
                class={`btn btn--sense${isLive() ? ' active' : ''}`}
                id="enable-sensors"
                onClick={enableLive}
              >
                <span>{buttonText()}</span>
              </button>
              <p class="sensor-demo__note">Uses your device's Web APIs. No data is collected or sent anywhere.</p>
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}
