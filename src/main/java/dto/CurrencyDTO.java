package dto;

public record CurrencyDTO(Long id, String code, String fullName, String sign) {

    @Override
    public String toString() {
        return "CurrencyDTO{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + fullName + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
