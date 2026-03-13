import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
  it: {
    translation: {
      app: {
        title: "Rubrica",
      },
      login: {
        title: "Accedi al tuo account",
        description: "Inserisci le tue credenziali per accedere",
        submit: "Accedi",
        authFailed: "Autenticazione fallita, riprovare",
        noAccount: "Non hai un account?",
        signup: "Registrati",
      },
      signup: {
        title: "Registrati",
        description: "Compila il form per registrarti",
        submit: "Registrati",
        passwordMinLength: "La password deve contenere almeno 8 caratteri",
        error: "C'e stato un errore nella registrazione",
        success: "Registrazione completata, controlla la tua email",
        alreadyHaveAccount: "Hai gia un account?",
        login: "Accedi",
      },
      verification: {
        title: "Verifica la tua email",
        description:
          "Per completare la registrazione, inserisci il codice che abbiamo inviato a {{email}}",
        codeLabel: "Codice di verifica",
        submit: "Verifica",
        incompleteCode: "Inserisci il codice completo di 6 cifre",
        error: "C'e stato un errore durante la verifica",
        success: "Email verificata, ora puoi accedere",
      },
      contacts: {
        firstName: "Nome",
        lastName: "Cognome",
        phoneNumber: "Numero di telefono",
        address: "Indirizzo",
        age: "Eta",
        empty: "Nessun contatto",
        selectContact: "Devi selezionare un contatto per procedere",
        new: "Nuovo",
        edit: "Modifica",
        delete: "Elimina",
        newTitle: "Nuovo contatto",
        editTitle: "Modifica contatto",
        newDescription: "Compila il form per creare un nuovo contatto",
        editDescription: "Compila il form per modificare il contatto",
        save: "Salva",
        createError: "Errore nella creazione del contatto",
        updateError: "Errore nella modifica del contatto",
        created: "Contatto creato",
        updated: "Contatto aggiornato",
      },
      deleteDialog: {
        title: "Eliminare la persona {{name}}?",
        description: "Questa azione non e reversibile",
        cancel: "No",
        confirm: "Si",
      },
      fields: {
        email: "Email",
        password: "Password",
      },
      pagination: {
        navigation: "Paginazione",
        previous: "Precedente",
        next: "Successiva",
        goToPrevious: "Vai alla pagina precedente",
        goToNext: "Vai alla pagina successiva",
        morePages: "Altre pagine",
      },
      phoneInput: {
        searchCountry: "Cerca paese...",
        noCountryFound: "Nessun paese trovato.",
      },
      dialog: {
        close: "Chiudi",
      },
      command: {
        title: "Palette dei comandi",
        description: "Cerca un comando da eseguire...",
      },
    },
  },
  en: {
    translation: {
      app: {
        title: "Phonebook",
      },
      login: {
        title: "Sign in to your account",
        description: "Enter your credentials to continue",
        submit: "Sign in",
        authFailed: "Authentication failed, please try again",
        noAccount: "Don't have an account?",
        signup: "Sign up",
      },
      signup: {
        title: "Sign up",
        description: "Fill out the form to create your account",
        submit: "Sign up",
        passwordMinLength: "Password must be at least 8 characters long",
        error: "There was an error while signing up",
        success: "Registration completed, check your email",
        alreadyHaveAccount: "Already have an account?",
        login: "Sign in",
      },
      verification: {
        title: "Verify your email",
        description:
          "To complete registration, enter the code we sent to {{email}}",
        codeLabel: "Verification code",
        submit: "Verify",
        incompleteCode: "Enter the full 6-digit code",
        error: "There was an error during verification",
        success: "Email verified, you can now sign in",
      },
      contacts: {
        firstName: "First name",
        lastName: "Last name",
        phoneNumber: "Phone number",
        address: "Address",
        age: "Age",
        empty: "No contacts",
        selectContact: "Select a contact to continue",
        new: "New",
        edit: "Edit",
        delete: "Delete",
        newTitle: "New contact",
        editTitle: "Edit contact",
        newDescription: "Fill out the form to create a new contact",
        editDescription: "Fill out the form to update the contact",
        save: "Save",
        createError: "Error creating contact",
        updateError: "Error updating contact",
        created: "Contact created",
        updated: "Contact updated",
      },
      deleteDialog: {
        title: "Delete {{name}}?",
        description: "This action cannot be undone",
        cancel: "No",
        confirm: "Yes",
      },
      fields: {
        email: "Email",
        password: "Password",
      },
      pagination: {
        navigation: "Pagination",
        previous: "Previous",
        next: "Next",
        goToPrevious: "Go to previous page",
        goToNext: "Go to next page",
        morePages: "More pages",
      },
      phoneInput: {
        searchCountry: "Search country...",
        noCountryFound: "No country found.",
      },
      dialog: {
        close: "Close",
      },
      command: {
        title: "Command palette",
        description: "Search for a command to run...",
      },
    },
  },
} as const;

function getBrowserLanguage() {
  if (typeof window === "undefined") {
    return "en";
  }

  return window.navigator.language.toLowerCase().startsWith("it") ? "it" : "en";
}

if (!i18n.isInitialized) {
  void i18n.use(initReactI18next).init({
    resources,
    lng: getBrowserLanguage(),
    fallbackLng: "en",
    supportedLngs: ["it", "en"],
    interpolation: {
      escapeValue: false,
    },
  });

  if (typeof window !== "undefined") {
    const syncDocumentMetadata = (language: string) => {
      document.documentElement.lang = language;
      document.title = i18n.t("app.title", { lng: language });
    };

    syncDocumentMetadata(i18n.language);
    i18n.on("languageChanged", syncDocumentMetadata);
  }
}

export default i18n;
