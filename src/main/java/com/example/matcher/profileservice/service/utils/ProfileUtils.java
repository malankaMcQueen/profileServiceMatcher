package com.example.matcher.profileservice.service.utils;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public final class ProfileUtils {

    private ProfileUtils() {}
    public static boolean isProfileUpdateDTOEmpty(ProfileUpdateDTO profileUpdate) {
        return profileUpdate == null || (
                profileUpdate.getCity() == null &&
                        profileUpdate.getSearchAgeMin() == null &&
                        profileUpdate.getSearchAgeMax() == null &&
//                        profileUpdate.getSearchUniversity() == null &&
//                        profileUpdate.getSearchFaculty() == null &&
                        profileUpdate.getSearchGender() == null &&
                        profileUpdate.getFirstName() == null &&
                        profileUpdate.getDateOfBirth() == null
//                        profileUpdate.getUniversity() == null &&
//                        profileUpdate.getFaculty() == null
        );
    }

    public static <T> boolean updateField(T newValue, Supplier<T> getter, Consumer<T> setter) {
        if (!Objects.equals(newValue, getter.get())) {
            Optional.ofNullable(newValue).ifPresent(setter);
            return true; // Было обновление
        }
        return false; // Обновления не было
    }

}
