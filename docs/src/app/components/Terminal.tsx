"use client";

import { useEffect, useRef, useState } from "react";

export default function Terminal() {
  const [line1, setLine1] = useState(false);
  const [line2, setLine2] = useState(false);
  const [x, setX] = useState("0.02");
  const [y, setY] = useState("-9.81");
  const [z, setZ] = useState("0.15");
  const [changed, setChanged] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    let tickId: ReturnType<typeof setInterval> | null = null;

    const obs = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setTimeout(() => setLine1(true), 0);
          setTimeout(() => setLine2(true), 600);

          tickId = setInterval(() => {
            const bases = [0.02, -9.81, 0.15];
            const vals = bases.map(
              (b) =>
                (b + (Math.random() - 0.5) * (Math.abs(b) < 1 ? 0.03 : 0.05)).toFixed(2)
            );
            setX(vals[0]);
            setY(vals[1]);
            setZ(vals[2]);
            setChanged(true);
            setTimeout(() => setChanged(false), 200);
          }, 1500);

          obs.disconnect();
        }
      },
      { threshold: 0.3 }
    );
    obs.observe(el);

    return () => {
      obs.disconnect();
      if (tickId) clearInterval(tickId);
    };
  }, []);

  return (
    <div ref={ref} className="terminal">
      <div className="terminal__bar">
        <span className="terminal__dot" />
        <span className="terminal__dot" />
        <span className="terminal__dot" />
        <span className="terminal__title">MCP Exchange</span>
      </div>
      <div className="terminal__body">
        <div className={`terminal__line terminal__line--request${line1 ? " visible" : ""}`}>
          <span className="terminal__arrow">&rarr;</span>
          <span className="terminal__text">
            {"{ \"method\": \"tools/call\", \"params\": { \"name\": \""}
            <em>read_accelerometer</em>
            {"\" } }"}
          </span>
        </div>
        <div className={`terminal__line terminal__line--response${line2 ? " visible" : ""}`}>
          <span className="terminal__arrow">&larr;</span>
          <span className="terminal__text">
            {"{ \"result\": { \"x\": "}
            <span className={`tick-value${changed ? " changed" : ""}`}>{x}</span>
            {", \"y\": "}
            <span className={`tick-value${changed ? " changed" : ""}`}>{y}</span>
            {", \"z\": "}
            <span className={`tick-value${changed ? " changed" : ""}`}>{z}</span>
            {" } }"}
          </span>
        </div>
      </div>
    </div>
  );
}
