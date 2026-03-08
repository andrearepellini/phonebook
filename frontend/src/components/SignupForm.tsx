import { registerUser } from "@/client";
import { Link, useNavigate } from "@tanstack/react-router";
import { useState, type FormEvent } from "react";
import { toast } from "sonner";
import { Button } from "./ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "./ui/card";
import { Field, FieldDescription, FieldGroup, FieldLabel } from "./ui/field";
import { Input } from "./ui/input";

export default function SignupForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (password.length < 8) {
      setPasswordError("La password deve contenere almeno 8 caratteri");
      return;
    }
    setPasswordError("");

    const { error } = await registerUser({
      body: {
        email,
        password,
      },
    });

    if (error) {
      console.error("Signup failed:", error);
      toast.error("C'è stato un errore nella registrazione");
      return;
    }

    toast.success("Registrazione completata");

    navigate({ to: "/login" });
  }

  return (
    <div className="w-full max-w-md mx-auto mt-10">
      <Card>
        <CardHeader>
          <CardTitle>Registrati</CardTitle>
          <CardDescription>Compila il form per registrarti</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="email">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </Field>
              <Field>
                <FieldLabel htmlFor="password">Password</FieldLabel>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => {
                    const nextPassword = e.target.value;
                    setPassword(nextPassword);
                    if (nextPassword.length >= 8) {
                      setPasswordError("");
                    }
                  }}
                  required
                />
                {passwordError && (
                  <p className="text-sm text-red-500">{passwordError}</p>
                )}
              </Field>
              <Field>
                <Button type="submit">Registrati</Button>
                <FieldDescription className="text-center">
                  Hai già un account?{" "}
                  <Link to="/login" className="underline">
                    Accedi
                  </Link>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
