import VerificationCodeForm from "@/components/VerificationCodeForm";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/verification-code")({
  component: RouteComponent,
});

function RouteComponent() {
  return <VerificationCodeForm />;
}
