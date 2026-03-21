package andrearepellini.phonebook.dto.response;

public record ContactResponse(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        Integer age,
        Boolean deleted) {
}
