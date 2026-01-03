import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "./ui/button";
import { Field, FieldGroup, FieldLabel, FieldSet } from "./ui/field";
import { Input } from "./ui/input";
import { PhoneInput } from "./ui/phone-input";

import { createContact, patchContact } from "@/client";
import type { ContactDto } from "@/client/types.gen";

interface ContactFormProps {
  contact?: ContactDto;
  onSaved: () => void;
}

export default function ContactForm({ contact, onSaved }: ContactFormProps) {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [age, setAge] = useState<number | "">("");

  useEffect(() => {
    if (contact) {
      setFirstName(contact.firstName ?? "");
      setLastName(contact.lastName ?? "");
      setPhoneNumber(contact.phoneNumber ?? "");
      setAddress(contact.address ?? "");
      setAge(contact.age ?? "");
    } else {
      setFirstName("");
      setLastName("");
      setPhoneNumber("");
      setAddress("");
      setAge("");
    }
  }, [contact]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    try {
      if (contact && contact.id) {
        await patchContact({
          path: { id: contact.id },
          body: {
            firstName,
            lastName,
            phoneNumber,
            address,
            age: age === "" ? undefined : Number(age),
          },
        });
        toast.success("Contatto aggiornato");
      } else {
        await createContact({
          body: {
            firstName,
            lastName,
            phoneNumber,
            address,
            age: age === "" ? undefined : Number(age),
          },
        });
        toast.success("Contatto creato");
      }
      onSaved();
    } catch (error) {
      console.error(error);
      toast.error("Errore durante il salvataggio");
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <FieldSet>
        <FieldGroup>
          <Field orientation="horizontal">
            <FieldLabel>Nome</FieldLabel>
            <Input
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Cognome</FieldLabel>
            <Input
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              required
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Numero di telefono</FieldLabel>
            <PhoneInput
              defaultCountry="IT"
              international={false}
              value={phoneNumber}
              onChange={(value) => setPhoneNumber(value ?? "")}
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Indirizzo</FieldLabel>
            <Input
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Età</FieldLabel>
            <Input
              type="number"
              min={0}
              step={1}
              inputMode="numeric"
              value={age}
              onChange={(e) =>
                setAge(e.target.value === "" ? "" : Number(e.target.value))
              }
            />
          </Field>
        </FieldGroup>
        <Button type="submit">Salva</Button>
      </FieldSet>
    </form>
  );
}
