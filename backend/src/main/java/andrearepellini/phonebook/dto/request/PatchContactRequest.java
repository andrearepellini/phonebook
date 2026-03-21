package andrearepellini.phonebook.dto.request;

public record PatchContactRequest(
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        Integer age,
        Boolean deleted) {
}
