import VerificationCodeForm from "@/components/VerificationCodeForm";
import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/verification-code")({
  validateSearch: (search: Record<string, unknown>) => ({
    email: typeof search.email === "string" ? search.email : "",
  }),
  beforeLoad: ({ search }) => {
    if (!search.email.trim()) {
      throw redirect({ to: "/signup" });
    }
  },
  component: RouteComponent,
});

function RouteComponent() {
  const { email } = Route.useSearch();

  return <VerificationCodeForm email={email} />;
}
