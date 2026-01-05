import "./App.css";
import ContactsTable from "./components/ContactsTable";
import { Toaster } from "./components/ui/sonner";

function App() {
  return (
    <>
      <ContactsTable />
      <Toaster position="top-center" />
    </>
  );
}

export default App;
