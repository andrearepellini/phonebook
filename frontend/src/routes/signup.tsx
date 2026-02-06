import { createFileRoute } from "@tanstack/react-router";
import SignupForm from "../components/SignupForm";

export const Route = createFileRoute("/signup")({
  component: Signup,
});

function Signup() {
  return <SignupForm />;
}
