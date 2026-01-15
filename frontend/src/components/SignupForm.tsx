import { Link } from "@tanstack/react-router";
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
  return (
    <div className="w-full max-w-md mx-auto mt-10">
      <Card>
        <CardHeader>
          <CardTitle>Registrati</CardTitle>
          <CardDescription>Compila il form per registrarti</CardDescription>
        </CardHeader>
        <CardContent>
          <form>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="email">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  // placeholder="m@example.com"
                  required
                />
              </Field>
              <Field>
                <FieldLabel htmlFor="password">Password</FieldLabel>
                <Input
                  id="password1"
                  type="password"
                  // placeholder="m@example.com"
                  required
                />
                <FieldDescription>Ripeti la password</FieldDescription>
                <Input
                  id="password2"
                  type="password"
                  // placeholder="m@example.com"
                  required
                />
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
