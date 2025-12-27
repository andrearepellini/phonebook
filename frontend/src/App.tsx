import { useEffect, useState } from "react";
import "./App.css";
import { helloWorld } from "./client";

function App() {
  const [message, setMessage] = useState<string | null>("Loading...");

  useEffect(() => {
    helloWorld()
      .then((res) => res.data)
      .then((text) => setMessage(text ?? "Failed to load"));
  }, []);

  return <div>{message}</div>;
}

export default App;
