import type { ContactResponse } from "@/client";
import { toast } from "sonner";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "./ui/alert-dialog";
import { Button } from "./ui/button";

interface DeleteAlertDialogProps {
  disabled?: boolean;
  contact?: ContactResponse;
  onConfirm: () => void;
}

export default function DeleteAlertDialog({
  disabled,
  contact,
  onConfirm,
}: DeleteAlertDialogProps) {
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="destructive"
          onClick={(e) => {
            if (disabled) {
              e.preventDefault();
              toast.error("Devi selezionare un contatto per procedere");
            }
          }}
        >
          Elimina
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Eliminare la persona {contact?.firstName} {contact?.lastName}?
          </AlertDialogTitle>
          <AlertDialogDescription>
            Questa azione non è reversibile
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>No</AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm}>Sì</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
