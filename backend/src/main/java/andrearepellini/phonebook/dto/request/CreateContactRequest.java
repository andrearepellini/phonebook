package andrearepellini.phonebook.dto.request;

public record CreateContactRequest(
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        Integer age) {
}
