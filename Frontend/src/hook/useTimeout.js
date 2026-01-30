import { useState, useEffect } from "react";

function useTimeout() {
  const [secondsLeft, setSecondsLeft] = useState(null);

  useEffect(() => {
    if (secondsLeft === null || secondsLeft <= 0) return;

    const timer = setTimeout(() => {
      setSecondsLeft((prev) => prev - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [secondsLeft]);

  return { secondsLeft, setSecondsLeft };
}

export default useTimeout;
