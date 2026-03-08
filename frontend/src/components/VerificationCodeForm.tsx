import { REGEXP_ONLY_DIGITS } from "input-otp";
import type { FormEvent } from "react";
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

export default function VerificationCodeForm() {
  function handleSubmit(e: FormEvent<HTMLFormElement>): void {
    throw new Error("Function not implemented.");
  }

  return (
    <div className="w-full max-w-md mx-auto mt-10">
      <Card>
        <CardHeader>
          <CardTitle>Verifica la tua email</CardTitle>
          <CardDescription>
            Per completare la tua registrazione, inserisci il codice che abbiamo
            inviato alla tua casella di posta
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
                <Button type="submit">Verifica</Button>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
