import { createFileRoute, redirect } from "@tanstack/react-router";
import ContactsTable from "../components/ContactsTable";

// Simple auth check - you can replace this with your own logic
const isAuthenticated = () => {
  // Check for auth token in localStorage, cookies, etc.
  return !!localStorage.getItem("token");
};

export const Route = createFileRoute("/")({
  beforeLoad: () => {
    if (!isAuthenticated()) {
      throw redirect({
        to: "/login",
      });
    }
  },
  component: Index,
});

function Index() {
  return <ContactsTable />;
}
