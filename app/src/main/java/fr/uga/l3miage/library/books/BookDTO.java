package fr.uga.l3miage.library.books;

import fr.uga.l3miage.library.authors.AuthorDTO;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Collection;

import org.springframework.boot.context.properties.bind.DefaultValue;



public record BookDTO(
        @PositiveOrZero
        Long id,
        @NotBlank
        String title,
        @Min(value = 1000000000L)
        @Max(value = 9999999999999L)
        @Digits(integer = 13, fraction = 0)
        long isbn,
        @NotBlank
        String publisher,
        @Min(value = -9999)
        @Max(value = 9999)
        @Digits(integer = 4, fraction = 0)
        short year,
        @DefaultValue("french")
        @Pattern(regexp = "^(french|english)$")
        String language,
        Collection<AuthorDTO> authors
) {
}
