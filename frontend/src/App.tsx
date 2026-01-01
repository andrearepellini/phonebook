import "./App.css";
import ContactsTable from "./components/ContactsTable";
import { Toaster } from "./components/ui/sonner";

function App() {
  return (
    <>
      <ContactsTable />
      <Toaster />
    </>
  );
}

export default App;
