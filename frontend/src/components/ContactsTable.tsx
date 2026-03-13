import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
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

import type {
  ContactResponse,
  PagedModelContactResponse,
} from "@/client/types.gen";

import { getAllContacts, patchContact } from "@/client";
import ContactForm from "./ContactForm";
import DeleteAlertDialog from "./DeleteAlertDialog";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "./ui/pagination";

export default function ContactsTable() {
  const [page, setPage] = useState<PagedModelContactResponse | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [pageNumber, setPageNumber] = useState<number>(0);
  const { t } = useTranslation();

  const [editorMode, setEditorMode] = useState<"new" | "edit" | null>(null);
  const [contactToEdit, setContactToEdit] = useState<ContactResponse | null>(
    null,
  );

  const contacts = page?.content ?? [];

  async function loadContacts() {
    const res = await getAllContacts({
      query: {
        page: pageNumber,
        size: 20,
        deleted: false,
      },
    });

    setPage(res.data ?? null);
  }

  useEffect(() => {
    loadContacts();
  }, [pageNumber]);

  function onNew() {
    setContactToEdit(null);
    setEditorMode("new");
  }

  function onEdit() {
    if (selectedId == null) {
      toast.error(t("contacts.selectContact"));
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
            <TableHead>{t("contacts.firstName")}</TableHead>
            <TableHead>{t("contacts.lastName")}</TableHead>
            <TableHead>{t("contacts.phoneNumber")}</TableHead>
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
                {t("contacts.empty")}
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <div className="flex items-center justify-between mt-4 px-2">
        <Pagination className="w-auto mx-0">
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => setPageNumber((prev) => prev - 1)}
                className={
                  pageNumber === 0
                    ? "pointer-events-none opacity-50"
                    : undefined
                }
              />
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                onClick={() => setPageNumber((prev) => prev + 1)}
                className={
                  pageNumber >= (page?.page?.totalPages ?? 1) - 1
                    ? "pointer-events-none opacity-50"
                    : undefined
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>

        <div className="flex gap-2">
          <Button onClick={onNew}>{t("contacts.new")}</Button>

          <Button variant="secondary" onClick={onEdit}>
            {t("contacts.edit")}
          </Button>

          <DeleteAlertDialog
            disabled={selectedId == null}
            contact={contacts.find((c) => c.id === selectedId)}
            onConfirm={onDeleteConfirmed}
          />
        </div>
      </div>

      <Dialog
        open={editorMode !== null}
        onOpenChange={() => setEditorMode(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editorMode === "new"
                ? t("contacts.newTitle")
                : t("contacts.editTitle")}
            </DialogTitle>
            <DialogDescription>
              {editorMode === "new"
                ? t("contacts.newDescription")
                : t("contacts.editDescription")}
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
