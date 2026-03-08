import { getCurrentUser } from "@/lib/auth";
import { createFileRoute, redirect } from "@tanstack/react-router";
import ContactsTable from "../components/ContactsTable";

export const Route = createFileRoute("/")({
  beforeLoad: async () => {
    const user = await getCurrentUser();
    if (!user) {
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
