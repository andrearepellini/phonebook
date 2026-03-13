import type { ContactResponse } from "@/client";
import { useTranslation } from "react-i18next";
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
  const { t } = useTranslation();
  const fullName = [contact?.firstName, contact?.lastName]
    .filter(Boolean)
    .join(" ");

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="destructive"
          onClick={(e) => {
            if (disabled) {
              e.preventDefault();
              toast.error(t("contacts.selectContact"));
            }
          }}
        >
          {t("contacts.delete")}
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            {t("deleteDialog.title", { name: fullName })}
          </AlertDialogTitle>
          <AlertDialogDescription>
            {t("deleteDialog.description")}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t("deleteDialog.cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm}>
            {t("deleteDialog.confirm")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
