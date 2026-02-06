import { createFileRoute, redirect } from "@tanstack/react-router";
import ContactsTable from "../components/ContactsTable";

const isAuthenticated = () => {
  return !!sessionStorage.getItem("token");
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
