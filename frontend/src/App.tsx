import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [message, setMessage] = useState("Loading...");

  useEffect(() => {
    fetch("/api/helloworld")
      .then((res) => res.text())
      .then((text) => setMessage(text))
      .catch(() => setMessage("Failed to load"));
  }, []);

  return <div>{message}</div>;
}

export default App;
