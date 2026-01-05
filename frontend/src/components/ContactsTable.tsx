import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "./ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "./ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "./ui/table";

import type { ContactDto, PagedModelContactDto } from "@/client/types.gen";

import { getAllContacts, patchContact } from "@/client";
import ContactForm from "./ContactForm";
import DeleteAlertDialog from "./DeleteAlertDialog";

export default function ContactsTable() {
  const [page, setPage] = useState<PagedModelContactDto | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const [editorMode, setEditorMode] = useState<"new" | "edit" | null>(null);
  const [contactToEdit, setContactToEdit] = useState<ContactDto | null>(null);

  const contacts = page?.content ?? [];

  async function loadContacts() {
    const res = await getAllContacts({
      query: {
        page: 0,
        size: 20,
        deleted: false,
      },
    });

    setPage(res.data ?? null);
  }

  useEffect(() => {
    loadContacts();
  }, []);

  function onNew() {
    setContactToEdit(null);
    setEditorMode("new");
  }

  function onEdit() {
    if (selectedId == null) {
      toast.error("Devi selezionare un contatto per procedere");
      return;
    }

    const contact = contacts.find((c) => c.id === selectedId);
    if (!contact) return;

    setContactToEdit(contact);
    setEditorMode("edit");
  }

  async function onDeleteConfirmed() {
    if (selectedId == null) return;

    await patchContact({
      path: { id: selectedId as number },
      body: { deleted: true },
    });

    setSelectedId(null);
    loadContacts();
  }

  return (
    <>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Nome</TableHead>
            <TableHead>Cognome</TableHead>
            <TableHead>Numero di telefono</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {contacts.map((contact) => (
            <TableRow
              key={contact.id}
              onClick={() => setSelectedId(contact.id ?? null)}
              className={
                selectedId === contact.id
                  ? "bg-muted cursor-pointer"
                  : "cursor-pointer"
              }
            >
              <TableCell>{contact.firstName}</TableCell>
              <TableCell>{contact.lastName}</TableCell>
              <TableCell>{contact.phoneNumber}</TableCell>
            </TableRow>
          ))}

          {contacts.length === 0 && (
            <TableRow>
              <TableCell
                colSpan={3}
                className="text-center text-muted-foreground"
              >
                Nessun contatto
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <div className="flex gap-2 justify-end mt-4">
        <Button onClick={onNew}>Nuovo</Button>

        <Button variant="secondary" onClick={onEdit}>
          Modifica
        </Button>

        <DeleteAlertDialog
          disabled={selectedId == null}
          contact={contacts.find((c) => c.id === selectedId)}
          onConfirm={onDeleteConfirmed}
        />
      </div>

      <Dialog
        open={editorMode !== null}
        onOpenChange={() => setEditorMode(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editorMode === "new" ? "Nuovo contatto" : "Modifica contatto"}
            </DialogTitle>
            <DialogDescription>
              Compila il form per{" "}
              {editorMode === "new" ? "creare un nuovo" : "modificare il"}{" "}
              contatto
            </DialogDescription>
          </DialogHeader>

          {editorMode === "new" && (
            <ContactForm
              onSaved={() => {
                setEditorMode(null);
                loadContacts();
              }}
            />
          )}

          {editorMode === "edit" && contactToEdit && (
            <ContactForm
              contact={contactToEdit}
              onSaved={() => {
                setEditorMode(null);
                loadContacts();
              }}
            />
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}
