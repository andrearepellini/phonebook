import { verifyUser } from "@/client";
import { useNavigate } from "@tanstack/react-router";
import { REGEXP_ONLY_DIGITS } from "input-otp";
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
import { Field, FieldGroup, FieldLabel } from "./ui/field";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "./ui/input-otp";

type VerificationCodeFormProps = {
  email: string;
};

export default function VerificationCodeForm({
  email,
}: VerificationCodeFormProps) {
  const [verificationCode, setVerificationCode] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (verificationCode.length !== 6) {
      toast.error("Inserisci il codice completo di 6 cifre");
      return;
    }

    const { error } = await verifyUser({
      body: {
        email,
        verificationCode,
      },
    });

    if (error) {
      console.error("Verification failed:", error);
      toast.error(
        typeof error === "string"
          ? error
          : "C'è stato un errore durante la verifica",
      );
      return;
    }

    toast.success("Email verificata, ora puoi accedere");
    navigate({ to: "/login" });
  }

  return (
    <div className="w-full max-w-md mx-auto mt-10">
      <Card>
        <CardHeader>
          <CardTitle>Verifica la tua email</CardTitle>
          <CardDescription>
            Per completare la registrazione, inserisci il codice che abbiamo
            inviato a {email}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel>Codice di verifica</FieldLabel>
                <InputOTP
                  maxLength={6}
                  id="verification-code"
                  required
                  pattern={REGEXP_ONLY_DIGITS}
                  containerClassName="justify-center"
                  value={verificationCode}
                  onChange={setVerificationCode}
                >
                  <InputOTPGroup>
                    <InputOTPSlot index={0} />
                    <InputOTPSlot index={1} />
                    <InputOTPSlot index={2} />
                    <InputOTPSlot index={3} />
                    <InputOTPSlot index={4} />
                    <InputOTPSlot index={5} />
                  </InputOTPGroup>
                </InputOTP>
              </Field>
              <Field>
                <Button type="submit" disabled={verificationCode.length !== 6}>
                  Verifica
                </Button>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
