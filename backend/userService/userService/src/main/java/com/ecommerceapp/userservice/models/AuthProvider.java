package com.ecommerceapp.userservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


public enum AuthProvider {
    LOCAL,
    GOOGLE,
    FACEBOOK,
    GITHUB
}
