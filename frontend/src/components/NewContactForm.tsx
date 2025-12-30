import {
  Field,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSet,
} from "./ui/field";
import { Input } from "./ui/input";
import { PhoneInput } from "./ui/phone-input";

export default function NewContactForm() {
  return (
    <form action="">
      <FieldSet>
        <FieldLegend>Nuovo contatto</FieldLegend>
        <FieldGroup>
          <Field orientation="horizontal">
            <FieldLabel>Nome</FieldLabel>
            <Input placeholder="Mario" />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Cognome</FieldLabel>
            <Input placeholder="Rossi" />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Numero di telefono</FieldLabel>
            <PhoneInput defaultCountry="IT" international={false} />
          </Field>
          <Field orientation="horizontal">
            <FieldLabel>Età</FieldLabel>
            <Input type="number" min={0} step={1} inputMode="numeric" />
          </Field>
        </FieldGroup>
      </FieldSet>
    </form>
  );
}
