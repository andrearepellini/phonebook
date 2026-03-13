import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "sonner";
import { Button } from "./ui/button";
import { Field, FieldGroup, FieldLabel, FieldSet } from "./ui/field";
import { Input } from "./ui/input";
import { PhoneInput } from "./ui/phone-input";

import { createContact, patchContact } from "@/client";
import type { ContactResponse } from "@/client/types.gen";

interface ContactFormProps {
  contact?: ContactResponse;
  onSaved: () => void;
}

export default function ContactForm({ contact, onSaved }: ContactFormProps) {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [age, setAge] = useState<number | "">("");
  const { t } = useTranslation();

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

    if (contact && contact.id) {
      const { error } = await patchContact({
        path: { id: contact.id },
        body: {
          firstName,
          lastName,
          phoneNumber,
          address,
          age: age === "" ? undefined : Number(age),
        },
      });

      if (error) {
        console.error(error);
        toast.error(t("contacts.updateError"));
        return;
      }

      toast.success(t("contacts.updated"));
    } else {
      const { error } = await createContact({
        body: {
          firstName,
          lastName,
          phoneNumber,
          address,
          age: age === "" ? undefined : Number(age),
        },
      });

      if (error) {
        console.error(error);
        toast.error(t("contacts.createError"));
        return;
      }

      toast.success(t("contacts.created"));
    }
    onSaved();
  }

  return (
    <form onSubmit={handleSubmit}>
      <FieldSet>
        <FieldGroup>
          <Field orientation="horizontal">
            <FieldLabel>{t("contacts.firstName")}</FieldLabel>
            <Input
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>{t("contacts.lastName")}</FieldLabel>
            <Input
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              required
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>{t("contacts.phoneNumber")}</FieldLabel>
            <PhoneInput
              defaultCountry="IT"
              international={false}
              value={phoneNumber}
              onChange={(value) => setPhoneNumber(value ?? "")}
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>{t("contacts.address")}</FieldLabel>
            <Input
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>{t("contacts.age")}</FieldLabel>
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
        <Button type="submit">{t("contacts.save")}</Button>
      </FieldSet>
    </form>
  );
}
