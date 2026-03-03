import { type Accessor } from 'solid-js';
import { useTilt } from '../hooks/useTilt';

interface SensorValues {
  accelX: string;
  accelY: string;
  accelZ: string;
  gyroAlpha: string;
  gyroBeta: string;
  gyroGamma: string;
  geoLat: string;
  geoLon: string;
}

interface PhoneMockupProps {
  values: Accessor<SensorValues>;
  isLive: Accessor<boolean>;
  changedKey: Accessor<string | null>;
}

export default function PhoneMockup(props: PhoneMockupProps) {
  let mockupEl: HTMLDivElement | undefined;
  const transform = useTilt(() => mockupEl);

  const valClass = (key: string) =>
    `sensor-val${props.changedKey() === key ? ' active' : ''}`;

  return (
    <div class="phone-mockup" id="phone-mockup" ref={mockupEl}>
      <div class="phone-mockup__frame" style={{ transform: transform() }}>
        <div class="phone-mockup__screen">
          <div class="phone-mockup__island" />
          <div class="sensor-readout">
            <div class="readout-group">
              <h5>Accelerometer</h5>
              <div class="readout-values">
                <span>x: <span class={valClass('accelX')}>{props.values().accelX}</span></span>
                <span>y: <span class={valClass('accelY')}>{props.values().accelY}</span></span>
                <span>z: <span class={valClass('accelZ')}>{props.values().accelZ}</span></span>
              </div>
            </div>
            <div class="readout-group">
              <h5>Gyroscope</h5>
              <div class="readout-values">
                <span>&alpha;: <span class={valClass('gyroAlpha')}>{props.values().gyroAlpha}</span></span>
                <span>&beta;: <span class={valClass('gyroBeta')}>{props.values().gyroBeta}</span></span>
                <span>&gamma;: <span class={valClass('gyroGamma')}>{props.values().gyroGamma}</span></span>
              </div>
            </div>
            <div class="readout-group">
              <h5>Location</h5>
              <div class="readout-values readout-values--geo">
                <span>lat: <span class={valClass('geoLat')}>{props.values().geoLat}</span></span>
                <span>lon: <span class={valClass('geoLon')}>{props.values().geoLon}</span></span>
              </div>
            </div>
          </div>
          <div class={`sensor-status${props.isLive() ? ' sensor-status--live' : ''}`}>
            <span class="sensor-status__dot" />
            <span class="sensor-status__text">{props.isLive() ? 'Live' : 'Simulated'}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
