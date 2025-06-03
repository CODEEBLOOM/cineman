package com.codebloom.cineman.model.Id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DetailBookingSnackId implements Serializable {
    private Long invoiceId;
    private Integer snackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailBookingSnackId)) return false;
        DetailBookingSnackId that = (DetailBookingSnackId) o;
        return Objects.equals(invoiceId, that.invoiceId) &&
                Objects.equals(snackId, that.snackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, snackId);
    }
}